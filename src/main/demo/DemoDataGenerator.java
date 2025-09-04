package demo;

import config.ApplicationContext;
import model.*;
import auth.Role;
import patterns.factory.RoomFactory;
import payment.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class DemoDataGenerator {
    private final ApplicationContext context;
    private final Random random = new Random();

    public DemoDataGenerator(ApplicationContext context) {
        this.context = context;
    }

    public void generateDemoData() {
        System.out.println("🚀 Generating comprehensive demo data...");
        
        createDemoRooms();
        createDemoBookings();
        createDemoPayments();
        
        System.out.println("✅ Demo data generation complete!");
        printDemoSummary();
    }

    private void createDemoRooms() {
        System.out.println("📦 Creating demo rooms with factory pattern...");
        
        // Create rooms using factory pattern
        for (int i = 1; i <= 50; i++) {
            RoomFactory.RoomType roomType;
            if (i % 5 == 0) {
                roomType = RoomFactory.RoomType.PRESIDENTIAL;
            } else if (i % 4 == 0) {
                roomType = RoomFactory.RoomType.DELUXE;
            } else if (i % 3 == 0) {
                roomType = RoomFactory.RoomType.SUITE;
            } else if (i % 2 == 0) {
                roomType = RoomFactory.RoomType.DOUBLE;
            } else {
                roomType = RoomFactory.RoomType.SINGLE;
            }
            
            Room room = RoomFactory.createRoom(i, roomType);
            room.setDescription("Demo room created with factory pattern");
            
            // Randomly make some rooms unavailable to show booking system
            if (random.nextDouble() < 0.3) {
                room.setAvailable(false);
            }
            
            context.getRoomRepository().save(room);
        }
        
        System.out.printf("   ✓ Created %d rooms with different types and configurations\n", 50);
    }

    private void createDemoBookings() {
        System.out.println("📅 Creating demo bookings to test analytics...");
        
        List<User> users = context.getUserRepository().findAll();
        
        // Create bookings for the past 3 months
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        int bookingCount = 0;
        
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            // Create 1-5 bookings per day randomly
            int dailyBookings = random.nextInt(5) + 1;
            
            for (int i = 0; i < dailyBookings; i++) {
                try {
                    User user = users.get(random.nextInt(users.size()));
                    int roomNumber = random.nextInt(50) + 1;
                    LocalDate checkOut = date.plusDays(random.nextInt(7) + 1);
                    
                    // Check if room exists and booking is possible
                    var roomOpt = context.getRoomRepository().findByRoomNumber(roomNumber);
                    if (roomOpt.isPresent()) {
                        Booking booking = new Booking(user.getId(), roomNumber, date, checkOut, 
                            calculateDemoPrice(roomNumber, date, checkOut));
                        
                        // Set various booking statuses for demo
                        BookingStatus status = getRandomBookingStatus(date);
                        booking.setBookingStatus(status);
                        booking.setPaymentStatus(PaymentStatus.COMPLETED);
                        booking.setCreatedAt(date.atStartOfDay());
                        
                        context.getBookingRepository().save(booking);
                        bookingCount++;
                    }
                } catch (Exception e) {
                    // Skip this booking if there's an error
                }
            }
        }
        
        System.out.printf("   ✓ Created %d demo bookings across 3 months\n", bookingCount);
    }

    private void createDemoPayments() {
        System.out.println("💳 Creating demo payments for analytics...");
        
        List<Booking> bookings = context.getBookingRepository().findAll();
        int paymentCount = 0;
        
        for (Booking booking : bookings) {
            if (booking.getBookingStatus() != BookingStatus.CANCELLED) {
                Payment payment = new Payment();
                payment.setPaymentId("demo_" + booking.getId() + "_" + System.currentTimeMillis());
                payment.setUserId(booking.getUserId());
                payment.setAmount(booking.getTotalPrice());
                payment.setDescription("Payment for booking #" + booking.getId());
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(booking.getCreatedAt());
                
                context.getPaymentRepository().save(payment);
                paymentCount++;
            }
        }
        
        // Create some refund examples
        List<Payment> payments = context.getPaymentRepository().findAll();
        int refundCount = 0;
        
        for (int i = 0; i < Math.min(5, payments.size()); i++) {
            Payment originalPayment = payments.get(i);
            
            Payment refund = new Payment();
            refund.setPaymentId("refund_" + System.currentTimeMillis() + "_" + i);
            refund.setUserId(originalPayment.getUserId());
            refund.setAmount(-originalPayment.getAmount() * 0.8); // Partial refund
            refund.setDescription("Refund for: " + originalPayment.getDescription());
            refund.setStatus(PaymentStatus.COMPLETED);
            refund.setPaymentDate(LocalDateTime.now().minusDays(random.nextInt(30)));
            refund.setOriginalPaymentId(originalPayment.getPaymentId());
            
            context.getPaymentRepository().save(refund);
            refundCount++;
        }
        
        System.out.printf("   ✓ Created %d payments and %d refunds\n", paymentCount, refundCount);
    }

    private double calculateDemoPrice(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        // Use the dynamic pricing engine for realistic prices
        var roomOpt = context.getRoomRepository().findByRoomNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            return context.getPricingEngine().calculateDynamicPrice(room, checkIn, checkOut);
        }
        return 100.0; // Default price
    }

    private BookingStatus getRandomBookingStatus(LocalDate bookingDate) {
        LocalDate now = LocalDate.now();
        
        if (bookingDate.isAfter(now)) {
            return BookingStatus.CONFIRMED;
        } else if (bookingDate.isAfter(now.minusDays(30))) {
            // Recent bookings - mix of statuses
            double rand = random.nextDouble();
            if (rand < 0.7) return BookingStatus.CHECKED_OUT;
            else if (rand < 0.85) return BookingStatus.CHECKED_IN;
            else if (rand < 0.95) return BookingStatus.CANCELLED;
            else return BookingStatus.NO_SHOW;
        } else {
            // Older bookings - mostly completed
            double rand = random.nextDouble();
            if (rand < 0.9) return BookingStatus.CHECKED_OUT;
            else return BookingStatus.CANCELLED;
        }
    }

    private void printDemoSummary() {
        System.out.println("\n📊 DEMO DATA SUMMARY");
        System.out.println("====================");
        System.out.printf("Users: %d\n", context.getUserRepository().count());
        System.out.printf("Rooms: %d\n", context.getRoomRepository().count());
        System.out.printf("Bookings: %d\n", context.getBookingRepository().count());
        System.out.printf("Payments: %d\n", context.getPaymentRepository().count());
        
        System.out.println("\n🎯 KEY FEATURES TO TEST:");
        System.out.println("• Authentication: Try different user roles");
        System.out.println("• Async Booking: Create bookings with concurrency control");
        System.out.println("• Dynamic Pricing: Calculate prices for different dates");
        System.out.println("• Analytics: View revenue reports and forecasts");
        System.out.println("• Payment Gateways: Check gateway status and processing");
        System.out.println("• Caching: Monitor cache performance and statistics");
        System.out.println("• Command Pattern: Test undo functionality");
        System.out.println("• Observer Pattern: Book/cancel rooms to see notifications");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("🏨 Enterprise Hotel Management Demo Data Generator");
        System.out.println("=================================================");
        
        ApplicationContext context = new ApplicationContext();
        DemoDataGenerator generator = new DemoDataGenerator(context);
        generator.generateDemoData();
        
        context.shutdown();
    }
}