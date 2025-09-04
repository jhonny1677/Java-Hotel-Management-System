package auth;

import model.User;

public class SecurityContext {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentToken(String token) {
        currentToken.set(token);
    }

    public static String getCurrentToken() {
        return currentToken.get();
    }

    public static boolean isAuthenticated() {
        return currentUser.get() != null;
    }

    public static boolean hasRole(Role role) {
        User user = currentUser.get();
        return user != null && user.getRole() == role;
    }

    public static boolean hasPermission(Permission permission) {
        User user = currentUser.get();
        return user != null && user.getRole().hasPermission(permission);
    }

    public static Long getCurrentUserId() {
        User user = currentUser.get();
        return user != null ? user.getId() : null;
    }

    public static void clear() {
        currentUser.remove();
        currentToken.remove();
    }
}