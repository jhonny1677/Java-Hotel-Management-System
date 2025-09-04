package repository;

import model.Notification;
import java.util.List;

public interface NotificationRepository extends Repository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndReadStatus(Long userId, boolean isRead);
    List<Notification> findUnreadNotifications();
}