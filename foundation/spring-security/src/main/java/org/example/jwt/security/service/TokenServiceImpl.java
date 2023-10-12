package org.example.jwt.security.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.SecurityTokenException;
import org.example.jwt.security.api.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RSASSASigner signer;
    private final RSAKey publicKey;
    @Value("${org.example.jwt.keyId}")
    private String keyId;

    @Override
    public String newToken(String userUuid) {
        UserTokenDto userTokenDto = setTokenContext(userUuid);
        try {
            SignedJWT jwt = createJwt(userTokenDto);
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new SecurityTokenException(e.getMessage(), e);
        }
    }

    @Override
    public UserTokenDto verifyToken(String token) {
    // On the consumer side, parse the JWS and verify its RSA signature
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            signedJWT.verify(verifier);

            UserTokenDto userTokenDto = new UserTokenDto();
            userTokenDto.setUserUuid(signedJWT.getJWTClaimsSet().getSubject());
            userTokenDto.setExpirationDate(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant());
            if (userTokenDto.getExpirationDate().isBefore(Instant.now())) {
                throw new RuntimeException("token expired");
            }
            return userTokenDto;
        } catch (Exception e) {
            throw new SecurityTokenException(e.getMessage(), e);
        }
    }

    @Override
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

    private SignedJWT createJwt(UserTokenDto userTokenDto) throws JOSEException {
        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userTokenDto.getUserUuid())
                .issuer("https://example.org")
                .expirationTime(Date.from(userTokenDto.getExpirationDate()))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(keyId).build(),
                claimsSet);
        // Compute the RSA signature
        signedJWT.sign(signer);
        return signedJWT;
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
