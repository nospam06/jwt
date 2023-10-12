package org.example.jwt.security.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenServiceTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        MessageDigest.getInstance("SHA-256");
    }

    @Test
    void verifyToken() throws JOSEException {
        SecurityConfig securityConfig = new SecurityConfig();
        RSAKey privateKey = securityConfig.generateRsaKey(UUID.randomUUID().toString());
        RSASSASigner signer = new RSASSASigner(privateKey);
        TokenServiceImpl tokenService = new TokenServiceImpl(signer, securityConfig.publicKey(privateKey.toPublicJWK()));
        String userUuid = UUID.randomUUID().toString();
        String token = tokenService.newToken(userUuid);
        UserTokenDto userTokenDto = tokenService.verifyToken(token);
        assertEquals(userUuid, userTokenDto.getUserUuid());
    }
}