package services;

import model.User;
import repository.UserRepository;
import auth.AuthenticationService;
import auth.Role;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authService;

    public UserService(UserRepository userRepository, AuthenticationService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public Optional<User> authenticateUser(String email, String password) {
        return authService.authenticateUser(email, password);
    }

    public User createUser(String name, String email, String password, Role role) {
        User user = new User(name, email, password, role);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        user.setLastModified(LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean deleteUser(Long userId) {
        return userRepository.deleteById(userId);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (authService.validatePassword(oldPassword, user.getPasswordHash())) {
                user.setPasswordHash(authService.hashPassword(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}