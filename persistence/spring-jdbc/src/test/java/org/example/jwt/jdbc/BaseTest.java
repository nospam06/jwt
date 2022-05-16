package org.example.jwt.jdbc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.example.jwt.jdbc.config.MysqlConfig;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class BaseTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() {
        Logger logger = (Logger) LoggerFactory.getLogger("com.github.dockerjava");
        logger.setLevel(Level.INFO);
        logger = (Logger) LoggerFactory.getLogger("org.springframework.jdbc");
        logger.setLevel(Level.INFO);
    }

    protected DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new MysqlConfig().fromContainer();
        }
        return dataSource;
    }
}
