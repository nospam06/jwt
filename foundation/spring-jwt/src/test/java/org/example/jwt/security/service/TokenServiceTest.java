package org.example.jwt.security.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class TokenServiceTest {

    @Test
    void verifyToken() throws JOSEException {
        SecurityConfig securityConfig = new SecurityConfig();
        RSAKey privateKey = securityConfig.generateRsaKey();
        RSASSASigner signer = securityConfig.signer(privateKey);
        TokenServiceImpl tokenService = new TokenServiceImpl(signer, securityConfig.verifier(privateKey.toPublicJWK()));
        String userUuid = UUID.randomUUID().toString();
        UserTokenDto dto = create(userUuid);
        String token = tokenService.newToken(dto);
        UserTokenDto userTokenDto = tokenService.verifyToken(token);
        assertEquals(userUuid, userTokenDto.getUserUuid());
    }

    private UserTokenDto create(String userUuid) {
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setUuid(UUID.randomUUID().toString());
        userTokenDto.setUserUuid(userUuid);
        Instant now = Instant.now();
        Instant expirationDate = now.plus(1, ChronoUnit.DAYS);
        userTokenDto.setCreateDate(now);
        userTokenDto.setExpirationDate(expirationDate);
        return userTokenDto;
    }
}
