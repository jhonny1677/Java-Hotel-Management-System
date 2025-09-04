package analytics;

import model.Booking;
import model.Payment;
import model.BookingStatus;
import model.PaymentStatus;
import repository.BookingRepository;
import repository.PaymentRepository;
import repository.RoomRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RevenueAnalytics {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;

    public RevenueAnalytics(BookingRepository bookingRepository, 
                          PaymentRepository paymentRepository,
                          RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
    }

    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = bookingRepository.findByDateRange(startDate, endDate);
        List<Payment> payments = paymentRepository.findSuccessfulPayments();
        
        // Filter payments by date range
        payments = payments.stream()
            .filter(p -> {
                LocalDate paymentDate = p.getPaymentDate().toLocalDate();
                return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
            })
            .collect(Collectors.toList());

        return new RevenueReport.Builder()
            .period(startDate, endDate)
            .totalRevenue(calculateTotalRevenue(payments))
            .totalBookings(bookings.size())
            .averageDailyRate(calculateADR(bookings, payments))
            .revenuePerAvailableRoom(calculateRevPAR(bookings, payments, startDate, endDate))
            .occupancyRate(calculateOccupancyRate(bookings, startDate, endDate))
            .revenueByRoomType(calculateRevenueByRoomType(bookings, payments))
            .dailyRevenue(calculateDailyRevenue(payments))
            .monthlyRevenue(calculateMonthlyRevenue(payments))
            .topPerformingRooms(getTopPerformingRooms(bookings, payments))
            .averageBookingValue(calculateAverageBookingValue(payments))
            .cancellationRate(calculateCancellationRate(bookings))
            .noShowRate(calculateNoShowRate(bookings))
            .build();
    }

    private double calculateTotalRevenue(List<Payment> payments) {
        return payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
            .filter(p -> p.getAmount() > 0) // Exclude refunds
            .mapToDouble(Payment::getAmount)
            .sum();
    }

    private double calculateADR(List<Booking> bookings, List<Payment> payments) {
        Map<Long, Double> bookingPayments = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
            .collect(Collectors.toMap(
                Payment::getId, // Assuming payment ID maps to booking
                Payment::getAmount,
                (existing, replacement) -> existing
            ));

        long totalNights = bookings.stream()
            .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
            .mapToLong(Booking::getNumberOfNights)
            .sum();

        double totalRevenue = calculateTotalRevenue(payments);
        return totalNights > 0 ? totalRevenue / totalNights : 0.0;
    }

    private double calculateRevPAR(List<Booking> bookings, List<Payment> payments, 
                                 LocalDate startDate, LocalDate endDate) {
        long totalRooms = roomRepository.count();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalRoomNights = totalRooms * daysBetween;
        
        double totalRevenue = calculateTotalRevenue(payments);
        return totalRoomNights > 0 ? totalRevenue / totalRoomNights : 0.0;
    }

    private double calculateOccupancyRate(List<Booking> bookings, LocalDate startDate, LocalDate endDate) {
        long totalRooms = roomRepository.count();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalRoomNights = totalRooms * daysBetween;

        long occupiedNights = bookings.stream()
            .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
            .mapToLong(Booking::getNumberOfNights)
            .sum();

        return totalRoomNights > 0 ? (double) occupiedNights / totalRoomNights * 100 : 0.0;
    }

    private Map<String, Double> calculateRevenueByRoomType(List<Booking> bookings, List<Payment> payments) {
        Map<String, Double> revenueByType = new HashMap<>();
        
        Map<Long, Double> paymentMap = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
            .collect(Collectors.toMap(
                Payment::getId,
                Payment::getAmount,
                (existing, replacement) -> existing
            ));

        // Group bookings by room type and sum revenue
        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.CANCELLED) continue;
            
            // Get room type (simplified - in real system, would join with room data)
            String roomType = getRoomTypeFromBooking(booking);
            
            // Get payment amount for this booking
            double paymentAmount = paymentMap.getOrDefault(booking.getId(), booking.getTotalPrice());
            
            revenueByType.merge(roomType, paymentAmount, Double::sum);
        }

        return revenueByType;
    }

    private Map<LocalDate, Double> calculateDailyRevenue(List<Payment> payments) {
        return payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
            .collect(Collectors.groupingBy(
                p -> p.getPaymentDate().toLocalDate(),
                Collectors.summingDouble(Payment::getAmount)
            ));
    }

    private Map<String, Double> calculateMonthlyRevenue(List<Payment> payments) {
        return payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
            .collect(Collectors.groupingBy(
                p -> p.getPaymentDate().getMonth().toString() + " " + p.getPaymentDate().getYear(),
                Collectors.summingDouble(Payment::getAmount)
            ));
    }

    private List<RoomPerformance> getTopPerformingRooms(List<Booking> bookings, List<Payment> payments) {
        Map<Integer, Double> roomRevenue = new HashMap<>();
        Map<Integer, Integer> roomBookings = new HashMap<>();

        Map<Long, Double> paymentMap = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
            .collect(Collectors.toMap(
                Payment::getId,
                Payment::getAmount,
                (existing, replacement) -> existing
            ));

        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.CANCELLED) continue;
            
            int roomNumber = booking.getRoomNumber();
            double revenue = paymentMap.getOrDefault(booking.getId(), booking.getTotalPrice());
            
            roomRevenue.merge(roomNumber, revenue, Double::sum);
            roomBookings.merge(roomNumber, 1, Integer::sum);
        }

        return roomRevenue.entrySet().stream()
            .map(entry -> new RoomPerformance(
                entry.getKey(),
                entry.getValue(),
                roomBookings.get(entry.getKey()),
                entry.getValue() / roomBookings.get(entry.getKey())
            ))
            .sorted((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()))
            .limit(10)
            .collect(Collectors.toList());
    }

    private double calculateAverageBookingValue(List<Payment> payments) {
        List<Double> amounts = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
            .map(Payment::getAmount)
            .collect(Collectors.toList());

        return amounts.isEmpty() ? 0.0 : amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double calculateCancellationRate(List<Booking> bookings) {
        long totalBookings = bookings.size();
        long cancelledBookings = bookings.stream()
            .filter(b -> b.getBookingStatus() == BookingStatus.CANCELLED)
            .count();

        return totalBookings > 0 ? (double) cancelledBookings / totalBookings * 100 : 0.0;
    }

    private double calculateNoShowRate(List<Booking> bookings) {
        long totalBookings = bookings.size();
        long noShowBookings = bookings.stream()
            .filter(b -> b.getBookingStatus() == BookingStatus.NO_SHOW)
            .count();

        return totalBookings > 0 ? (double) noShowBookings / totalBookings * 100 : 0.0;
    }

    public ForecastReport generateForecast(LocalDate startDate, int days) {
        LocalDate endDate = startDate.plusDays(days - 1);
        LocalDate historicalEnd = startDate.minusDays(1);
        LocalDate historicalStart = historicalEnd.minusDays(days - 1);

        // Get historical data
        RevenueReport historicalReport = generateRevenueReport(historicalStart, historicalEnd);
        
        // Simple forecast based on historical trends
        double historicalDailyAvg = historicalReport.getTotalRevenue() / days;
        double forecastedRevenue = historicalDailyAvg * days * 1.05; // 5% growth assumption

        return new ForecastReport(
            startDate,
            endDate,
            forecastedRevenue,
            historicalReport.getOccupancyRate() + 2.0, // Slight improvement
            historicalReport.getAverageDailyRate() * 1.02, // 2% rate increase
            calculateSeasonalAdjustment(startDate)
        );
    }

    private double calculateSeasonalAdjustment(LocalDate date) {
        // Simplified seasonal adjustment based on month
        int month = date.getMonthValue();
        switch (month) {
            case 12: case 1: case 2: return 0.9;  // Winter
            case 3: case 4: case 5: return 1.1;   // Spring
            case 6: case 7: case 8: return 1.3;   // Summer
            case 9: case 10: case 11: return 1.0; // Fall
            default: return 1.0;
        }
    }

    private String getRoomTypeFromBooking(Booking booking) {
        // Simplified room type extraction based on room number
        int roomNumber = booking.getRoomNumber();
        if (roomNumber % 3 == 1) return "Single";
        else if (roomNumber % 3 == 2) return "Double";
        else return "Suite";
    }

    public static class RoomPerformance {
        private final int roomNumber;
        private final double totalRevenue;
        private final int totalBookings;
        private final double averageBookingValue;

        public RoomPerformance(int roomNumber, double totalRevenue, int totalBookings, double averageBookingValue) {
            this.roomNumber = roomNumber;
            this.totalRevenue = totalRevenue;
            this.totalBookings = totalBookings;
            this.averageBookingValue = averageBookingValue;
        }

        public int getRoomNumber() { return roomNumber; }
        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalBookings() { return totalBookings; }
        public double getAverageBookingValue() { return averageBookingValue; }

        @Override
        public String toString() {
            return String.format("Room %d: $%.2f revenue, %d bookings, $%.2f avg", 
                               roomNumber, totalRevenue, totalBookings, averageBookingValue);
        }
    }
}