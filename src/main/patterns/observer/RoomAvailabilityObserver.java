package patterns.observer;

import model.Booking;
import services.NotificationService;

public class RoomAvailabilityObserver implements BookingObserver {
    private final NotificationService notificationService;

    public RoomAvailabilityObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onBookingUpdate(Booking booking, String eventType) {
        if ("BOOKING_CANCELLED".equals(eventType)) {
            // Room became available, notify interested parties
            notificationService.sendRoomAvailabilityAlert(
                booking.getRoomNumber(), 
                getRoomType(booking.getRoomNumber())
            );
        }
    }

    private String getRoomType(int roomNumber) {
        // Simplified logic - in real system, would query room repository
        if (roomNumber % 3 == 1) return "Single";
        else if (roomNumber % 3 == 2) return "Double";
        else return "Suite";
    }
}