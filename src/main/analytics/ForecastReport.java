package analytics;

import java.time.LocalDate;

public class ForecastReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double forecastedRevenue;
    private final double forecastedOccupancy;
    private final double forecastedADR;
    private final double seasonalAdjustment;

    public ForecastReport(LocalDate startDate, LocalDate endDate, double forecastedRevenue, 
                         double forecastedOccupancy, double forecastedADR, double seasonalAdjustment) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.forecastedRevenue = forecastedRevenue;
        this.forecastedOccupancy = forecastedOccupancy;
        this.forecastedADR = forecastedADR;
        this.seasonalAdjustment = seasonalAdjustment;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getForecastedRevenue() { return forecastedRevenue; }
    public double getForecastedOccupancy() { return forecastedOccupancy; }
    public double getForecastedADR() { return forecastedADR; }
    public double getSeasonalAdjustment() { return seasonalAdjustment; }

    @Override
    public String toString() {
        return String.format(
            "=== REVENUE FORECAST ===\n" +
            "Period: %s to %s\n" +
            "Forecasted Revenue: $%.2f\n" +
            "Forecasted Occupancy: %.1f%%\n" +
            "Forecasted ADR: $%.2f\n" +
            "Seasonal Adjustment: %.1f%%\n",
            startDate, endDate, forecastedRevenue, forecastedOccupancy, 
            forecastedADR, (seasonalAdjustment - 1) * 100
        );
    }
}