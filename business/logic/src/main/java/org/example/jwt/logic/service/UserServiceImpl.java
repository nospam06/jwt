package org.example.jwt.logic.service;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.dto.UserDto;
import org.example.jwt.dto.UserLoginRequest;
import org.example.jwt.dto.UserLoginResponse;
import org.example.jwt.dto.UserRequest;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.logic.UserException;
import org.example.jwt.logic.api.UserService;
import org.example.jwt.security.SecurityTokenException;
import org.example.jwt.security.api.TokenService;
import org.example.jwt.nosql.api.NoSqlRepository;
import org.example.jwt.nosql.query.QueryParameter;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final TokenService tokenService;
    private final NoSqlRepository repository;

    @Override
    public OnetimeTokenDto createOnetimeToken(String email) {
        OnetimeTokenDto onetimeTokenDto = new OnetimeTokenDto();
        onetimeTokenDto.setEmail(email);
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(TimeUnit.HOURS.toSeconds(1));
        String uuid = UUID.randomUUID().toString();
        onetimeTokenDto.setToken(uuid);
        onetimeTokenDto.setCreateDate(now);
        onetimeTokenDto.setExpirationDate(expiration);
        repository.insert(onetimeTokenDto);
        return onetimeTokenDto;
    }

    @Override
    public UserLoginResponse createUser(UserRequest request) {
        // validation
        OnetimeTokenDto onetimeTokenDto = validationUserRequest(request);
        onetimeTokenDto.setUsedDate(Instant.now());
        repository.save(onetimeTokenDto);
        UserDto userDto = createUserDto(request);
        repository.save(userDto);
        UserTokenDto userTokenDto = createUserToken(userDto.getUuid());
        String newToken = tokenService.newToken(userTokenDto);
        userTokenDto.setToken(newToken);
        Instant now = Instant.now();
        String uuid = UUID.randomUUID().toString();
        userTokenDto.setUuid(uuid);
        userTokenDto.setCreateDate(now);
        repository.save(userTokenDto);
        return createResponse(newToken);
    }

    private UserDto createUserDto(UserRequest request) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(request, userDto); Instant now = Instant.now();
        String uuid = UUID.randomUUID().toString();
        userDto.setUuid(uuid);
        userDto.setCreateDate(now);
        userDto.setUpdateDate(now);
        String sha = tokenService.sign(request.getPassword());
        userDto.setPasswordSha(sha);
        return userDto;
    }

    public UserTokenDto createUserToken(String userUuid) {
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setUuid(UUID.randomUUID().toString());
        userTokenDto.setUserUuid(userUuid);
        Instant now = Instant.now();
        Instant expirationDate = now.plus(30, ChronoUnit.DAYS);
        userTokenDto.setCreateDate(now);
        userTokenDto.setExpirationDate(expirationDate);
        return userTokenDto;
    }

    private static UserLoginResponse createResponse(String newToken) {
        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setToken(newToken);
        return userResponse;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        validationUserLoginRequest(request);
        Instant now = Instant.now();
        List<UserDto> userDto = repository.findAll(UserDto.class, QueryParameter.builder().whereEqual("email", request.getEmail()).build());
        if (userDto.isEmpty()) {
            throw new UserException("user not found", null);
        }
        tokenService.verify(request.getPassword(), userDto.get(0).getPasswordSha());
        UserDto realUser = userDto.get(0);
        UserTokenDto userTokenDto = userDto.stream().findFirst()
                .flatMap(user -> repository.findAll(UserTokenDto.class, QueryParameter.builder().whereEqual("userUuid", user.getUuid()).build())
                .stream()
                .filter(token -> token.getExpirationDate().isAfter(now)).findFirst())
                .orElseGet(() -> {
                    UserTokenDto dto = createUserToken(realUser.getUuid());
                    String token = tokenService.newToken(dto);
                    dto.setToken(token);
                    repository.insert(dto);
                    return dto;
                });
        return createResponse(userTokenDto.getToken());
    }

    private void validationUserLoginRequest(UserLoginRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
            throw new UserException("required information absent", null);
        }
    }

    private OnetimeTokenDto validationUserRequest(UserRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getOnetimeToken())
                || !StringUtils.hasText(request.getPassword())
                || !StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getLastName())) {
            throw new UserException("required information absent", null);
        }
        Optional<OnetimeTokenDto> onetimeTokenDto = repository.findById(OnetimeTokenDto.class, request.getOnetimeToken());
        if (onetimeTokenDto.isEmpty() || onetimeTokenDto.get().getUsedDate() != null
                || onetimeTokenDto.get().getExpirationDate().isBefore(Instant.now())
                || !request.getEmail().equals(onetimeTokenDto.get().getEmail())) {
            throw new UserException("one time token not valid", null);
        }
        List<UserDto> userDto = repository.findAll(UserDto.class, QueryParameter.builder().whereEqual("email", request.getEmail()).build());
        if (!userDto.isEmpty()) {
            throw new UserException("user already existed", null);
        }
        return onetimeTokenDto.get();
    }

    @Override
    public UserDto findOne(String uuid) {
        return repository.findById(UserDto.class, uuid).orElseThrow(() -> new UserException("user not found", null));
    }

    @Override
    public UserDto findByEmail(String email) {
        return repository.findAll(UserDto.class, QueryParameter.builder().whereEqual("email", email).build())
        .stream().findFirst().orElseThrow(() -> new UserException("user not found", null));
    }

    @Override
    public void validateToken(String token) {
        UserTokenDto userTokenDto = tokenService.verifyToken(token);
        if (userTokenDto.getExpirationDate().isBefore(Instant.now())) {
            throw new SecurityTokenException("security token expired", null);
        }
        List<UserTokenDto> tokens = repository.findAll(UserTokenDto.class, QueryParameter.builder().whereEqual("userUuid", userTokenDto.getUserUuid()).build());
        if (tokens.stream().filter(ut -> ut.getToken().equals(userTokenDto.getToken()))
                .filter(ut -> ut.getExpirationDate().isAfter(Instant.now()))
                .findFirst().isEmpty()) {
            throw new SecurityTokenException("security token not found or expired", null);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = findByEmail(username);
        return new User(userDto.getEmail(), userDto.getPasswordSha(), AuthorityUtils.createAuthorityList(List.of("USER")));
    }
}
