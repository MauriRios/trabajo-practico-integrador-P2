package com.mycompany.trabajo.practico.integrador.p2.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String PROPERTIES_FILE = "database.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Could not find " + PROPERTIES_FILE);
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing conection: " + e.getMessage());
            }
        }
    }
}
