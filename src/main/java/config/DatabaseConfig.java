package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
    
    // Connection pool
    private static HikariDataSource dataSource;
    
    // Static block: Loads configuration when class is first used
    static {
        loadDatabaseProperties();
        initializeConnectionPool();
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
     * Initializes the connection pool for better performance
     */
    private static void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");
            
            // Connection pool settings for better performance
            config.setMinimumIdle(2);
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(5000); // 5 seconds
            config.setIdleTimeout(300000); // 5 minutes
            config.setMaxLifetime(600000); // 10 minutes
            config.setLeakDetectionThreshold(60000); // 1 minute
            
            // Performance tuning
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets a connection to the database using the connection pool.
     *
     * @return a live SQL Connection from the pool
     * @throws SQLException if connection from pool fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            // Fallback to direct connection if pool initialization failed
            try {
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL driver not found", e);
            }
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
