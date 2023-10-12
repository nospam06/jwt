package org.example.jwt.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Bean
    public RSASSASigner signer(RSAKey rsaJWK) throws JOSEException {
        // Create RSA-signer with the private key
        return new RSASSASigner(rsaJWK);
    }

    @Bean
    public JWSVerifier verifier(RSAKey rsaJWK) throws JOSEException {
        return new RSASSAVerifier(rsaJWK.toPublicJWK());
    }

    @Bean
    public RSAKey generateRsaKey() throws JOSEException {
        return new RSAKeyGenerator(2048).generate();
    }
}
