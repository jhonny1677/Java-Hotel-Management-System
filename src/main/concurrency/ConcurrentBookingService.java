package concurrency;

import model.*;
import repository.BookingRepository;
import repository.RoomRepository;
import services.NotificationService;
import services.PaymentService;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentBookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final BookingLockManager lockManager;
    private final ExecutorService executorService;

    public ConcurrentBookingService(BookingRepository bookingRepository,
                                  RoomRepository roomRepository,
                                  NotificationService notificationService,
                                  PaymentService paymentService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.lockManager = new BookingLockManager();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public CompletableFuture<BookingResult> createBookingAsync(Long userId, int roomNumber, 
                                                             LocalDate checkIn, LocalDate checkOut) {
        return CompletableFuture.supplyAsync(() -> {
            return createBooking(userId, roomNumber, checkIn, checkOut);
        }, executorService);
    }

    public BookingResult createBooking(Long userId, int roomNumber, 
                                     LocalDate checkIn, LocalDate checkOut) {
        // Acquire lock for the room
        if (!lockManager.acquireRoomLock(roomNumber, "BOOKING_CREATE")) {
            return BookingResult.failure("Room is currently being booked by another user. Please try again.");
        }

        try {
            // Double-check room availability under lock
            if (!isRoomAvailableForPeriod(roomNumber, checkIn, checkOut)) {
                return BookingResult.failure("Room is no longer available for the selected dates.");
            }

            // Get room details
            Optional<Room> roomOpt = roomRepository.findByRoomNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                return BookingResult.failure("Room not found.");
            }

            Room room = roomOpt.get();
            double totalPrice = calculateTotalPrice(room, checkIn, checkOut);

            // Create booking
            Booking booking = new Booking(userId, roomNumber, checkIn, checkOut, totalPrice);
            booking.setBookingStatus(BookingStatus.PENDING);

            // Save booking
            Booking savedBooking = bookingRepository.save(booking);

            // Process payment asynchronously
            CompletableFuture<PaymentResult> paymentFuture = CompletableFuture.supplyAsync(() -> {
                return paymentService.processPayment(userId, totalPrice, 
                    "Booking for Room " + roomNumber);
            }, executorService);

            try {
                PaymentResult paymentResult = paymentFuture.get(30, TimeUnit.SECONDS);
                
                if (paymentResult.isSuccessful()) {
                    // Update booking with payment info
                    savedBooking.setPaymentStatus(PaymentStatus.COMPLETED);
                    savedBooking.setPaymentId(paymentResult.getPaymentId());
                    savedBooking.setBookingStatus(BookingStatus.CONFIRMED);
                    bookingRepository.save(savedBooking);

                    // Update room availability
                    room.setAvailable(false);
                    roomRepository.save(room);

                    // Send confirmation notification asynchronously
                    CompletableFuture.runAsync(() -> {
                        notificationService.sendBookingConfirmation(savedBooking);
                    }, executorService);

                    return BookingResult.success(savedBooking, "Booking confirmed successfully.");
                } else {
                    // Payment failed, cleanup booking
                    savedBooking.setPaymentStatus(PaymentStatus.FAILED);
                    savedBooking.setBookingStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(savedBooking);
                    
                    return BookingResult.failure("Payment failed: " + paymentResult.getMessage());
                }
            } catch (Exception e) {
                // Payment timeout or error
                savedBooking.setPaymentStatus(PaymentStatus.FAILED);
                savedBooking.setBookingStatus(BookingStatus.CANCELLED);
                bookingRepository.save(savedBooking);
                
                return BookingResult.failure("Payment processing failed. Please try again.");
            }

        } finally {
            lockManager.releaseRoomLock(roomNumber);
        }
    }

    public CompletableFuture<Boolean> cancelBookingAsync(Long bookingId, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            return cancelBooking(bookingId, userId);
        }, executorService);
    }

    public boolean cancelBooking(Long bookingId, Long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return false;
        }

        Booking booking = bookingOpt.get();
        if (!booking.getUserId().equals(userId)) {
            return false;
        }

        if (!booking.getBookingStatus().isCancellable()) {
            return false;
        }

        // Acquire lock for the room
        if (!lockManager.acquireRoomLock(booking.getRoomNumber(), "BOOKING_CANCEL")) {
            return false;
        }

        try {
            // Update booking status
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // Make room available again
            Optional<Room> roomOpt = roomRepository.findByRoomNumber(booking.getRoomNumber());
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                room.setAvailable(true);
                roomRepository.save(room);
            }

            // Process refund asynchronously if payment was completed
            if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
                CompletableFuture.runAsync(() -> {
                    paymentService.processRefund(booking.getPaymentId(), booking.getTotalPrice());
                }, executorService);
            }

            // Send cancellation notification asynchronously
            CompletableFuture.runAsync(() -> {
                notificationService.sendCancellationConfirmation(booking);
            }, executorService);

            return true;

        } finally {
            lockManager.releaseRoomLock(booking.getRoomNumber());
        }
    }

    public CompletableFuture<Boolean> checkInAsync(Long bookingId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return false;
            }

            Booking booking = bookingOpt.get();
            if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
                return false;
            }

            booking.setBookingStatus(BookingStatus.CHECKED_IN);
            bookingRepository.save(booking);
            return true;
        }, executorService);
    }

    public CompletableFuture<Boolean> checkOutAsync(Long bookingId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return false;
            }

            Booking booking = bookingOpt.get();
            if (booking.getBookingStatus() != BookingStatus.CHECKED_IN) {
                return false;
            }

            if (!lockManager.acquireRoomLock(booking.getRoomNumber(), "CHECK_OUT")) {
                return false;
            }

            try {
                booking.setBookingStatus(BookingStatus.CHECKED_OUT);
                bookingRepository.save(booking);

                // Make room available for new bookings
                Optional<Room> roomOpt = roomRepository.findByRoomNumber(booking.getRoomNumber());
                if (roomOpt.isPresent()) {
                    Room room = roomOpt.get();
                    room.setAvailable(true);
                    roomRepository.save(room);
                }

                return true;
            } finally {
                lockManager.releaseRoomLock(booking.getRoomNumber());
            }
        }, executorService);
    }

    private boolean isRoomAvailableForPeriod(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.findConflictingBookings(roomNumber, checkIn, checkOut).isEmpty();
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

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static class BookingResult {
        private final boolean successful;
        private final Booking booking;
        private final String message;

        private BookingResult(boolean successful, Booking booking, String message) {
            this.successful = successful;
            this.booking = booking;
            this.message = message;
        }

        public static BookingResult success(Booking booking, String message) {
            return new BookingResult(true, booking, message);
        }

        public static BookingResult failure(String message) {
            return new BookingResult(false, null, message);
        }

        public boolean isSuccessful() { return successful; }
        public Booking getBooking() { return booking; }
        public String getMessage() { return message; }
    }
}