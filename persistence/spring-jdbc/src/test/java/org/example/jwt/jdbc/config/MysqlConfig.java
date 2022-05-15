package org.example.jwt.jdbc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.jwt.jdbc.docker.MysqlContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;

@Configuration
@Profile("mysql")
public class MysqlConfig {
    @Bean
    public DataSource fromContainer() {
        JdbcDatabaseContainer<?> container = new MysqlContainer().startContainer();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(container.getDriverClassName());
        hikariConfig.setJdbcUrl(container.getJdbcUrl());
        hikariConfig.setUsername(container.getUsername());
        hikariConfig.setPassword(container.getPassword());
        return new HikariDataSource(hikariConfig);
    }

    public DataSource local() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/testdb?useSSL=false&characterEncoding=UTF8&connectionTimeZone=UTC&allowPublicKeyRetrieval=true");
        hikariConfig.setUsername("testuser");
        hikariConfig.setPassword("testuser");
        return new HikariDataSource(hikariConfig);
    }
}
