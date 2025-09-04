package services;

import model.*;
import repository.BookingRepository;
import repository.RoomRepository;
import services.NotificationService;
import services.PaymentService;
import patterns.observer.BookingObserver;
import patterns.command.BookingCommand;
import patterns.command.CommandInvoker;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final CommandInvoker commandInvoker;
    private final List<BookingObserver> observers;
    private final ReentrantLock bookingLock = new ReentrantLock();

    public BookingService(BookingRepository bookingRepository, 
                         RoomRepository roomRepository,
                         NotificationService notificationService,
                         PaymentService paymentService,
                         CommandInvoker commandInvoker) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.commandInvoker = commandInvoker;
        this.observers = new ArrayList<>();
    }

    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Booking booking, String eventType) {
        for (BookingObserver observer : observers) {
            observer.onBookingUpdate(booking, eventType);
        }
    }

    public Optional<Booking> createBooking(Long userId, int roomNumber, 
                                         LocalDate checkIn, LocalDate checkOut) {
        bookingLock.lock();
        try {
            Optional<Room> roomOpt = roomRepository.findByRoomNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                return Optional.empty();
            }

            Room room = roomOpt.get();
            if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
                return Optional.empty();
            }

            double totalPrice = calculateTotalPrice(room, checkIn, checkOut);
            Booking booking = new Booking(userId, roomNumber, checkIn, checkOut, totalPrice);
            
            BookingCommand bookCommand = new BookingCommand(booking, this::executeBooking);
            commandInvoker.executeCommand(bookCommand);
            
            Booking savedBooking = bookingRepository.save(booking);
            
            // Process payment
            PaymentResult paymentResult = paymentService.processPayment(
                userId, totalPrice, "Booking for Room " + roomNumber);
            
            if (paymentResult.isSuccessful()) {
                savedBooking.setPaymentStatus(PaymentStatus.COMPLETED);
                savedBooking.setPaymentId(paymentResult.getPaymentId());
                bookingRepository.save(savedBooking);
                
                // Update room availability
                room.setAvailable(false);
                roomRepository.save(room);
                
                // Notify observers
                notifyObservers(savedBooking, "BOOKING_CREATED");
                
                // Send notification
                notificationService.sendBookingConfirmation(savedBooking);
                
                return Optional.of(savedBooking);
            } else {
                // Payment failed, cleanup booking
                bookingRepository.delete(savedBooking);
                return Optional.empty();
            }
        } finally {
            bookingLock.unlock();
        }
    }

    private void executeBooking(Booking booking) {
        // Core booking logic
        booking.setBookingStatus(BookingStatus.CONFIRMED);
    }

    public boolean cancelBooking(Long bookingId, Long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return false;
        }

        Booking booking = bookingOpt.get();
        if (!booking.getUserId().equals(userId)) {
            return false; // User can only cancel their own bookings
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Make room available again
        Optional<Room> roomOpt = roomRepository.findByRoomNumber(booking.getRoomNumber());
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setAvailable(true);
            roomRepository.save(room);
        }

        // Process refund if applicable
        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            paymentService.processRefund(booking.getPaymentId(), booking.getTotalPrice());
        }

        // Notify observers
        notifyObservers(booking, "BOOKING_CANCELLED");

        // Send notification
        notificationService.sendCancellationConfirmation(booking);

        return true;
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
            roomNumber, checkIn, checkOut);
        return conflictingBookings.isEmpty();
    }

    private double calculateTotalPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        double basePrice = room.getPrice() * nights;
        
        // Apply discount for extended stays
        if (nights > 5) {
            basePrice *= 0.9; // 10% discount
        }
        
        return basePrice;
    }

    public void undoLastBooking() {
        commandInvoker.undoLastCommand();
    }
}