package org.example.jwt.jdbc.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.jdbc.PersistenceException;
import org.example.jwt.jdbc.api.JdbcDao;
import org.example.jwt.jdbc.converter.OnetimeTokenRowMapper;
import org.example.jwt.model.OnetimeToken;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OnetimeTokenDao implements JdbcDao<OnetimeTokenDto> {
    private static final String INSERT = "insert into onetime_token values(?, ?, ?, ?, ?)";
    private static final String UPDATE = "update onetime_token set expiration_date=?, used_date=? where token=?";
    private static final String DELETE = "delete from onetime_token where token=?";
    private static final String FIND_ONE = "select * from onetime_token where token=?";
    private final ConversionService conversionService;
    private final JdbcOperations jdbcOperations;

    @Override
    public OnetimeTokenDto insert(OnetimeTokenDto dto) {
        OnetimeToken onetimeToken = conversionService.convert(dto, OnetimeToken.class);
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(TimeUnit.HOURS.toSeconds(1));
        String dateTimeString = now.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String expirationString = expiration.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String uuid = UUID.randomUUID().toString();
        jdbcOperations.update(INSERT, uuid, onetimeToken.getEmail(), dateTimeString, expirationString, null);
     return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot insert onetime token " + uuid, null));
    }

    @Override
    public OnetimeTokenDto update(OnetimeTokenDto dto) {
        OnetimeToken onetimeToken = conversionService.convert(dto, OnetimeToken.class);
        Instant expiration = onetimeToken.getExpirationDate();
        String dateTimeString = expiration.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "");
        String usedDateString = Optional.ofNullable(onetimeToken.getUsedDate()).map(ud -> ud.truncatedTo(ChronoUnit.MILLIS).toString().replace("T", " ").replace("Z", "")).orElse(null);
        String uuid = dto.getToken();
        jdbcOperations.update(UPDATE, dateTimeString, usedDateString, uuid);
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot update onetime token " + uuid, null));
    }

    @Override
    public void delete(OnetimeTokenDto OnetimeTokenDto) {
        jdbcOperations.update(DELETE, OnetimeTokenDto.getToken());
    }

    @Override
    public Optional<OnetimeTokenDto> findOne(String uuid) {
        try {
            OnetimeToken onetimeToken = jdbcOperations.queryForObject(FIND_ONE, new OnetimeTokenRowMapper(), uuid);
            return Optional.ofNullable(conversionService.convert(onetimeToken, OnetimeTokenDto.class));
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}
