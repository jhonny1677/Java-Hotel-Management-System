package services;

import model.Booking;
import model.User;
import model.Notification;
import model.NotificationType;
import repository.NotificationRepository;
import repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, 
                             UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void sendBookingConfirmation(Booking booking) {
        CompletableFuture.runAsync(() -> {
            Optional<User> userOpt = userRepository.findById(booking.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String subject = "Booking Confirmation - Room " + booking.getRoomNumber();
                String message = buildBookingConfirmationMessage(booking, user);
                
                Notification notification = new Notification(
                    booking.getUserId(),
                    subject,
                    message,
                    NotificationType.BOOKING_CONFIRMATION,
                    LocalDateTime.now()
                );
                
                notificationRepository.save(notification);
                sendEmail(user.getEmail(), subject, message);
                sendSMS(user.getPhoneNumber(), "Booking confirmed for Room " + booking.getRoomNumber());
            }
        });
    }

    public void sendCancellationConfirmation(Booking booking) {
        CompletableFuture.runAsync(() -> {
            Optional<User> userOpt = userRepository.findById(booking.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String subject = "Booking Cancellation - Room " + booking.getRoomNumber();
                String message = buildCancellationMessage(booking, user);
                
                Notification notification = new Notification(
                    booking.getUserId(),
                    subject,
                    message,
                    NotificationType.BOOKING_CANCELLATION,
                    LocalDateTime.now()
                );
                
                notificationRepository.save(notification);
                sendEmail(user.getEmail(), subject, message);
            }
        });
    }

    public void sendPaymentFailureNotification(Long userId, String reason) {
        CompletableFuture.runAsync(() -> {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String subject = "Payment Failed";
                String message = "Your payment could not be processed. Reason: " + reason;
                
                Notification notification = new Notification(
                    userId,
                    subject,
                    message,
                    NotificationType.PAYMENT_FAILURE,
                    LocalDateTime.now()
                );
                
                notificationRepository.save(notification);
                sendEmail(user.getEmail(), subject, message);
            }
        });
    }

    public void sendPromotionalNotification(List<Long> userIds, String subject, String message) {
        CompletableFuture.runAsync(() -> {
            for (Long userId : userIds) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    Notification notification = new Notification(
                        userId,
                        subject,
                        message,
                        NotificationType.PROMOTIONAL,
                        LocalDateTime.now()
                    );
                    
                    notificationRepository.save(notification);
                    sendEmail(user.getEmail(), subject, message);
                }
            }
        });
    }

    public void sendRoomAvailabilityAlert(int roomNumber, String roomType) {
        // Find users who might be interested in this room type
        CompletableFuture.runAsync(() -> {
            String subject = "Room Available - " + roomType + " Room " + roomNumber;
            String message = "Good news! A " + roomType + " room is now available for booking.";
            
            // In a real system, this would query users based on preferences
            List<User> interestedUsers = userRepository.findAll(); // Simplified
            
            for (User user : interestedUsers) {
                Notification notification = new Notification(
                    user.getId(),
                    subject,
                    message,
                    NotificationType.ROOM_AVAILABILITY,
                    LocalDateTime.now()
                );
                
                notificationRepository.save(notification);
            }
        });
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadStatus(userId, false);
    }

    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    private String buildBookingConfirmationMessage(Booking booking, User user) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking has been confirmed!\n\n" +
            "Booking Details:\n" +
            "Room Number: %d\n" +
            "Check-in: %s\n" +
            "Check-out: %s\n" +
            "Total Amount: $%.2f\n\n" +
            "Thank you for choosing our hotel!\n\n" +
            "Best regards,\n" +
            "Hotel Management Team",
            user.getName(),
            booking.getRoomNumber(),
            booking.getCheckInDate(),
            booking.getCheckOutDate(),
            booking.getTotalPrice()
        );
    }

    private String buildCancellationMessage(Booking booking, User user) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking for Room %d has been cancelled.\n" +
            "If applicable, your refund will be processed within 3-5 business days.\n\n" +
            "We hope to serve you again in the future.\n\n" +
            "Best regards,\n" +
            "Hotel Management Team",
            user.getName(),
            booking.getRoomNumber()
        );
    }

    private void sendEmail(String email, String subject, String message) {
        // Simulate email sending (in real implementation, use SendGrid, SES, etc.)
        System.out.printf("EMAIL SENT to %s: %s\n", email, subject);
    }

    private void sendSMS(String phoneNumber, String message) {
        // Simulate SMS sending (in real implementation, use Twilio, etc.)
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            System.out.printf("SMS SENT to %s: %s\n", phoneNumber, message);
        }
    }
}