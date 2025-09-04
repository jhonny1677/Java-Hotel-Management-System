package pricing;

import model.Room;
import repository.BookingRepository;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class DynamicPricingEngine {
    private final BookingRepository bookingRepository;
    private final List<PricingStrategy> strategies;
    private final Map<String, Double> strategyWeights;

    public DynamicPricingEngine(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        this.strategies = new ArrayList<>();
        this.strategyWeights = new HashMap<>();
        
        initializeStrategies();
    }

    private void initializeStrategies() {
        // Demand-based pricing
        addStrategy(new DemandBasedPricingStrategy(bookingRepository), 0.4);
        
        // Seasonal pricing
        addStrategy(new SeasonalPricingStrategy(), 0.3);
        
        // Day of week pricing
        addStrategy(new DayOfWeekPricingStrategy(), 0.2);
        
        // Length of stay pricing
        addStrategy(new LengthOfStayPricingStrategy(), 0.1);
    }

    public void addStrategy(PricingStrategy strategy, double weight) {
        strategies.add(strategy);
        strategyWeights.put(strategy.getStrategyName(), weight);
    }

    public double calculateDynamicPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        Map<String, Object> context = buildContext(room, checkIn, checkOut);
        double basePrice = room.getPrice();
        double totalMultiplier = 0.0;
        double totalWeight = 0.0;

        for (PricingStrategy strategy : strategies) {
            double strategyPrice = strategy.calculatePrice(room, checkIn, checkOut, context);
            double multiplier = strategyPrice / basePrice;
            double weight = strategyWeights.get(strategy.getStrategyName());
            
            totalMultiplier += multiplier * weight;
            totalWeight += weight;
        }

        // Calculate weighted average multiplier
        double finalMultiplier = totalWeight > 0 ? totalMultiplier / totalWeight : 1.0;
        
        // Apply bounds to prevent extreme pricing
        finalMultiplier = Math.max(0.5, Math.min(3.0, finalMultiplier));
        
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        return basePrice * nights * finalMultiplier;
    }

    public PricingBreakdown calculatePriceBreakdown(Room room, LocalDate checkIn, LocalDate checkOut) {
        Map<String, Object> context = buildContext(room, checkIn, checkOut);
        double basePrice = room.getPrice();
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        
        Map<String, Double> strategyPrices = new HashMap<>();
        Map<String, Double> strategyMultipliers = new HashMap<>();
        
        for (PricingStrategy strategy : strategies) {
            double strategyPrice = strategy.calculatePrice(room, checkIn, checkOut, context);
            double multiplier = strategyPrice / basePrice;
            
            strategyPrices.put(strategy.getStrategyName(), strategyPrice);
            strategyMultipliers.put(strategy.getStrategyName(), multiplier);
        }
        
        double finalPrice = calculateDynamicPrice(room, checkIn, checkOut);
        
        return new PricingBreakdown(basePrice, nights, strategyPrices, strategyMultipliers, 
                                  strategyWeights, finalPrice);
    }

    private Map<String, Object> buildContext(Room room, LocalDate checkIn, LocalDate checkOut) {
        Map<String, Object> context = new HashMap<>();
        
        // Occupancy data
        int totalRooms = 100; // This should come from hotel configuration
        long occupiedRooms = bookingRepository.findByDateRange(checkIn, checkOut).size();
        double occupancyRate = (double) occupiedRooms / totalRooms;
        
        context.put("occupancyRate", occupancyRate);
        context.put("totalRooms", totalRooms);
        context.put("occupiedRooms", occupiedRooms);
        context.put("roomType", room.getRoomType());
        context.put("daysUntilCheckIn", checkIn.toEpochDay() - LocalDate.now().toEpochDay());
        
        return context;
    }

    public static class PricingBreakdown {
        private final double basePrice;
        private final long nights;
        private final Map<String, Double> strategyPrices;
        private final Map<String, Double> strategyMultipliers;
        private final Map<String, Double> strategyWeights;
        private final double finalPrice;

        public PricingBreakdown(double basePrice, long nights, Map<String, Double> strategyPrices,
                              Map<String, Double> strategyMultipliers, Map<String, Double> strategyWeights,
                              double finalPrice) {
            this.basePrice = basePrice;
            this.nights = nights;
            this.strategyPrices = new HashMap<>(strategyPrices);
            this.strategyMultipliers = new HashMap<>(strategyMultipliers);
            this.strategyWeights = new HashMap<>(strategyWeights);
            this.finalPrice = finalPrice;
        }

        public double getBasePrice() { return basePrice; }
        public long getNights() { return nights; }
        public Map<String, Double> getStrategyPrices() { return strategyPrices; }
        public Map<String, Double> getStrategyMultipliers() { return strategyMultipliers; }
        public Map<String, Double> getStrategyWeights() { return strategyWeights; }
        public double getFinalPrice() { return finalPrice; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Base Price: $%.2f per night\n", basePrice));
            sb.append(String.format("Number of Nights: %d\n", nights));
            sb.append(String.format("Base Total: $%.2f\n", basePrice * nights));
            sb.append("\nPricing Adjustments:\n");
            
            for (String strategy : strategyMultipliers.keySet()) {
                double multiplier = strategyMultipliers.get(strategy);
                double weight = strategyWeights.get(strategy);
                sb.append(String.format("- %s: %.1f%% (weight: %.1f%%)\n", 
                    strategy, (multiplier - 1) * 100, weight * 100));
            }
            
            sb.append(String.format("\nFinal Price: $%.2f", finalPrice));
            return sb.toString();
        }
    }
}