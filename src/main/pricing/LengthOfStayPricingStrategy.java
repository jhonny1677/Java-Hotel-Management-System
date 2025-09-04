package pricing;

import model.Room;
import java.time.LocalDate;
import java.util.Map;

public class LengthOfStayPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut, Map<String, Object> context) {
        double basePrice = room.getPrice();
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        
        double lengthOfStayMultiplier = calculateLengthOfStayMultiplier(nights);
        return basePrice * nights * lengthOfStayMultiplier;
    }

    private double calculateLengthOfStayMultiplier(long nights) {
        if (nights >= 30) {
            return 0.7; // 30% discount for monthly stays
        } else if (nights >= 14) {
            return 0.8; // 20% discount for bi-weekly stays
        } else if (nights >= 7) {
            return 0.9; // 10% discount for weekly stays
        } else if (nights >= 3) {
            return 0.95; // 5% discount for extended weekend stays
        } else if (nights == 1) {
            return 1.1; // 10% premium for single night stays (more operational overhead)
        } else {
            return 1.0; // Normal rate for 2-night stays
        }
    }

    @Override
    public String getStrategyName() {
        return "Length-of-Stay Pricing";
    }

    @Override
    public String getDescription() {
        return "Provides discounts for longer stays and premiums for very short stays";
    }
}