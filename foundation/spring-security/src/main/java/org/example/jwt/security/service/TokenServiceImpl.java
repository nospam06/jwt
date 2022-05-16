package org.example.jwt.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.SecurityTokenException;
import org.example.jwt.security.api.TokenService;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final ObjectMapper objectMapper;
    private final SignerVerifier signer;

    @Override
    public String newToken(String userUuid) {
        UserTokenDto userTokenDto = setTokenContext(userUuid);
        String payload = createJwtPayload(userTokenDto);
        Jwt jwt = createJwt(payload);
        return jwt.getEncoded();
    }

    @Override
    public UserTokenDto verifyToken(String token) {
        UserTokenDto userTokenDto;
        Jwt jwt = JwtHelper.decodeAndVerify(token, signer);
        userTokenDto = extractPayload(jwt);
        if (userTokenDto.getExpirationDate().isBefore(Instant.now())) {
            throw new SecurityTokenException("token expired", null);
        }
        return userTokenDto;
    }

    public String sign(String content) {
        MessageDigest sha256 = getSha256();
        return new String(sha256.digest(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    private MessageDigest getSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityTokenException("hashing error", e);
        }
    }

    public void verify(String content, String signature) {
        MessageDigest sha256 = getSha256();
        String hash = new String(sha256.digest(content.getBytes(StandardCharsets.UTF_8)));
        if (!hash.equals(signature)) {
            throw new SecurityTokenException("password not correct", null);
        }
    }

    private Jwt createJwt(String payload) {
        return JwtHelper.encode(payload, signer);
    }

    private String createJwtPayload(UserTokenDto userTokenDto) {
        try {
            return objectMapper.writeValueAsString(userTokenDto);
        } catch (Exception e) {
            throw new SecurityTokenException("Cannot create security token", e);
        }
    }

    private UserTokenDto extractPayload(Jwt jwt) {
        try {
            return objectMapper.readValue(jwt.getClaims(), UserTokenDto.class);
        } catch (Exception e) {
            throw new SecurityTokenException("Error reading payload", e);
        }
    }

    private UserTokenDto setTokenContext(String userUuid) {
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
