package repository;

import model.Booking;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends Repository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByRoomNumber(int roomNumber);
    List<Booking> findConflictingBookings(int roomNumber, LocalDate checkIn, LocalDate checkOut);
    List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate);
}