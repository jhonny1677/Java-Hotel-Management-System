package pricing;

import model.Room;
import java.time.LocalDate;
import java.util.Map;

public interface PricingStrategy {
    double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut, Map<String, Object> context);
    String getStrategyName();
    String getDescription();
}