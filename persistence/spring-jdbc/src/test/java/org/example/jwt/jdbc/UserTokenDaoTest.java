package org.example.jwt.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jdbc.config.JdbcConfig;
import org.example.jwt.jdbc.converter.UserTokenDtoUserTokenConverter;
import org.example.jwt.jdbc.converter.UserTokenUserTokenDtoConverter;
import org.example.jwt.jdbc.dao.UserTokenDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class UserTokenDaoTest extends BaseTest {
    private static UserTokenDao userTokenDao;

    @BeforeEach
    void setup() {
        if (userTokenDao == null) {
            DataSource dataSource = getDataSource();
            JdbcOperations jdbcOperations = new JdbcConfig().datasourceConfig(dataSource);
            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new UserTokenDtoUserTokenConverter());
            conversionService.addConverter(new UserTokenUserTokenDtoConverter());
            userTokenDao = new UserTokenDao(conversionService, jdbcOperations);
        }
    }

    @Test
    void crudTest() {
        UserTokenDto userTokenDto = new UserTokenDto();
        String userUuid = UUID.randomUUID().toString();
        userTokenDto.setUserUuid(userUuid);
        Instant expiration = Instant.now().plusSeconds(1).truncatedTo(ChronoUnit.SECONDS);
        userTokenDto.setExpirationDate(expiration);
        // insert
        UserTokenDto actual = userTokenDao.insert(userTokenDto);
        assertNotNull(actual);
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreateDate());
        assertEquals(expiration, actual.getExpirationDate());
        // update
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        userTokenDto.setUuid(actual.getUuid());
        userTokenDto.setExpirationDate(now);
        UserTokenDto update = userTokenDao.update(userTokenDto);
        assertNotNull(update);
        assertEquals(now, update.getExpirationDate());
        //insert another one
        expiration = Instant.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS);
        userTokenDto.setExpirationDate(expiration);
        UserTokenDto actual2 = userTokenDao.insert(userTokenDto);
        assertNotNull(actual);
        // find by user
        List<UserTokenDto> all = userTokenDao.findAll(userUuid);
        assertEquals(2, all.size());
        // delete
        userTokenDao.delete(userTokenDto);
        userTokenDao.delete(actual2);
        // find one
        Optional<UserTokenDto> deleted = userTokenDao.findOne(actual.getUuid());
        assertTrue(deleted.isEmpty());
        deleted = userTokenDao.findOne(actual2.getUuid());
        assertTrue(deleted.isEmpty());
    }
}
