package auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class JWTService {
    private static final String SECRET_KEY = "MySecretKeyForJWTTokenGenerationAndValidation123!";
    private static final long JWT_EXPIRATION = 86400000; // 24 hours in milliseconds
    private static final long REFRESH_EXPIRATION = 604800000; // 7 days in milliseconds

    public String generateAccessToken(Long userId, String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role.name());
        claims.put("type", "ACCESS");
        
        return createToken(claims, email, JWT_EXPIRATION);
    }

    public String generateRefreshToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "REFRESH");
        
        return createToken(claims, email, REFRESH_EXPIRATION);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        // Simplified JWT creation (in production, use a proper JWT library like JJWT)
        long now = System.currentTimeMillis();
        Date expirationDate = new Date(now + expiration);
        
        // Create header
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(header.getBytes(StandardCharsets.UTF_8));
        
        // Create payload
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("{");
        payloadBuilder.append("\"sub\":\"").append(subject).append("\",");
        payloadBuilder.append("\"iat\":").append(now / 1000).append(",");
        payloadBuilder.append("\"exp\":").append(expirationDate.getTime() / 1000);
        
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            payloadBuilder.append(",\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                payloadBuilder.append("\"").append(entry.getValue()).append("\"");
            } else {
                payloadBuilder.append(entry.getValue());
            }
        }
        payloadBuilder.append("}");
        
        String payload = payloadBuilder.toString();
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        
        // Create signature (simplified - use HMAC SHA256 in production)
        String data = encodedHeader + "." + encodedPayload;
        String signature = createSignature(data);
        
        return data + "." + signature;
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            String data = parts[0] + "." + parts[1];
            String signature = parts[2];
            
            // Verify signature
            if (!signature.equals(createSignature(data))) {
                return false;
            }
            
            // Check expiration
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            long exp = extractExpirationFromPayload(payload);
            
            return System.currentTimeMillis() / 1000 < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        try {
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
            return extractLongFromPayload(payload, "userId");
        } catch (Exception e) {
            return null;
        }
    }

    public String extractEmail(String token) {
        try {
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
            return extractStringFromPayload(payload, "email");
        } catch (Exception e) {
            return null;
        }
    }

    public Role extractRole(String token) {
        try {
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
            String roleStr = extractStringFromPayload(payload, "role");
            return Role.valueOf(roleStr);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
            long exp = extractExpirationFromPayload(payload);
            return System.currentTimeMillis() / 1000 >= exp;
        } catch (Exception e) {
            return true;
        }
    }

    private String createSignature(String data) {
        // Simplified signature creation (use proper HMAC in production)
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString((data + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
    }

    private long extractExpirationFromPayload(String payload) {
        // Simple JSON parsing for exp field
        String expPattern = "\"exp\":";
        int startIndex = payload.indexOf(expPattern) + expPattern.length();
        int endIndex = payload.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = payload.indexOf("}", startIndex);
        
        return Long.parseLong(payload.substring(startIndex, endIndex));
    }

    private Long extractLongFromPayload(String payload, String field) {
        String pattern = "\"" + field + "\":";
        int startIndex = payload.indexOf(pattern) + pattern.length();
        int endIndex = payload.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = payload.indexOf("}", startIndex);
        
        return Long.parseLong(payload.substring(startIndex, endIndex));
    }

    private String extractStringFromPayload(String payload, String field) {
        String pattern = "\"" + field + "\":\"";
        int startIndex = payload.indexOf(pattern) + pattern.length();
        int endIndex = payload.indexOf("\"", startIndex);
        
        return payload.substring(startIndex, endIndex);
    }
}