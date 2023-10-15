package org.example.jwt.security.service;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.security.SecurityTokenException;
import org.example.jwt.security.api.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String newToken(UserTokenDto userTokenDto) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(userTokenDto.getUserUuid())
                .issuer("https://example.org")
                .issuedAt(Instant.now())
                .expiresAt(userTokenDto.getExpirationDate())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet));
        return jwt.getTokenValue();
    }

    @Override
    public UserTokenDto verifyToken(String token) {
        // On the consumer side, parse the JWS and verify its RSA signature
        Jwt jwt = jwtDecoder.decode(token);
        String subject = jwt.getClaims().getOrDefault(JwtClaimNames.SUB, "").toString();
        Object expirationDate = jwt.getClaims().get(JwtClaimNames.EXP);
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setUserUuid(subject);
        userTokenDto.setToken(token);
        Optional.ofNullable(expirationDate).ifPresent(exp -> userTokenDto.setExpirationDate((Instant) exp));
        return userTokenDto;
    }

    @Override
    public String sign(String content) {
        return passwordEncoder.encode(content);
    }

    @Override
    public void verify(String content, String signature) {
        if (!passwordEncoder.matches(content, signature)) {
            throw new SecurityTokenException("password not correct", null);
        }
    }
}
