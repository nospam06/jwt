package org.example.jwt.jpa;

import org.example.jwt.dto.UserDto;
import org.example.jwt.jpa.config.TestJpaConfig;
import org.example.jwt.jpa.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("h2")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestJpaConfig.class})
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

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
