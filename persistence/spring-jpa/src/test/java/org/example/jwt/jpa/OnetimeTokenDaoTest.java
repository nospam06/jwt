package org.example.jwt.jpa;

import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.jpa.config.TestJpaConfig;
import org.example.jwt.jpa.dao.OnetimeTokenDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("h2")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestJpaConfig.class})
public class OnetimeTokenDaoTest {
    @Autowired
    private OnetimeTokenDao OnetimeTokenDao;

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
