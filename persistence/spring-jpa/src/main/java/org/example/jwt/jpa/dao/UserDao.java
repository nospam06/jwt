package org.example.jwt.jpa.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserDto;
import org.example.jwt.jpa.PersistenceException;
import org.example.jwt.jpa.api.JdbcDao;
import org.example.jwt.jpa.model.User;
import org.example.jwt.jpa.repository.UserRepository;
import org.example.jwt.security.api.TokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDao implements JdbcDao<UserDto> {
    private final ConversionService conversionService;
    private final UserRepository repository;
    private final TokenService tokenService;

    @Override
    public UserDto insert(UserDto dto) {
        User user = conversionService.convert(dto, User.class);
        Instant now = Instant.now();
        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        user.setCreateDate(now);
        user.setUpdateDate(now);
        String sha = tokenService.sign(dto.getPassword());
        user.setPasswordSha(sha);
        repository.save(user);
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot insert user " + uuid, null));
    }

    @Override
    public UserDto update(UserDto dto) {
        String uuid = dto.getUuid();
        Instant now = Instant.now();
        repository.findById(uuid).ifPresent(user -> {
            BeanUtils.copyProperties(dto, user, "uuid", "createDate");
            user.setUpdateDate(now);
            repository.save(user);
        });
        return findOne(uuid)
                .orElseThrow(() -> new PersistenceException("cannot update user " + uuid, null));
    }

    @Override
    public void delete(UserDto userDto) {
        repository.deleteById(userDto.getUuid());
    }

    @Override
    public Optional<UserDto> findOne(String uuid) {
        return repository.findById(uuid).map(u -> conversionService.convert(u, UserDto.class));
    }

    public Optional<UserDto> findByEmail(String email) {
        return repository.findByEmail(email).map(u -> conversionService.convert(u, UserDto.class));
    }
}
