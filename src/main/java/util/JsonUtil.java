package util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // convert object to JSON string
    public static String toJson(Object object) {
        // TODO: implement JSON serialization
        return null;
    }

    // convert JSON string to object
    public static <T> T fromJson(String json, Class<T> clazz) {
        // TODO: implement JSON deserialization
        return null;
    }

    // check if string is valid JSON
    public static boolean isValidJson(String json) {
        // TODO: validate JSON format
        return false;
    }
}
