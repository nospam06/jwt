package org.example.jwt.logic.service;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.dto.UserDto;
import org.example.jwt.dto.UserLoginRequest;
import org.example.jwt.dto.UserRequest;
import org.example.jwt.dto.UserResponse;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jdbc.dao.OnetimeTokenDao;
import org.example.jwt.jdbc.dao.UserDao;
import org.example.jwt.jdbc.dao.UserTokenDao;
import org.example.jwt.logic.UserException;
import org.example.jwt.logic.api.UserService;
import org.example.jwt.security.api.TokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final TokenService tokenService;
    private final UserDao userDao;
    private final UserTokenDao userTokenDao;
    private final OnetimeTokenDao onetimeTokenDao;

    @Override
    public OnetimeTokenDto createOnetimeToken(String email) {
        OnetimeTokenDto onetimeTokenDto = new OnetimeTokenDto();
        onetimeTokenDto.setEmail(email);
        return onetimeTokenDao.insert(onetimeTokenDto);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        // validation
        OnetimeTokenDto onetimeTokenDto = validationUserRequest(request);
        markTokenAsUsed(onetimeTokenDto);
        UserDto userDto = createUserDto(request);
        UserDto newUser = userDao.insert(userDto);
        String newToken = tokenService.newToken(newUser.getUuid());
        UserResponse userResponse = new UserResponse();
        userResponse.setToken(newToken);
        return userResponse;
    }

    private void markTokenAsUsed(OnetimeTokenDto onetimeTokenDto) {
        onetimeTokenDto.setUsedDate(Instant.now());
        onetimeTokenDao.update(onetimeTokenDto);
    }

    @Override
    public UserResponse login(UserLoginRequest request) {
        validationUserLoginRequest(request);
        Instant now = Instant.now();
        Optional<UserDto> userDto = userDao.findByEmail(request.getEmail())
                .filter(user -> user.getPasswordSha().equals(tokenService.sign(request.getPassword())));
        if (userDto.isEmpty()) {
            throw new UserException("user not found or password not matching", null);
        }
        UserDto realUser = userDto.get();
        UserTokenDto userTokenDto = userDto.map(user -> userTokenDao.findAll(user.getUuid()))
                .stream().flatMap(Collection::stream)
                .filter(token -> token.getExpirationDate().isAfter(now)).findFirst()
                .orElseGet(() -> {
                    UserTokenDto dto = new UserTokenDto();
                    dto.setUserUuid(realUser.getUuid());
                    dto.setExpirationDate(now.plus(1, ChronoUnit.DAYS));
                    return userTokenDao.insert(dto);
                });
        String token = tokenService.newToken(userTokenDto.getUserUuid());
        UserResponse userResponse = new UserResponse();
        userResponse.setToken(token);
        return userResponse;
    }

    private void validationUserLoginRequest(UserLoginRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
            throw new UserException("required information absent", null);
        }
    }

    private UserDto createUserDto(UserRequest request) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(request, userDto);
        return userDto;
    }

    private OnetimeTokenDto validationUserRequest(UserRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getOnetimeToken())
                || !StringUtils.hasText(request.getPassword())
                || !StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getLastName())) {
            throw new UserException("required information absent", null);
        }
        Optional<OnetimeTokenDto> onetimeTokenDto = onetimeTokenDao.findOne(request.getOnetimeToken());
        if (onetimeTokenDto.isEmpty() || onetimeTokenDto.get().getUsedDate() != null
                || onetimeTokenDto.get().getExpirationDate().isBefore(Instant.now())
                || !request.getEmail().equals(onetimeTokenDto.get().getEmail())) {
            throw new UserException("one time token not valid", null);
        }
        Optional<UserDto> userDto = userDao.findByEmail(request.getEmail());
        if (userDto.isPresent()) {
            throw new UserException("user already existed", null);
        }
        return onetimeTokenDto.get();
    }

    @Override
    public UserDto findOne(String uuid) {
        return userDao.findOne(uuid).orElseThrow(() -> new UserException("user not found", null));
    }

    @Override
    public UserDto findByEmail(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new UserException("user not found", null));
    }
}
