package org.example.jwt.jdbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {
    @Bean
    public JdbcOperations jdbcConfig(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
