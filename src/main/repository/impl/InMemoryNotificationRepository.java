package repository.impl;

import model.Notification;
import repository.NotificationRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idGenerator.getAndIncrement());
        }
        notifications.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notifications.get(id));
    }

    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return notifications.remove(id) != null;
    }

    @Override
    public void delete(Notification notification) {
        if (notification.getId() != null) {
            notifications.remove(notification.getId());
        }
    }

    @Override
    public boolean exists(Long id) {
        return notifications.containsKey(id);
    }

    @Override
    public long count() {
        return notifications.size();
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndReadStatus(Long userId, boolean isRead) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .filter(notification -> notification.isRead() == isRead)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadNotifications() {
        return notifications.values().stream()
                .filter(notification -> !notification.isRead())
                .collect(Collectors.toList());
    }
}