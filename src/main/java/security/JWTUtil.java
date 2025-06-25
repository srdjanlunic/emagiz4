package security;

import model.User;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.RoleDAO;
import model.Role;

/**
 * Utility class for generating and validating JSON Web Tokens (JWT) for user authentication.
 */
public class JWTUtil {
    private static final String SECRET_KEY = "mySecretKeyForJWTTokenGenerationThatIsLongEnough";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final RoleDAO roleDAO = new RoleDAO();
    
    /**
     * Generates a JWT token for the given user.
     *
     * @param user the User object for whom to generate the token
     * @return a JWT token string
     * @throws RuntimeException if token generation fails
     */
    public static String generateToken(User user) {
        try {
            ObjectNode header = mapper.createObjectNode();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            ObjectNode payload = mapper.createObjectNode();
            payload.put("sub", user.getUsername());
            payload.put("userId", user.getId().toString());
            payload.put("roleId", user.getRoleId() != null ? user.getRoleId().toString() : "");
            
            if (user.getRoleId() != null) {
                Role role = roleDAO.findById(user.getRoleId());
                if (role != null) {
                    payload.put("role", role.getName());
                }
            }
            
            payload.put("iat", System.currentTimeMillis() / 1000);
            payload.put("exp", (System.currentTimeMillis() + EXPIRATION_TIME) / 1000);
            
            String headerEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mapper.writeValueAsBytes(header));
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mapper.writeValueAsBytes(payload));
            
            String signature = createSignature(headerEncoded + "." + payloadEncoded);
            
            return headerEncoded + "." + payloadEncoded + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }
    
    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token string to validate
     * @return true if token is valid and not expired, false otherwise
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            
            String headerAndPayload = parts[0] + "." + parts[1];
            String signature = parts[2];
            
            String expectedSignature = createSignature(headerAndPayload);
            if (!signature.equals(expectedSignature)) return false;
            
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            long exp = payload.get("exp").asLong();
            return exp > (System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token string
     * @return the username contained in the token, or null if extraction fails
     */
    public static String getUsernameFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            return payload.get("sub").asText();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts the user ID (UUID) from the given JWT token.
     *
     * @param token the JWT token string
     * @return the user UUID contained in the token, or null if extraction fails
     */
    public static UUID getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            return UUID.fromString(payload.get("userId").asText());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts the role name from the given JWT token.
     *
     * @param token the JWT token string
     * @return the role name contained in the token, or null if not present or extraction fails
     */
    public static String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            return payload.has("role") ? payload.get("role").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts the role ID (UUID) from the given JWT token.
     *
     * @param token the JWT token string
     * @return the role UUID contained in the token, or null if not present or extraction fails
     */
    public static UUID getRoleIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            String roleId = payload.get("roleId").asText();
            return roleId.isEmpty() ? null : UUID.fromString(roleId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token string
     * @return true if the token is expired or invalid, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            long exp = payload.get("exp").asLong();
            return exp <= (System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Creates an HMAC SHA-256 signature for the given data using the secret key.
     *
     * @param data the data to sign
     * @return the Base64 URL-safe encoded signature
     * @throws RuntimeException if signature creation fails
     */
    private static String createSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error creating signature", e);
        }
    }
}
