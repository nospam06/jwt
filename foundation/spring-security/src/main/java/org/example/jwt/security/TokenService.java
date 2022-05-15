package org.example.jwt.security;

import org.example.jwt.dto.UserTokenDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class TokenService {
    public void newJwt(String userUuid) {
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setUuid(UUID.randomUUID().toString());
        userTokenDto.setUserUuid(userUuid);
        Instant now = Instant.now();
        Instant expirationDate = now.plus(1, ChronoUnit.DAYS);
        userTokenDto.setCreateDate(now);
        userTokenDto.setExpirationDate(expirationDate);
    }

}
