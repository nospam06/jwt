package org.example.jwt.jdbc.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserDto;
import org.example.jwt.jdbc.api.JdbcDao;
import org.example.jwt.jdbc.converter.UserRowMapper;
import org.example.jwt.model.User;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDao implements JdbcDao<UserDto> {
    private static final String INSERT = "insert into user values(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "update user set password=?, first_name=?, last_name=?, email=?, phone=?, update_date=? where uuid=?";
    private static final String DELETE = "delete from user where uuid=?";
    private static final String FIND_ONE = "select * from user where uuid=?";
    private final ConversionService conversionService;
    private final JdbcOperations jdbcOperations;

    @Override
    public UserDto insert(UserDto dto) {
        User user = conversionService.convert(dto, User.class);
        Instant now = Instant.now();
        String dateTimeString = now.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = UUID.randomUUID().toString();
        jdbcOperations.update(INSERT, uuid, dto.getPassword(),
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), dateTimeString, dateTimeString);
     return findOne(uuid);
    }

    @Override
    public UserDto update(UserDto dto) {
        User user = conversionService.convert(dto, User.class);
        Instant now = Instant.now();
        String dateTimeString = now.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = dto.getUuid();
        jdbcOperations.update(UPDATE, dto.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), dateTimeString, uuid);
        return findOne(uuid);
    }

    @Override
    public void delete(UserDto userDto) {
        jdbcOperations.update(DELETE, userDto.getUuid());
    }

    @Override
    public UserDto findOne(String uuid) {
        User user = jdbcOperations.queryForObject(FIND_ONE, new UserRowMapper(), uuid);
        return conversionService.convert(user, UserDto.class);
    }

    @Override
    public List<UserDto> findAll(Class<UserDto> cls, Map<String, String> searchCriteria) {
        //jdbcOperations.query
        return null;
    }
}
