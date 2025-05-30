package security;

import model.User;
import java.util.Base64;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JWTUtil {
    private static final String SECRET_KEY = "mySecretKeyForJWTTokenGenerationThatIsLongEnough";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    private static final ObjectMapper mapper = new ObjectMapper();

    // generate JWT token for user
    public static String generateToken(User user) {
        try {
            // Header
            ObjectNode header = mapper.createObjectNode();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            // Payload
            ObjectNode payload = mapper.createObjectNode();
            payload.put("sub", user.getUsername());
            payload.put("userId", user.getId());
            payload.put("role", user.getRole().toString());
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

    // validate JWT token
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String headerAndPayload = parts[0] + "." + parts[1];
            String signature = parts[2];

            String expectedSignature = createSignature(headerAndPayload);

            if (!signature.equals(expectedSignature)) return false;

            // Check expiration
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            long exp = payload.get("exp").asLong();
            return exp > (System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            return false;
        }
    }

    // extract username from token
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

    // extract user ID from token
    public static Long getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            return payload.get("userId").asLong();
        } catch (Exception e) {
            return null;
        }
    }

    // extract role from token
    public static String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            ObjectNode payload = (ObjectNode) mapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );
            return payload.get("role").asText();
        } catch (Exception e) {
            return null;
        }
    }

    // check if token is expired
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
