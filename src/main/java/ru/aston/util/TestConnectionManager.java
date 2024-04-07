package ru.aston.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TestConnectionManager class provides methods for setting up and initializing a test database schema.
 */
public class TestConnectionManager {

    /**
     * Returns a DataSource object configured with the provided JDBC URL, username, and password.
     *
     * @param jdbcUrl  the JDBC URL for the database
     * @param username the username for database authentication
     * @param password the password for database authentication
     * @return configured DataSource object
     */
    public static DataSource getDataSource(String jdbcUrl, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver"); // Set the JDBC driver class name

        return dataSource;
    }

    /**
     * Initializes the database schema using the provided DataSource.
     *
     * @param dataSource the DataSource object representing the database connection
     */
    public static void initializeDatabaseSchema(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            InputStream inputStream = TestConnectionManager.class.getResourceAsStream("/schema.sql");
            if (inputStream != null) {
                String schemaSql = new String(inputStream.readAllBytes());
                statement.executeUpdate(schemaSql);
            } else {
                throw new IOException("Unable to load schema SQL file.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}