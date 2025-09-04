package pricing;

import model.Room;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.HashMap;

public class DayOfWeekPricingStrategy implements PricingStrategy {
    private final Map<DayOfWeek, Double> dayMultipliers;

    public DayOfWeekPricingStrategy() {
        this.dayMultipliers = new HashMap<>();
        initializeDayRates();
    }

    private void initializeDayRates() {
        // Weekend rates are higher
        dayMultipliers.put(DayOfWeek.MONDAY, 0.9);
        dayMultipliers.put(DayOfWeek.TUESDAY, 0.85);
        dayMultipliers.put(DayOfWeek.WEDNESDAY, 0.85);
        dayMultipliers.put(DayOfWeek.THURSDAY, 0.9);
        dayMultipliers.put(DayOfWeek.FRIDAY, 1.1);
        dayMultipliers.put(DayOfWeek.SATURDAY, 1.3);
        dayMultipliers.put(DayOfWeek.SUNDAY, 1.2);
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut, Map<String, Object> context) {
        double basePrice = room.getPrice();
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        
        // Calculate average day-of-week multiplier for the stay period
        double totalMultiplier = 0.0;
        LocalDate currentDate = checkIn;
        long totalDays = 0;
        
        while (currentDate.isBefore(checkOut)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            double multiplier = dayMultipliers.getOrDefault(dayOfWeek, 1.0);
            
            totalMultiplier += multiplier;
            totalDays++;
            currentDate = currentDate.plusDays(1);
        }
        
        double averageMultiplier = totalDays > 0 ? totalMultiplier / totalDays : 1.0;
        return basePrice * nights * averageMultiplier;
    }

    @Override
    public String getStrategyName() {
        return "Day-of-Week Pricing";
    }

    @Override
    public String getDescription() {
        return "Adjusts prices based on day of the week, with higher rates for weekends";
    }
}