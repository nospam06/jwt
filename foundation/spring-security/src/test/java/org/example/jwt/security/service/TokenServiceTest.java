package org.example.jwt.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.json.config.JsonConfig;
import org.example.jwt.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    @Test
    void verifyToken() {
        SignerVerifier signerVerifier = new SecurityConfig().signer(UUID.randomUUID().toString());
        ObjectMapper objectMapper = new JsonConfig().createObjectMapper();
        TokenServiceImpl tokenService = new TokenServiceImpl(objectMapper, signerVerifier);
        String userUuid = UUID.randomUUID().toString();
        String token = tokenService.newToken(userUuid);
        UserTokenDto userTokenDto = tokenService.verifyToken(token);
        assertEquals(userUuid, userTokenDto.getUserUuid());
    }
}