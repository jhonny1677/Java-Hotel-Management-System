package patterns.observer;

import model.Booking;

public interface BookingObserver {
    void onBookingUpdate(Booking booking, String eventType);
}