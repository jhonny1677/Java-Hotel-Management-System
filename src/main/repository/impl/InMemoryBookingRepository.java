package repository.impl;

import model.Booking;
import model.BookingStatus;
import repository.BookingRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idGenerator.getAndIncrement());
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return bookings.remove(id) != null;
    }

    @Override
    public void delete(Booking booking) {
        if (booking.getId() != null) {
            bookings.remove(booking.getId());
        }
    }

    @Override
    public boolean exists(Long id) {
        return bookings.containsKey(id);
    }

    @Override
    public long count() {
        return bookings.size();
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookings.values().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByRoomNumber(int roomNumber) {
        return bookings.values().stream()
                .filter(booking -> booking.getRoomNumber() == roomNumber)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findConflictingBookings(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return bookings.values().stream()
                .filter(booking -> booking.getRoomNumber() == roomNumber)
                .filter(booking -> booking.getBookingStatus().isActive())
                .filter(booking -> {
                    LocalDate existingCheckIn = booking.getCheckInDate();
                    LocalDate existingCheckOut = booking.getCheckOutDate();
                    
                    // Check for date overlap
                    return !(checkOut.isBefore(existingCheckIn) || checkIn.isAfter(existingCheckOut) || checkOut.isEqual(existingCheckIn) || checkIn.isEqual(existingCheckOut));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookings.values().stream()
                .filter(booking -> {
                    LocalDate checkIn = booking.getCheckInDate();
                    LocalDate checkOut = booking.getCheckOutDate();
                    
                    // Booking overlaps with the date range
                    return !(checkOut.isBefore(startDate) || checkIn.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }
}