package ru.aston.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages database connections and provides a data source for accessing the database.
 */
public class ConnectionManager {
    /**
     * The name of the properties file containing database connection configuration.
     */
    private static final String PROPERTIES_FILE = "db.properties";

    /**
     * Retrieves a data source configured with database connection properties.
     *
     * @return Configured data source.
     */
    public static DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        Properties properties = new Properties();

        try (InputStream inputStream =
                     ConnectionManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
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
        dataSource.setDriverClassName("org.postgresql.Driver");

        return dataSource;
    }
}