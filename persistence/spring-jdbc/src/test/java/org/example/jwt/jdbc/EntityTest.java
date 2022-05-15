package org.example.jwt.jdbc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserDto;
import org.example.jwt.jdbc.config.JdbcConfig;
import org.example.jwt.jdbc.config.MysqlConfig;
import org.example.jwt.jdbc.converter.UserDtoUserConverter;
import org.example.jwt.jdbc.converter.UserUserDtoConverter;
import org.example.jwt.jdbc.dao.UserDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class EntityTest {
    private static UserDao userDao;

    public static void main(String[] args) {
        new EntityTest().manual();
    }

    private void manual() {
        String replaced = "2022-05-15T16:24:06.392Z".replace("T", " ").replace("Z", "");
        log.info("{}", replaced);
    }

    @BeforeAll
    public static void init() {
        Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.github.dockerjava");
        logger.setLevel(Level.INFO);
    }

    @BeforeEach
    void setup() {
        if (userDao == null) {
            DataSource dataSource = new MysqlConfig().local();
            JdbcOperations jdbcOperations = new JdbcConfig().jdbcConfig(dataSource);
            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new UserDtoUserConverter());
            conversionService.addConverter(new UserUserDtoConverter());
            userDao = new UserDao(conversionService, jdbcOperations);
        }
    }

    @Test
    void userCrudTest() throws InterruptedException {
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
        // update
        TimeUnit.MILLISECONDS.sleep(500);
        userDto.setUuid(actual.getUuid());
        userDto.setFirstName("hello");
        userDto.setLastName("world");
        UserDto update = userDao.update(userDto);
        assertNotNull(update);
        assertEquals("hello", update.getFirstName());
        assertEquals("world", update.getLastName());
        assertEquals(actual.getCreateDate(), update.getCreateDate());
        assertTrue(update.getUpdateDate().isAfter(actual.getUpdateDate()));
        // delete
        userDao.delete(userDto);
        // find one
        UserDto deleted = userDao.findOne(actual.getUuid());
        assertNull(deleted);
    }
}
