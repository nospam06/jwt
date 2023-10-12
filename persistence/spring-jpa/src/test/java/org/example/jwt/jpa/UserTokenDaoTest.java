package org.example.jwt.jpa;

import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jpa.config.TestJpaConfig;
import org.example.jwt.jpa.dao.UserTokenDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("h2")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestJpaConfig.class})
public class UserTokenDaoTest {
    @Autowired
    private UserTokenDao userTokenDao;

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
