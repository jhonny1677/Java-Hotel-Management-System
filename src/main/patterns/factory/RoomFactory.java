package patterns.factory;

import model.Room;
import java.util.Arrays;
import java.util.List;

public class RoomFactory {
    
    public static Room createRoom(int roomNumber, RoomType roomType) {
        Room room = new Room(roomNumber, roomType.getTypeName(), roomType.getBasePrice());
        
        // Configure room with type-specific amenities
        List<String> amenities = roomType.getDefaultAmenities();
        for (String amenity : amenities) {
            room.addAmenity(amenity);
        }
        
        room.setMaxOccupancy(roomType.getMaxOccupancy());
        room.setSize(roomType.getSize());
        
        return room;
    }

    public static Room createCustomRoom(int roomNumber, String typeName, double price, 
                                      List<String> amenities, int maxOccupancy, int size) {
        Room room = new Room(roomNumber, typeName, price);
        
        for (String amenity : amenities) {
            room.addAmenity(amenity);
        }
        
        room.setMaxOccupancy(maxOccupancy);
        room.setSize(size);
        
        return room;
    }

    public enum RoomType {
        SINGLE("Single", 100.0, 2, 250, 
               Arrays.asList("WiFi", "TV", "Air Conditioning")),
        
        DOUBLE("Double", 150.0, 4, 350, 
               Arrays.asList("WiFi", "TV", "Air Conditioning", "Mini Fridge")),
        
        SUITE("Suite", 300.0, 6, 600, 
               Arrays.asList("WiFi", "TV", "Air Conditioning", "Mini Fridge", 
                           "Balcony", "Room Service", "Jacuzzi")),
        
        DELUXE("Deluxe", 450.0, 8, 800, 
               Arrays.asList("WiFi", "TV", "Air Conditioning", "Mini Fridge", 
                           "Balcony", "Room Service", "Jacuzzi", "Ocean View", "Butler Service")),
        
        PRESIDENTIAL("Presidential", 1000.0, 10, 1200, 
                    Arrays.asList("WiFi", "TV", "Air Conditioning", "Mini Fridge", 
                                "Balcony", "Room Service", "Jacuzzi", "Ocean View", 
                                "Butler Service", "Private Dining", "Helipad Access"));

        private final String typeName;
        private final double basePrice;
        private final int maxOccupancy;
        private final int size; // in square feet
        private final List<String> defaultAmenities;

        RoomType(String typeName, double basePrice, int maxOccupancy, int size, List<String> defaultAmenities) {
            this.typeName = typeName;
            this.basePrice = basePrice;
            this.maxOccupancy = maxOccupancy;
            this.size = size;
            this.defaultAmenities = defaultAmenities;
        }

        public String getTypeName() { return typeName; }
        public double getBasePrice() { return basePrice; }
        public int getMaxOccupancy() { return maxOccupancy; }
        public int getSize() { return size; }
        public List<String> getDefaultAmenities() { return defaultAmenities; }
    }
}