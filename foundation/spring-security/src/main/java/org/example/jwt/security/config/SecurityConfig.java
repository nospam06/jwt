package org.example.jwt.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Bean
    public RSASSASigner signer(@Value("${org.example.jwt.keyId}") String keyId) throws JOSEException {
        RSAKey rsaJWK = generateRsaKey(keyId);
// Create RSA-signer with the private key
        return new RSASSASigner(rsaJWK);
    }

    @Bean
    public RSAKey publicKey(RSAKey rsaJWK) {
        return rsaJWK.toPublicJWK();
    }

    @Bean
    public RSAKey generateRsaKey(String keyId) throws JOSEException {
        return new RSAKeyGenerator(2048)
                .keyID(keyId)
                .generate();
    }
}
