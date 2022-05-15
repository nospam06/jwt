package org.example.jwt.jdbc.docker;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

@Slf4j
public class MysqlContainer {
    public JdbcDatabaseContainer<?> startContainer() {
        JdbcDatabaseContainer<?> container = new MySQLContainer<>("mysql:8.0.22")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testuser")
                .withInitScript("sql/test_init.sql");
        container.start();
        return container;
    }
}
