package org.example.jwt.jdbc.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserDto;
import org.example.jwt.jdbc.PersistenceException;
import org.example.jwt.jdbc.api.JdbcDao;
import org.example.jwt.jdbc.converter.UserRowMapper;
import org.example.jwt.model.User;
import org.example.jwt.security.api.TokenService;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDao implements JdbcDao<UserDto> {
    private static final String INSERT = "insert into user values(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "update user set password=?, first_name=?, last_name=?, email=?, phone=?, update_date=? where uuid=?";
    private static final String DELETE = "delete from user where uuid=?";
    private static final String FIND_ONE = "select * from user where uuid=?";
    private static final String FIND_BY_EMAIL = "select * from user where email=?";
    private final ConversionService conversionService;
    private final JdbcOperations jdbcOperations;
    private final TokenService tokenService;

    @Override
    public UserDto insert(UserDto dto) {
        User user = conversionService.convert(dto, User.class);
        Instant now = Instant.now();
        String dateTimeString = now.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = UUID.randomUUID().toString();
        String password = tokenService.sign(dto.getPassword());
        jdbcOperations.update(INSERT, uuid, password,
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), dateTimeString, dateTimeString);
     return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot insert user " + uuid, null));
    }

    @Override
    public UserDto update(UserDto dto) {
        User user = conversionService.convert(dto, User.class);
        Instant now = Instant.now();
        String dateTimeString = now.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = dto.getUuid();
        String password = tokenService.sign(dto.getPassword());
        jdbcOperations.update(UPDATE, password, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), dateTimeString, uuid);
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot update user " + uuid, null));
    }

    @Override
    public void delete(UserDto userDto) {
        jdbcOperations.update(DELETE, userDto.getUuid());
    }

    @Override
    public Optional<UserDto> findOne(String uuid) {
        try {
            User user = jdbcOperations.queryForObject(FIND_ONE, new UserRowMapper(), uuid);
            return Optional.ofNullable(conversionService.convert(user, UserDto.class));
        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserDto> findByEmail(String email) {
        try {
            User user = jdbcOperations.queryForObject(FIND_BY_EMAIL, new UserRowMapper(), email);
            return Optional.ofNullable(conversionService.convert(user, UserDto.class));
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}
