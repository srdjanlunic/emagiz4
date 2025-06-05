package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConfig {
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        loadDatabaseProperties();
    }

    private static void loadDatabaseProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                DB_URL = prop.getProperty("db.url");
                DB_USER = prop.getProperty("db.username");
                DB_PASSWORD = prop.getProperty("db.password");
            } else {
                // Use team's database configuration
                setDefaultDatabaseConfig();
            }
        } catch (Exception e) {
            // Fallback to team's database
            setDefaultDatabaseConfig();
        }
    }

    private static void setDefaultDatabaseConfig() {
        String host = "bronto.ewi.utwente.nl";
        String dbName = "dab_di2425-2b_205";
        String schema = "?currentSchema=dab_di2425-2b_205"; // Change this to team's schema name

        DB_URL = "jdbc:postgresql://" + host + ":5432/" + dbName + schema;
        DB_USER = "dab_di2425-2b_205"; // database name as username
        DB_PASSWORD = "CHANGE_ME"; //  provided password
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL driver not found", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
            System.out.println("Connected to: " + conn.getMetaData().getURL());
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        testConnection();
    }
}
