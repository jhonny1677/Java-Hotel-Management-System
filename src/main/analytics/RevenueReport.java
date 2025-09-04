package analytics;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class RevenueReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double totalRevenue;
    private final int totalBookings;
    private final double averageDailyRate; // ADR
    private final double revenuePerAvailableRoom; // RevPAR
    private final double occupancyRate;
    private final Map<String, Double> revenueByRoomType;
    private final Map<LocalDate, Double> dailyRevenue;
    private final Map<String, Double> monthlyRevenue;
    private final List<RevenueAnalytics.RoomPerformance> topPerformingRooms;
    private final double averageBookingValue;
    private final double cancellationRate;
    private final double noShowRate;

    private RevenueReport(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.totalRevenue = builder.totalRevenue;
        this.totalBookings = builder.totalBookings;
        this.averageDailyRate = builder.averageDailyRate;
        this.revenuePerAvailableRoom = builder.revenuePerAvailableRoom;
        this.occupancyRate = builder.occupancyRate;
        this.revenueByRoomType = new HashMap<>(builder.revenueByRoomType);
        this.dailyRevenue = new HashMap<>(builder.dailyRevenue);
        this.monthlyRevenue = new HashMap<>(builder.monthlyRevenue);
        this.topPerformingRooms = new ArrayList<>(builder.topPerformingRooms);
        this.averageBookingValue = builder.averageBookingValue;
        this.cancellationRate = builder.cancellationRate;
        this.noShowRate = builder.noShowRate;
    }

    // Getters
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalBookings() { return totalBookings; }
    public double getAverageDailyRate() { return averageDailyRate; }
    public double getRevenuePerAvailableRoom() { return revenuePerAvailableRoom; }
    public double getOccupancyRate() { return occupancyRate; }
    public Map<String, Double> getRevenueByRoomType() { return new HashMap<>(revenueByRoomType); }
    public Map<LocalDate, Double> getDailyRevenue() { return new HashMap<>(dailyRevenue); }
    public Map<String, Double> getMonthlyRevenue() { return new HashMap<>(monthlyRevenue); }
    public List<RevenueAnalytics.RoomPerformance> getTopPerformingRooms() { return new ArrayList<>(topPerformingRooms); }
    public double getAverageBookingValue() { return averageBookingValue; }
    public double getCancellationRate() { return cancellationRate; }
    public double getNoShowRate() { return noShowRate; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REVENUE REPORT ===\n");
        sb.append(String.format("Period: %s to %s\n", startDate, endDate));
        sb.append(String.format("Total Revenue: $%.2f\n", totalRevenue));
        sb.append(String.format("Total Bookings: %d\n", totalBookings));
        sb.append(String.format("Average Daily Rate (ADR): $%.2f\n", averageDailyRate));
        sb.append(String.format("Revenue per Available Room (RevPAR): $%.2f\n", revenuePerAvailableRoom));
        sb.append(String.format("Occupancy Rate: %.1f%%\n", occupancyRate));
        sb.append(String.format("Average Booking Value: $%.2f\n", averageBookingValue));
        sb.append(String.format("Cancellation Rate: %.1f%%\n", cancellationRate));
        sb.append(String.format("No-Show Rate: %.1f%%\n", noShowRate));
        
        sb.append("\n=== REVENUE BY ROOM TYPE ===\n");
        revenueByRoomType.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(entry -> sb.append(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue())));
        
        sb.append("\n=== TOP PERFORMING ROOMS ===\n");
        topPerformingRooms.stream()
            .limit(5)
            .forEach(room -> sb.append(room.toString()).append("\n"));
        
        return sb.toString();
    }

    public static class Builder {
        private LocalDate startDate;
        private LocalDate endDate;
        private double totalRevenue;
        private int totalBookings;
        private double averageDailyRate;
        private double revenuePerAvailableRoom;
        private double occupancyRate;
        private Map<String, Double> revenueByRoomType = new HashMap<>();
        private Map<LocalDate, Double> dailyRevenue = new HashMap<>();
        private Map<String, Double> monthlyRevenue = new HashMap<>();
        private List<RevenueAnalytics.RoomPerformance> topPerformingRooms = new ArrayList<>();
        private double averageBookingValue;
        private double cancellationRate;
        private double noShowRate;

        public Builder period(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }

        public Builder totalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public Builder totalBookings(int totalBookings) {
            this.totalBookings = totalBookings;
            return this;
        }

        public Builder averageDailyRate(double averageDailyRate) {
            this.averageDailyRate = averageDailyRate;
            return this;
        }

        public Builder revenuePerAvailableRoom(double revenuePerAvailableRoom) {
            this.revenuePerAvailableRoom = revenuePerAvailableRoom;
            return this;
        }

        public Builder occupancyRate(double occupancyRate) {
            this.occupancyRate = occupancyRate;
            return this;
        }

        public Builder revenueByRoomType(Map<String, Double> revenueByRoomType) {
            this.revenueByRoomType = new HashMap<>(revenueByRoomType);
            return this;
        }

        public Builder dailyRevenue(Map<LocalDate, Double> dailyRevenue) {
            this.dailyRevenue = new HashMap<>(dailyRevenue);
            return this;
        }

        public Builder monthlyRevenue(Map<String, Double> monthlyRevenue) {
            this.monthlyRevenue = new HashMap<>(monthlyRevenue);
            return this;
        }

        public Builder topPerformingRooms(List<RevenueAnalytics.RoomPerformance> topPerformingRooms) {
            this.topPerformingRooms = new ArrayList<>(topPerformingRooms);
            return this;
        }

        public Builder averageBookingValue(double averageBookingValue) {
            this.averageBookingValue = averageBookingValue;
            return this;
        }

        public Builder cancellationRate(double cancellationRate) {
            this.cancellationRate = cancellationRate;
            return this;
        }

        public Builder noShowRate(double noShowRate) {
            this.noShowRate = noShowRate;
            return this;
        }

        public RevenueReport build() {
            return new RevenueReport(this);
        }
    }
}