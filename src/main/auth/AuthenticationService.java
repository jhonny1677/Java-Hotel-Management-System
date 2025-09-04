package auth;

import model.User;
import repository.UserRepository;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

public class AuthenticationService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final SecureRandom secureRandom;

    public AuthenticationService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.secureRandom = new SecureRandom();
    }

    public AuthResult authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return AuthResult.failure("User not found");
        }

        User user = userOpt.get();
        if (!validatePassword(password, user.getPasswordHash())) {
            return AuthResult.failure("Invalid password");
        }

        if (!user.isActive()) {
            return AuthResult.failure("Account is deactivated");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResult.success(user, accessToken, refreshToken);
    }

    public Optional<User> authenticateUser(String email, String password) {
        AuthResult result = authenticate(email, password);
        return result.isSuccessful() ? Optional.of(result.getUser()) : Optional.empty();
    }

    public String hashPassword(String password) {
        try {
            // Generate salt
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean validatePassword(String password, String hashedPassword) {
        try {
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            
            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            
            // Extract hash (remaining bytes)
            byte[] hash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, hash, 0, hash.length);
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Compare hashes
            return MessageDigest.isEqual(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }

    public AuthResult refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            return AuthResult.failure("Invalid refresh token");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);

        if (userId == null || email == null) {
            return AuthResult.failure("Invalid token data");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return AuthResult.failure("User not found");
        }

        User user = userOpt.get();
        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResult.success(user, newAccessToken, newRefreshToken);
    }

    public boolean hasPermission(String token, Permission permission) {
        if (!jwtService.validateToken(token)) {
            return false;
        }

        Role role = jwtService.extractRole(token);
        return role != null && role.hasPermission(permission);
    }

    public boolean isTokenValid(String token) {
        return jwtService.validateToken(token);
    }

    public static class AuthResult {
        private final boolean successful;
        private final String message;
        private final User user;
        private final String accessToken;
        private final String refreshToken;

        private AuthResult(boolean successful, String message, User user, String accessToken, String refreshToken) {
            this.successful = successful;
            this.message = message;
            this.user = user;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public static AuthResult success(User user, String accessToken, String refreshToken) {
            return new AuthResult(true, "Authentication successful", user, accessToken, refreshToken);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, message, null, null, null);
        }

        public boolean isSuccessful() { return successful; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
    }
}