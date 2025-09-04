package config;

import model.*;
import auth.Role;
import patterns.factory.RoomFactory;
import repository.*;
import services.PaymentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class DatabaseInitializer {
    private final ApplicationContext context;
    private final Random random = new Random();

    public DatabaseInitializer(ApplicationContext context) {
        this.context = context;
    }

    public void initializeProductionData() {
        System.out.println("[INIT] Initializing production-ready data...");
        
        createHotelRooms();
        createSampleBookings();
        createRevenueHistory();
        
        System.out.println("✅ Production data initialization complete!");
        printSystemStats();
    }

    private void createHotelRooms() {
        System.out.println("[ROOMS] Creating hotel room inventory...");
        
        // Floor 1: Budget rooms (1-20)
        for (int i = 1; i <= 20; i++) {
            Room room = RoomFactory.createRoom(i, RoomFactory.RoomType.SINGLE);
            room.setDescription("Budget single room on ground floor");
            context.getRoomRepository().save(room);
        }
        
        // Floor 2-3: Standard rooms (21-60)
        for (int i = 21; i <= 60; i++) {
            RoomFactory.RoomType type = (i % 2 == 0) ? 
                RoomFactory.RoomType.DOUBLE : RoomFactory.RoomType.SINGLE;
            Room room = RoomFactory.createRoom(i, type);
            room.setDescription("Standard room with city view");
            context.getRoomRepository().save(room);
        }
        
        // Floor 4-5: Premium rooms (61-90)
        for (int i = 61; i <= 90; i++) {
            Room room = RoomFactory.createRoom(i, RoomFactory.RoomType.SUITE);
            room.setDescription("Premium suite with luxury amenities");
            context.getRoomRepository().save(room);
        }
        
        // Floor 6: Luxury rooms (91-100)
        for (int i = 91; i <= 100; i++) {
            RoomFactory.RoomType type = (i > 95) ? 
                RoomFactory.RoomType.PRESIDENTIAL : RoomFactory.RoomType.DELUXE;
            Room room = RoomFactory.createRoom(i, type);
            room.setDescription("Luxury accommodation with panoramic views");
            context.getRoomRepository().save(room);
        }
        
        System.out.println("   ✓ Created 100 rooms across 6 floors with varied configurations");
    }

    private void createSampleBookings() {
        System.out.println("📅 Creating realistic booking history...");
        
        List<User> users = context.getUserRepository().findAll();
        LocalDate startDate = LocalDate.now().minusMonths(6);
        int totalBookings = 0;
        
        // Create seasonal booking patterns
        for (LocalDate date = startDate; date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
            int dailyBookings = calculateDailyBookings(date);
            
            for (int i = 0; i < dailyBookings; i++) {
                try {
                    User user = users.get(random.nextInt(users.size()));
                    int roomNumber = selectRoomBasedOnDate(date);
                    int stayLength = random.nextInt(7) + 1;
                    LocalDate checkOut = date.plusDays(stayLength);
                    
                    var roomOpt = context.getRoomRepository().findByRoomNumber(roomNumber);
                    if (roomOpt.isPresent()) {
                        Room room = roomOpt.get();
                        double price = context.getPricingEngine().calculateDynamicPrice(room, date, checkOut);
                        
                        Booking booking = new Booking(user.getId(), roomNumber, date, checkOut, price);
                        booking.setBookingStatus(determineBookingStatus(date));
                        booking.setPaymentStatus(PaymentStatus.COMPLETED);
                        booking.setCreatedAt(date.minusDays(random.nextInt(30)).atStartOfDay());
                        
                        // Add special requests for some bookings
                        if (random.nextDouble() < 0.3) {
                            booking.setSpecialRequests(generateSpecialRequest());
                        }
                        
                        context.getBookingRepository().save(booking);
                        totalBookings++;
                    }
                } catch (Exception e) {
                    // Skip problematic bookings
                }
            }
        }
        
        System.out.printf("   ✓ Created %d realistic bookings with seasonal patterns\n", totalBookings);
    }

    private void createRevenueHistory() {
        System.out.println("💰 Creating comprehensive payment history...");
        
        List<Booking> bookings = context.getBookingRepository().findAll();
        int paymentsCreated = 0;
        int refundsCreated = 0;
        
        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.CANCELLED) continue;
            
            // Create main payment
            Payment payment = new Payment();
            payment.setPaymentId(generateRealisticPaymentId());
            payment.setUserId(booking.getUserId());
            payment.setAmount(booking.getTotalPrice());
            payment.setDescription("Room reservation - " + booking.getRoomNumber());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(booking.getCreatedAt().plusHours(random.nextInt(2)));
            
            context.getPaymentRepository().save(payment);
            paymentsCreated++;
            
            // Create some refunds for cancelled bookings
            if (booking.getBookingStatus() == BookingStatus.CANCELLED && random.nextDouble() < 0.8) {
                Payment refund = new Payment();
                refund.setPaymentId(generateRealisticPaymentId());
                refund.setUserId(booking.getUserId());
                refund.setAmount(-booking.getTotalPrice() * 0.85); // 15% cancellation fee
                refund.setDescription("Refund for booking #" + booking.getId());
                refund.setStatus(PaymentStatus.COMPLETED);
                refund.setPaymentDate(booking.getCreatedAt().plusDays(random.nextInt(7)));
                refund.setOriginalPaymentId(payment.getPaymentId());
                
                context.getPaymentRepository().save(refund);
                refundsCreated++;
            }
        }
        
        System.out.printf("   ✓ Created %d payments and %d refunds\n", paymentsCreated, refundsCreated);
    }

    private int calculateDailyBookings(LocalDate date) {
        int baseBookings = 15;
        
        // Weekend boost
        if (date.getDayOfWeek().getValue() >= 6) {
            baseBookings += 8;
        }
        
        // Seasonal adjustments
        int month = date.getMonthValue();
        if (month >= 6 && month <= 8) { // Summer
            baseBookings += 12;
        } else if (month == 12 || month <= 2) { // Winter holidays
            baseBookings += 8;
        }
        
        // Holiday periods
        if (isHolidayPeriod(date)) {
            baseBookings += 15;
        }
        
        // Random variation
        baseBookings += random.nextInt(10) - 5;
        
        return Math.max(5, Math.min(35, baseBookings));
    }

    private int selectRoomBasedOnDate(LocalDate date) {
        // Premium rooms more popular on weekends and holidays
        if (date.getDayOfWeek().getValue() >= 6 || isHolidayPeriod(date)) {
            if (random.nextDouble() < 0.4) {
                return 61 + random.nextInt(40); // Premium/Luxury rooms
            }
        }
        
        // Standard distribution
        double rand = random.nextDouble();
        if (rand < 0.2) {
            return 1 + random.nextInt(20);   // Budget
        } else if (rand < 0.6) {
            return 21 + random.nextInt(40);  // Standard
        } else if (rand < 0.9) {
            return 61 + random.nextInt(30);  // Premium
        } else {
            return 91 + random.nextInt(10);  // Luxury
        }
    }

    private BookingStatus determineBookingStatus(LocalDate bookingDate) {
        LocalDate now = LocalDate.now();
        
        if (bookingDate.isAfter(now.plusDays(1))) {
            return BookingStatus.CONFIRMED;
        } else if (bookingDate.isAfter(now.minusDays(1))) {
            return random.nextDouble() < 0.8 ? BookingStatus.CHECKED_IN : BookingStatus.CONFIRMED;
        } else if (bookingDate.isAfter(now.minusDays(30))) {
            double rand = random.nextDouble();
            if (rand < 0.75) return BookingStatus.CHECKED_OUT;
            else if (rand < 0.9) return BookingStatus.CANCELLED;
            else return BookingStatus.NO_SHOW;
        } else {
            double rand = random.nextDouble();
            if (rand < 0.9) return BookingStatus.CHECKED_OUT;
            else return BookingStatus.CANCELLED;
        }
    }

    private boolean isHolidayPeriod(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        
        // New Year period
        if ((month == 12 && day >= 20) || (month == 1 && day <= 5)) return true;
        
        // Valentine's Day
        if (month == 2 && day >= 12 && day <= 16) return true;
        
        // Summer vacation peak
        if (month == 7 || (month == 8 && day <= 15)) return true;
        
        // Thanksgiving week
        if (month == 11 && day >= 20 && day <= 28) return true;
        
        return false;
    }

    private String generateSpecialRequest() {
        String[] requests = {
            "Early check-in requested",
            "Late check-out needed",
            "Quiet room preferred",
            "High floor requested",
            "Extra towels needed",
            "Room service breakfast",
            "Airport shuttle required",
            "Celebration package",
            "Business center access",
            "Spa appointment booking"
        };
        return requests[random.nextInt(requests.length)];
    }

    private String generateRealisticPaymentId() {
        String[] prefixes = {"ch_", "pi_", "pay_", "txn_"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        
        StringBuilder id = new StringBuilder(prefix);
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 24; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return id.toString();
    }

    private void printSystemStats() {
        System.out.println("\n📊 SYSTEM STATISTICS");
        System.out.println("====================");
        
        long totalUsers = context.getUserRepository().count();
        long totalRooms = context.getRoomRepository().count();
        long totalBookings = context.getBookingRepository().count();
        long totalPayments = context.getPaymentRepository().count();
        
        System.out.printf("👥 Users: %d\n", totalUsers);
        System.out.printf("Rooms: %d\n", totalRooms);
        System.out.printf("📅 Bookings: %d\n", totalBookings);
        System.out.printf("💳 Payments: %d\n", totalPayments);
        
        // Calculate occupancy rate
        List<Booking> activeBookings = context.getBookingRepository().findAll().stream()
            .filter(b -> b.getBookingStatus().isActive())
            .collect(java.util.stream.Collectors.toList());
        
        double occupancyRate = (double) activeBookings.size() / totalRooms * 100;
        System.out.printf("📈 Current Occupancy: %.1f%%\n", occupancyRate);
        
        // Calculate revenue
        double totalRevenue = context.getPaymentService().getTotalRevenue();
        System.out.printf("💰 Total Revenue: $%.2f\n", totalRevenue);
        
        System.out.println("\n🚀 Ready for enterprise-level testing!");
    }
}