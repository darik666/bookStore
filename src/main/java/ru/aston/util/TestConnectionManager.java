package ru.aston.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnectionManager {

    public static DataSource getDataSource(String jdbcUrl, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver"); // Set the JDBC driver class name

        return dataSource;
    }

    public static void initializeDatabaseSchema(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // Load the schema SQL file
            InputStream inputStream = TestConnectionManager.class.getResourceAsStream("/schema.sql");
            if (inputStream != null) {
                String schemaSql = new String(inputStream.readAllBytes());
                // Execute the SQL statements to create tables and define constraints
                statement.executeUpdate(schemaSql);
            } else {
                throw new IOException("Unable to load schema SQL file.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
}
