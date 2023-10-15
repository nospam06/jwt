package org.example.jwt.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("org.example.jwt.keystore")
@Data
public class JwtProperties {
    private String path = "../../.keystore";
    private String password = "changeme";
    private String keyAlias = "app1";
}
