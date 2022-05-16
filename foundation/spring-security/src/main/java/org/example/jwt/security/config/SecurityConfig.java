package org.example.jwt.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;

@Configuration
public class SecurityConfig {
    @Bean
    public SignerVerifier signer(@Value("${org.example.jwt.keyId}") String keyId) {
        return new MacSigner(keyId);
    }
}
