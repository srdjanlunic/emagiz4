package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {

    // close database resources safely
    public static void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        // TODO: implement safe resource closing
    }

    // execute query and return result set
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        // TODO: implement query execution
        return null;
    }

    // execute update and return affected rows
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        // TODO: implement update execution
        return 0;
    }

    // check if table exists
    public static boolean tableExists(String tableName) {
        // TODO: check if table exists in database
        return false;
    }
}
