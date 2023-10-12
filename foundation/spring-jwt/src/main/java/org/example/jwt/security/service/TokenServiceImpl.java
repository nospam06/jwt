package org.example.jwt.security.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.SecurityTokenException;
import org.example.jwt.security.api.TokenService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RSASSASigner signer;
    private final JWSVerifier verifier;

    @Override
    public String newToken(UserTokenDto userTokenDto) {
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
            signedJWT.verify(verifier);
            UserTokenDto userTokenDto = new UserTokenDto();
            userTokenDto.setUserUuid(signedJWT.getJWTClaimsSet().getSubject());
            userTokenDto.setExpirationDate(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant());
            if (userTokenDto.getExpirationDate().isBefore(Instant.now())) {
                throw new SecurityTokenException("token expired", null);
            }
            return userTokenDto;
        } catch (SecurityTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityTokenException(e.getMessage(), e);
        }
    }

    private SignedJWT createJwt(UserTokenDto userTokenDto) throws JOSEException {
        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userTokenDto.getUserUuid())
                .issuer("https://example.org")
                .expirationTime(Date.from(userTokenDto.getExpirationDate()))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), claimsSet);
        // Compute the RSA signature
        signedJWT.sign(signer);
        return signedJWT;
    }

    @Override
    public String sign(String content) {
        MessageDigest sha256 = getSha256();
        byte[] digest = sha256.digest(content.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }

    @Override
    public void verify(String content, String signature) {
        MessageDigest sha256 = getSha256();
        String hash = HexFormat.of().formatHex(sha256.digest(content.getBytes(StandardCharsets.UTF_8)));
        if (!hash.equals(signature)) {
            throw new SecurityTokenException("password not correct", null);
        }
    }

    private MessageDigest getSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityTokenException("hashing error", e);
        }
    }
}
