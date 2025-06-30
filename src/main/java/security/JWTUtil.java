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
            System.out.println("Generating JWT token for user: " + user.getUsername() + " with roleId: " + user.getRoleId());
            
            ObjectNode header = mapper.createObjectNode();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            ObjectNode payload = mapper.createObjectNode();
            payload.put("sub", user.getUsername());
            payload.put("userId", user.getId().toString());
            payload.put("roleId", user.getRoleId() != null ? user.getRoleId().toString() : "");
            
            String roleName = "UNKNOWN";
            if (user.getRoleId() != null) {
                // First try database lookup
                Role role = roleDAO.findById(user.getRoleId());
                System.out.println("Role lookup result: " + (role != null ? role.getName() : "null"));
                
                if (role != null) {
                    roleName = role.getName().toLowerCase();
                } else {
                    System.out.println("Warning: Role not found for roleId: " + user.getRoleId() + ", trying direct mapping");
                    // Fallback: Direct UUID to role name mapping
                    String roleIdStr = user.getRoleId().toString();
                    switch (roleIdStr) {
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15":
                            roleName = "admin";
                            break;
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12":
                            roleName = "system_owner";
                            break;
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13":
                            roleName = "security_officer";
                            break;
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14":
                            roleName = "technical_expert";
                            break;
                        default:
                            System.out.println("Unknown roleId UUID: " + roleIdStr);
                            roleName = "UNKNOWN";
                    }
                    System.out.println("Mapped roleId " + roleIdStr + " to role: " + roleName);
                }
            }
            
            payload.put("role", roleName);
            System.out.println("JWT will contain role: " + roleName);
            
            payload.put("iat", System.currentTimeMillis() / 1000);
            payload.put("exp", (System.currentTimeMillis() + EXPIRATION_TIME) / 1000);
            
            String headerEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mapper.writeValueAsBytes(header));
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mapper.writeValueAsBytes(payload));
            
            String signature = createSignature(headerEncoded + "." + payloadEncoded);
            
            System.out.println("JWT token generated successfully with role: " + roleName);
            return headerEncoded + "." + payloadEncoded + "." + signature;
        } catch (Exception e) {
            System.out.println("Error generating JWT token: " + e.getMessage());
            e.printStackTrace();
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
     * Extracts the role ID (UUID) from the given JWT token.
     *
     * @param token the JWT token string
     * @return the role ID contained in the token, or null if extraction fails
     */
    public static UUID getRoleIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            String roleIdStr = payload.get("roleId").asText();
            return roleIdStr.isEmpty() ? null : UUID.fromString(roleIdStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts the role name from the given JWT token.
     *
     * @param token the JWT token string
     * @return the role name contained in the token, or null if extraction fails
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
     * Creates an HMAC SHA-256 signature for the given message.
     *
     * @param message the message to sign
     * @return the Base64-encoded signature
     * @throws RuntimeException if signing fails
     */
    private static String createSignature(String message) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            sha256.init(secretKeySpec);
            byte[] hash = sha256.doFinal(message.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error creating signature", e);
        }
    }
}
