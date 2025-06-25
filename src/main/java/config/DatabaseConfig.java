package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

/**
 * DatabaseConfig is responsible for initializing and managing connections
 * to the PostgreSQL database using credentials defined in a properties file
 * or fallback to hardcoded team credentials.
 *
 * This supports system functionality by enabling persistence of systems, CVEs,
 * and related metadata.
 */
public class DatabaseConfig {
    
    // Database connection details
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    // Static block: Loads configuration when class is first used
    static {
        loadDatabaseProperties();
    }
    
    /**
     * Loads database configuration from a `database.properties` file.
     * If the file is not found or an error occurs, it falls back to
     * predefined team settings.
     */
    private static void loadDatabaseProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                DB_URL = prop.getProperty("db.url");
                DB_USER = prop.getProperty("db.username");
                DB_PASSWORD = prop.getProperty("db.password");
            } else {
                // Use fallback credentials
                setDefaultDatabaseConfig();
            }
        } catch (Exception e) {
            // Fallback in case of error
            setDefaultDatabaseConfig();
        }
    }
    
    /**
     * Fallback method that sets hardcoded database configuration
     * (used when properties file is missing or unreadable).
     */
    private static void setDefaultDatabaseConfig() {
        String host = "bronto.ewi.utwente.nl";
        String dbName = "dab_di2425-2b_205";
        String schema = "?currentSchema=dab_di2425-2b_205"; // Replace with your schema name if needed
        
        DB_URL = "jdbc:postgresql://" + host + ":5432/" + dbName + schema;
        DB_USER = "dab_di2425-2b_205"; // default username
        DB_PASSWORD = "CHANGE_ME"; // default password
    }
    
    /**
     * Gets a connection to the database using configured credentials.
     *
     * @return a live SQL Connection
     * @throws SQLException if connection or driver loading fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL driver not found", e);
        }
    }
    
    /**
     * Gracefully closes a database connection.
     *
     * @param connection the Connection object to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Tests the database connection by opening and immediately closing it.
     *
     * @return true if successful, false if connection failed
     */
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
    
    /**
     * Standalone method to test database connectivity.
     * Used during development/debugging.
     */
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        testConnection();
    }
}
