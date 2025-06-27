# DatabaseConfig

## Purpose
DatabaseConfig manages database connections to PostgreSQL using connection pooling for improved performance and resource management.

## Key Features
- **Connection Pooling**: Uses HikariCP for efficient connection management
- **Fallback Configuration**: Supports both properties file and hardcoded credentials
- **Performance Optimized**: Configured with optimal pool settings for web applications

## Configuration
- **Pool Size**: 2-10 connections (minimum 2, maximum 10)
- **Timeouts**: 5s connection timeout, 5min idle timeout, 10min max lifetime
- **Performance Tuning**: Prepared statement caching and batch rewriting enabled

## Usage
```java
// Get a connection from the pool
try (Connection conn = DatabaseConfig.getConnection()) {
    // Use connection
}
```

## Performance Benefits
- Eliminates overhead of creating new connections for each request
- Reduces database server load through connection reuse
- Improves response times for database operations
- Automatic leak detection prevents connection leaks 