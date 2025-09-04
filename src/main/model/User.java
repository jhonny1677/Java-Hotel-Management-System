package model;

import auth.Role;
import java.time.LocalDateTime;
import org.json.JSONObject;
import persistence.Writable;

public class User implements Writable {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private LocalDateTime lastLogin;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.active = true;
    }

    public User(String name, String email, String passwordHash, Role role) {
        this();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
        this.lastModified = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email; 
        this.lastModified = LocalDateTime.now();
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
        this.lastModified = LocalDateTime.now();
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber; 
        this.lastModified = LocalDateTime.now();
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { 
        this.role = role; 
        this.lastModified = LocalDateTime.now();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { 
        this.active = active; 
        this.lastModified = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("email", email);
        json.put("passwordHash", passwordHash);
        json.put("phoneNumber", phoneNumber);
        json.put("role", role != null ? role.name() : null);
        json.put("active", active);
        json.put("createdAt", createdAt != null ? createdAt.toString() : null);
        json.put("lastModified", lastModified != null ? lastModified.toString() : null);
        json.put("lastLogin", lastLogin != null ? lastLogin.toString() : null);
        return json;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', role=%s, active=%s}", 
                           id, name, email, role, active);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}