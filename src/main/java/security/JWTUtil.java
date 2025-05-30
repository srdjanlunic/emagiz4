package security;

import model.User;

public class JWTUtil {
    private static final String SECRET_KEY = "your-secret-key";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    // generate JWT token for user
    public static String generateToken(User user) {
        // TODO: implement JWT token generation
        return null;
    }

    // validate JWT token
    public static boolean validateToken(String token) {
        // TODO: implement JWT token validation
        return false;
    }

    // extract username from token
    public static String getUsernameFromToken(String token) {
        // TODO: extract username from JWT token
        return null;
    }

    // extract user ID from token
    public static Long getUserIdFromToken(String token) {
        // TODO: extract user ID from JWT token
        return null;
    }

    // check if token is expired
    public static boolean isTokenExpired(String token) {
        // TODO: check token expiration
        return false;
    }
}
