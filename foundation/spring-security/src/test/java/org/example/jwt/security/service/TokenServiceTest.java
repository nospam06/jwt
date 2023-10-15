package org.example.jwt.security.service;

import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.config.JwtConfig;
import org.example.jwt.security.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.security.KeyStore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class TokenServiceTest {

    @Test
    void verifyToken() {
        JwtConfig jwtConfig = new JwtConfig();
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setPath("../../.keystore");
        jwtProperties.setPassword("changeme");
        jwtProperties.setKeyAlias("app1");
        KeyStore keyStore = jwtConfig.keyStore(jwtProperties);
        JwtEncoder jwtEncoder = jwtConfig.jwtEncoder(keyStore, jwtProperties);
        JwtDecoder jwtDecoder = jwtConfig.jwtDecoder(keyStore, jwtProperties);
        TokenServiceImpl tokenService = new TokenServiceImpl(jwtEncoder, jwtDecoder, jwtConfig.passwordEncoder());
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
