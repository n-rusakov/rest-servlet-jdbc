package org.example.restservlet.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.restservlet.config.AppData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HikariDataSourceProvider {

    private HikariDataSourceProvider() {
        throw new IllegalArgumentException();
    }

    private static HikariDataSource instance = null;

    public static HikariDataSource getDataSource() {
        if (instance == null) {
            instance = init();
        }

        return instance;
    }

    private static HikariDataSource init() {
        try {
            Properties properties = new Properties();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(AppData.DB_CONFIG_FILE);
            properties.load(inputStream);

            return new HikariDataSource(new HikariConfig(properties));
        } catch (IOException e) {
            throw new RuntimeException("Cannot load property file");
        }

    }
}
