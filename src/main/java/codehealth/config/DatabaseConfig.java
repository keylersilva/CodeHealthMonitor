package codehealth.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/codehealth_db";
        String user = "postgres";
        String password = "admin123";
        return DriverManager.getConnection(url, user, password);
    }
}
