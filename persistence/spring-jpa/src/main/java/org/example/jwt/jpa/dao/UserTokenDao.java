package org.example.jwt.jpa.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jpa.PersistenceException;
import org.example.jwt.jpa.api.JdbcDao;
import org.example.jwt.jpa.repository.UserTokenRepository;
import org.example.jwt.jpa.model.UserToken;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserTokenDao implements JdbcDao<UserTokenDto> {
    private final ConversionService conversionService;
    private final UserTokenRepository repository;

    @Override
    public UserTokenDto insert(UserTokenDto dto) {
        UserToken userToken = conversionService.convert(dto, UserToken.class);
        Instant now = Instant.now();
        String uuid = UUID.randomUUID().toString();
        userToken.setUuid(uuid);
        userToken.setCreateDate(now);
        repository.save(userToken);
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot insert userToken " + uuid, null));
    }

    @Override
    public UserTokenDto update(UserTokenDto dto) {
        String uuid = dto.getUuid();
        return repository.findById(uuid)
                .map(token -> {
                    BeanUtils.copyProperties(dto, token, "uuid", "createDate");
                    return repository.save(token);
                })
                .map(u -> conversionService.convert(u, UserTokenDto.class))
                .orElseThrow(() -> new PersistenceException("cannot update UserToken " + uuid, null));
    }

    @Override
    public void delete(UserTokenDto UserTokenDto) {
        repository.deleteById(UserTokenDto.getUuid());
    }

    @Override
    public Optional<UserTokenDto> findOne(String uuid) {
        return repository.findById(uuid)
                .map(u -> conversionService.convert(u, UserTokenDto.class));
    }

    public List<UserTokenDto> findAll(String userUuid) {
        return repository.findByUserUuid(userUuid).stream()
                .map(ut -> conversionService.convert(ut, UserTokenDto.class)).collect(Collectors.toList());
    }

    public UserTokenDto create(String userUuid) {
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setUuid(UUID.randomUUID().toString());
        userTokenDto.setUserUuid(userUuid);
        Instant now = Instant.now();
        Instant expirationDate = now.plus(1, ChronoUnit.DAYS);
        userTokenDto.setCreateDate(now);
        userTokenDto.setExpirationDate(expirationDate);
        return userTokenDto;
    }
}
