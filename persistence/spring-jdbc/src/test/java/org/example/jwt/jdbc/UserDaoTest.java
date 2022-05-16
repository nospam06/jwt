package org.example.jwt.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserDto;
import org.example.jwt.jdbc.config.JdbcConfig;
import org.example.jwt.jdbc.converter.UserDtoUserConverter;
import org.example.jwt.jdbc.converter.UserUserDtoConverter;
import org.example.jwt.jdbc.dao.UserDao;
import org.example.jwt.json.config.JsonConfig;
import org.example.jwt.security.config.SecurityConfig;
import org.example.jwt.security.service.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;

import javax.sql.DataSource;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class UserDaoTest extends BaseTest {
    private static UserDao userDao;

    @BeforeEach
    void setup() {
        if (userDao == null) {
            DataSource dataSource = getDataSource();
            JdbcOperations jdbcOperations = new JdbcConfig().datasourceConfig(dataSource);
            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new UserDtoUserConverter());
            conversionService.addConverter(new UserUserDtoConverter());
            ObjectMapper objectMapper = new JsonConfig().createObjectMapper();
            SignerVerifier signer = new SecurityConfig().signer(UUID.randomUUID().toString());
            TokenServiceImpl tokenService = new TokenServiceImpl(objectMapper, signer);
            userDao = new UserDao(conversionService, jdbcOperations, tokenService);
        }
    }

    @Test
    void crudTest() throws InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setPassword("hello_world");
        userDto.setFirstName("foo");
        userDto.setLastName("bar");
        userDto.setEmail("foo.bar@gmail.com");
        userDto.setPhone("111-2345678");
        // insert
        UserDto actual = userDao.insert(userDto);
        assertNotNull(actual);
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreateDate());
        assertEquals(actual.getCreateDate(), actual.getUpdateDate());
        assertEquals(userDto.getFirstName(), actual.getFirstName());
        assertEquals(userDto.getLastName(), actual.getLastName());
        assertEquals(userDto.getEmail(), actual.getEmail());
        assertEquals(userDto.getPhone(), actual.getPhone());
        // sleep necessary to have new update time
        TimeUnit.SECONDS.sleep(1);
        // update
        userDto.setUuid(actual.getUuid());
        userDto.setFirstName("hello");
        userDto.setLastName("world");
        UserDto update = userDao.update(userDto);
        assertNotNull(update);
        assertEquals("hello", update.getFirstName());
        assertEquals("world", update.getLastName());
        assertEquals(actual.getCreateDate(), update.getCreateDate());
        assertTrue(update.getUpdateDate().isAfter(actual.getUpdateDate()));
        // find by email
        Optional<UserDto> email = userDao.findByEmail(actual.getEmail());
        assertTrue(email.isPresent());
        assertEquals(update, email.get());
        // delete
        userDao.delete(userDto);
        // find one
        Optional<UserDto> deleted = userDao.findOne(actual.getUuid());
        assertTrue(deleted.isEmpty());
    }
}
