package org.example.jwt.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.jdbc.config.JdbcConfig;
import org.example.jwt.jdbc.converter.OnetimeTokenDtoOnetimeTokenConverter;
import org.example.jwt.jdbc.converter.OnetimeTokenOnetimeTokenDtoConverter;
import org.example.jwt.jdbc.dao.OnetimeTokenDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class OnetimeTokenDaoTest extends BaseTest {
    private static OnetimeTokenDao OnetimeTokenDao;

    @BeforeEach
    void setup() {
        if (OnetimeTokenDao == null) {
            DataSource dataSource = getDataSource();
            JdbcOperations jdbcOperations = new JdbcConfig().datasourceConfig(dataSource);
            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new OnetimeTokenOnetimeTokenDtoConverter());
            conversionService.addConverter(new OnetimeTokenDtoOnetimeTokenConverter());
            OnetimeTokenDao = new OnetimeTokenDao(conversionService, jdbcOperations);
        }
    }

    @Test
    void crudTest() {
        OnetimeTokenDto onetimeTokenDto = new OnetimeTokenDto();
        onetimeTokenDto.setEmail("foo.bar@gmail.com");
        // insert
        OnetimeTokenDto actual = OnetimeTokenDao.insert(onetimeTokenDto);
        assertNotNull(actual);
        assertNotNull(actual.getToken());
        assertNotNull(actual.getCreateDate());
        assertEquals(actual.getCreateDate().plusSeconds(TimeUnit.HOURS.toSeconds(1)), actual.getExpirationDate());
        assertEquals(onetimeTokenDto.getEmail(), actual.getEmail());
        // update
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        onetimeTokenDto.setToken(actual.getToken());
        onetimeTokenDto.setExpirationDate(now);
        onetimeTokenDto.setUsedDate(now);
        OnetimeTokenDto update = OnetimeTokenDao.update(onetimeTokenDto);
        assertNotNull(update);
        assertEquals(now, update.getExpirationDate());
        assertEquals(now, update.getUsedDate());
        // delete
        OnetimeTokenDao.delete(onetimeTokenDto);
        // find one
        Optional<OnetimeTokenDto> deleted = OnetimeTokenDao.findOne(actual.getToken());
        assertTrue(deleted.isEmpty());
    }
}
