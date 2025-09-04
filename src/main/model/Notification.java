package model;

import java.time.LocalDateTime;
import org.json.JSONObject;
import persistence.Writable;

public class Notification implements Writable {
    private Long id;
    private Long userId;
    private String subject;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(Long userId, String subject, String message, NotificationType type, LocalDateTime createdAt) {
        this();
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { 
        this.isRead = read; 
        if (read && readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("userId", userId);
        json.put("subject", subject);
        json.put("message", message);
        json.put("type", type != null ? type.name() : null);
        json.put("isRead", isRead);
        json.put("createdAt", createdAt != null ? createdAt.toString() : null);
        json.put("readAt", readAt != null ? readAt.toString() : null);
        return json;
    }

    @Override
    public String toString() {
        return String.format("Notification{id=%d, userId=%d, subject='%s', type=%s, read=%s}", 
                           id, userId, subject, type, isRead);
    }
}