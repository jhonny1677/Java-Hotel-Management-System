package pricing;

import model.Room;
import repository.BookingRepository;
import java.time.LocalDate;
import java.util.Map;

public class DemandBasedPricingStrategy implements PricingStrategy {
    private final BookingRepository bookingRepository;

    public DemandBasedPricingStrategy(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut, Map<String, Object> context) {
        double basePrice = room.getPrice();
        double occupancyRate = (Double) context.getOrDefault("occupancyRate", 0.0);
        long daysUntilCheckIn = (Long) context.getOrDefault("daysUntilCheckIn", 30L);

        // High occupancy increases price
        double occupancyMultiplier = calculateOccupancyMultiplier(occupancyRate);
        
        // Booking closer to check-in date increases price (dynamic pricing)
        double urgencyMultiplier = calculateUrgencyMultiplier(daysUntilCheckIn);
        
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        return basePrice * nights * occupancyMultiplier * urgencyMultiplier;
    }

    private double calculateOccupancyMultiplier(double occupancyRate) {
        if (occupancyRate >= 0.9) {
            return 1.5; // 50% increase for very high occupancy
        } else if (occupancyRate >= 0.8) {
            return 1.3; // 30% increase for high occupancy
        } else if (occupancyRate >= 0.6) {
            return 1.1; // 10% increase for moderate occupancy
        } else if (occupancyRate >= 0.4) {
            return 1.0; // No change for normal occupancy
        } else if (occupancyRate >= 0.2) {
            return 0.9; // 10% decrease for low occupancy
        } else {
            return 0.8; // 20% decrease for very low occupancy
        }
    }

    private double calculateUrgencyMultiplier(long daysUntilCheckIn) {
        if (daysUntilCheckIn <= 0) {
            return 1.4; // 40% increase for same-day booking
        } else if (daysUntilCheckIn <= 1) {
            return 1.3; // 30% increase for next-day booking
        } else if (daysUntilCheckIn <= 3) {
            return 1.2; // 20% increase for within 3 days
        } else if (daysUntilCheckIn <= 7) {
            return 1.1; // 10% increase for within a week
        } else if (daysUntilCheckIn <= 30) {
            return 1.0; // No change for normal advance booking
        } else {
            return 0.95; // 5% decrease for very early booking
        }
    }

    @Override
    public String getStrategyName() {
        return "Demand-Based Pricing";
    }

    @Override
    public String getDescription() {
        return "Adjusts prices based on current hotel occupancy and booking urgency";
    }
}