package pricing;

import model.Room;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.HashMap;

public class SeasonalPricingStrategy implements PricingStrategy {
    private final Map<Month, Double> seasonalMultipliers;

    public SeasonalPricingStrategy() {
        this.seasonalMultipliers = new HashMap<>();
        initializeSeasonalRates();
    }

    private void initializeSeasonalRates() {
        // Winter months - moderate pricing
        seasonalMultipliers.put(Month.JANUARY, 0.9);
        seasonalMultipliers.put(Month.FEBRUARY, 0.9);
        seasonalMultipliers.put(Month.MARCH, 1.0);
        
        // Spring months - increasing demand
        seasonalMultipliers.put(Month.APRIL, 1.1);
        seasonalMultipliers.put(Month.MAY, 1.2);
        seasonalMultipliers.put(Month.JUNE, 1.3);
        
        // Summer months - peak season
        seasonalMultipliers.put(Month.JULY, 1.4);
        seasonalMultipliers.put(Month.AUGUST, 1.4);
        seasonalMultipliers.put(Month.SEPTEMBER, 1.2);
        
        // Fall months - moderate pricing
        seasonalMultipliers.put(Month.OCTOBER, 1.1);
        seasonalMultipliers.put(Month.NOVEMBER, 0.95);
        seasonalMultipliers.put(Month.DECEMBER, 1.3); // Holiday season
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut, Map<String, Object> context) {
        double basePrice = room.getPrice();
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        
        // Calculate average seasonal multiplier for the stay period
        double totalMultiplier = 0.0;
        LocalDate currentDate = checkIn;
        long totalDays = 0;
        
        while (currentDate.isBefore(checkOut)) {
            Month month = currentDate.getMonth();
            double multiplier = seasonalMultipliers.getOrDefault(month, 1.0);
            
            // Special events and holidays
            multiplier *= getSpecialEventMultiplier(currentDate);
            
            totalMultiplier += multiplier;
            totalDays++;
            currentDate = currentDate.plusDays(1);
        }
        
        double averageMultiplier = totalDays > 0 ? totalMultiplier / totalDays : 1.0;
        return basePrice * nights * averageMultiplier;
    }

    private double getSpecialEventMultiplier(LocalDate date) {
        // New Year's period
        if ((date.getMonth() == Month.DECEMBER && date.getDayOfMonth() >= 25) ||
            (date.getMonth() == Month.JANUARY && date.getDayOfMonth() <= 2)) {
            return 1.2;
        }
        
        // Valentine's Day
        if (date.getMonth() == Month.FEBRUARY && date.getDayOfMonth() == 14) {
            return 1.15;
        }
        
        // Independence Day (US)
        if (date.getMonth() == Month.JULY && date.getDayOfMonth() == 4) {
            return 1.1;
        }
        
        // Halloween
        if (date.getMonth() == Month.OCTOBER && date.getDayOfMonth() == 31) {
            return 1.05;
        }
        
        // Thanksgiving week (approximate)
        if (date.getMonth() == Month.NOVEMBER && date.getDayOfMonth() >= 22 && date.getDayOfMonth() <= 28) {
            return 1.15;
        }
        
        return 1.0; // No special event multiplier
    }

    @Override
    public String getStrategyName() {
        return "Seasonal Pricing";
    }

    @Override
    public String getDescription() {
        return "Adjusts prices based on seasonal demand patterns and special events";
    }
}