package analytics;

import repository.BookingRepository;
import repository.PaymentRepository;
import repository.RoomRepository;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class AnalyticsDashboard {
    private final RevenueAnalytics revenueAnalytics;

    public AnalyticsDashboard(BookingRepository bookingRepository,
                            PaymentRepository paymentRepository,
                            RoomRepository roomRepository) {
        this.revenueAnalytics = new RevenueAnalytics(bookingRepository, paymentRepository, roomRepository);
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate yearStart = today.withDayOfYear(1);
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        
        // Current month performance
        RevenueReport monthlyReport = revenueAnalytics.generateRevenueReport(monthStart, today);
        dashboard.put("monthlyReport", monthlyReport);
        
        // Current year performance
        RevenueReport yearlyReport = revenueAnalytics.generateRevenueReport(yearStart, today);
        dashboard.put("yearlyReport", yearlyReport);
        
        // Current week performance
        RevenueReport weeklyReport = revenueAnalytics.generateRevenueReport(weekStart, today);
        dashboard.put("weeklyReport", weeklyReport);
        
        // Yesterday vs Today comparison
        RevenueReport yesterdayReport = revenueAnalytics.generateRevenueReport(today.minusDays(1), today.minusDays(1));
        RevenueReport todayReport = revenueAnalytics.generateRevenueReport(today, today);
        dashboard.put("dayComparison", createComparison(yesterdayReport, todayReport));
        
        // 30-day forecast
        ForecastReport forecast = revenueAnalytics.generateForecast(today.plusDays(1), 30);
        dashboard.put("forecast", forecast);
        
        // Key metrics summary
        Map<String, Double> keyMetrics = new HashMap<>();
        keyMetrics.put("monthlyRevenue", monthlyReport.getTotalRevenue());
        keyMetrics.put("monthlyOccupancy", monthlyReport.getOccupancyRate());
        keyMetrics.put("monthlyADR", monthlyReport.getAverageDailyRate());
        keyMetrics.put("monthlyRevPAR", monthlyReport.getRevenuePerAvailableRoom());
        keyMetrics.put("cancellationRate", monthlyReport.getCancellationRate());
        dashboard.put("keyMetrics", keyMetrics);
        
        return dashboard;
    }

    public String generateExecutiveSummary() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate lastMonthStart = monthStart.minusMonths(1);
        LocalDate lastMonthEnd = monthStart.minusDays(1);
        
        RevenueReport currentMonth = revenueAnalytics.generateRevenueReport(monthStart, today);
        RevenueReport lastMonth = revenueAnalytics.generateRevenueReport(lastMonthStart, lastMonthEnd);
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== EXECUTIVE SUMMARY ===\n\n");
        
        double revenueGrowth = ((currentMonth.getTotalRevenue() - lastMonth.getTotalRevenue()) / lastMonth.getTotalRevenue()) * 100;
        double occupancyChange = currentMonth.getOccupancyRate() - lastMonth.getOccupancyRate();
        double adrChange = currentMonth.getAverageDailyRate() - lastMonth.getAverageDailyRate();
        
        summary.append(String.format("Revenue Growth: %.1f%% (%s)\n", 
            Math.abs(revenueGrowth), revenueGrowth >= 0 ? "↑" : "↓"));
        summary.append(String.format("Current Month Revenue: $%.2f\n", currentMonth.getTotalRevenue()));
        summary.append(String.format("Occupancy Rate: %.1f%% (%+.1f%% from last month)\n", 
            currentMonth.getOccupancyRate(), occupancyChange));
        summary.append(String.format("Average Daily Rate: $%.2f (%+$.2f from last month)\n", 
            currentMonth.getAverageDailyRate(), adrChange));
        summary.append(String.format("RevPAR: $%.2f\n", currentMonth.getRevenuePerAvailableRoom()));
        
        summary.append("\n=== KEY INSIGHTS ===\n");
        if (revenueGrowth > 10) {
            summary.append("• Strong revenue growth indicates excellent performance\n");
        } else if (revenueGrowth < -5) {
            summary.append("• Revenue decline requires attention - consider pricing strategy review\n");
        } else {
            summary.append("• Revenue performance is stable\n");
        }
        
        if (currentMonth.getOccupancyRate() > 85) {
            summary.append("• High occupancy rate - consider dynamic pricing to maximize revenue\n");
        } else if (currentMonth.getOccupancyRate() < 60) {
            summary.append("• Low occupancy rate - consider marketing promotions or rate adjustments\n");
        }
        
        if (currentMonth.getCancellationRate() > 15) {
            summary.append("• High cancellation rate - review booking policies and guest communication\n");
        }
        
        return summary.toString();
    }

    private Map<String, Object> createComparison(RevenueReport yesterday, RevenueReport today) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("revenueChange", today.getTotalRevenue() - yesterday.getTotalRevenue());
        comparison.put("bookingsChange", today.getTotalBookings() - yesterday.getTotalBookings());
        comparison.put("occupancyChange", today.getOccupancyRate() - yesterday.getOccupancyRate());
        return comparison;
    }
}