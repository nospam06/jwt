package org.example.jwt.security.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {
    private static final String PKCS_12 = "PKCS12";

    @Bean
    public KeyStore keyStore(JwtProperties properties) {
        try {
            KeyStore keyStore = KeyStore.getInstance(PKCS_12);
            keyStore.load(new FileInputStream(properties.getPath()), properties.getPassword().toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new SecurityException("Error loading keystore", e);
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyStore keyStore, JwtProperties properties) {
        try {
            Key key = keyStore.getKey(properties.getKeyAlias(), properties.getPassword().toCharArray());
            Certificate certificate = keyStore.getCertificate(properties.getKeyAlias());
            RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) key;
            JWK jwk = new RSAKey.Builder(publicKey).privateKey(rsaPrivateKey).build();
            JWKSet jwkSet = new JWKSet(jwk);
            JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(jwkSet);
            return new NimbusJwtEncoder(jwks);
        } catch (Exception e) {
            throw new SecurityException("Error loading private key", e);
        }
    }


    @Bean
    public JwtDecoder jwtDecoder(KeyStore keyStore, JwtProperties properties) {
        try {
            Certificate certificate = keyStore.getCertificate(properties.getKeyAlias());
            RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new SecurityException("Error loading public key", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
