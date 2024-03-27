package ru.aston.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionManager {
    private static final String PROPERTIES_FILE = "db.properties";

    public static DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        Properties properties = new Properties();

        try (InputStream inputStream = ConnectionManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IOException("Properties file '" + PROPERTIES_FILE + "' not found");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());
        }

        String jdbcUrl = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver"); // Set the JDBC driver class name

        return dataSource;
    }
}
