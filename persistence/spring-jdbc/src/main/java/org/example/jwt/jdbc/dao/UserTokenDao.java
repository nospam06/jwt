package org.example.jwt.jdbc.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jdbc.PersistenceException;
import org.example.jwt.jdbc.api.JdbcDao;
import org.example.jwt.jdbc.converter.UserTokenRowMapper;
import org.example.jwt.model.UserToken;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcOperations;
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
    private static final String INSERT = "insert into user_token values(?, ?, ?, ?)";
    private static final String UPDATE = "update user_token set expiration_date=? where uuid=?";
    private static final String DELETE = "delete from user_token where uuid=?";
    private static final String FIND_ONE = "select * from user_token where uuid=?";
    private static final String FIND_BY_USER = "select * from user_token where user_uuid=?";
    private final ConversionService conversionService;
    private final JdbcOperations jdbcOperations;

    @Override
    public UserTokenDto insert(UserTokenDto dto) {
        UserToken userToken = conversionService.convert(dto, UserToken.class);
        Instant now = Instant.now();
        String dateTimeString = now.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String expirationDateString = userToken.getExpirationDate().truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = UUID.randomUUID().toString();
        jdbcOperations.update(INSERT, uuid, userToken.getUserUuid(), dateTimeString, expirationDateString);
     return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot insert userToken " + uuid, null));
    }

    @Override
    public UserTokenDto update(UserTokenDto dto) {
        UserToken userToken = conversionService.convert(dto, UserToken.class);
        String expirationDateString = userToken.getExpirationDate().truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = dto.getUuid();
        jdbcOperations.update(UPDATE, expirationDateString, uuid);
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot update UserToken " + uuid, null));
    }

    @Override
    public void delete(UserTokenDto UserTokenDto) {
        jdbcOperations.update(DELETE, UserTokenDto.getUuid());
    }

    @Override
    public Optional<UserTokenDto> findOne(String uuid) {
        try {
            UserToken UserToken = jdbcOperations.queryForObject(FIND_ONE, new UserTokenRowMapper(), uuid);
            return Optional.ofNullable(conversionService.convert(UserToken, UserTokenDto.class));
        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public List<UserTokenDto> findAll(String userUuid) {
        try {
            List<UserToken> userTokens = jdbcOperations.query(FIND_BY_USER, new UserTokenRowMapper(), userUuid);
            return userTokens.stream().map(ut -> conversionService.convert(ut, UserTokenDto.class)).collect(Collectors.toList());
        } catch(Exception e) {
            return List.of();
        }
    }
}
