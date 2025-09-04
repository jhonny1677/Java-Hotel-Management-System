package testing;

import config.ApplicationContext;
import model.*;
import repository.RoomRepository;
import java.util.List;

public class DebugTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== DEBUG: Room Repository Test ===");
        
        ApplicationContext context = new ApplicationContext();
        RoomRepository roomRepo = context.getRoomRepository();
        
        // Check initial count
        System.out.println("Initial room count: " + roomRepo.count());
        
        // Try to create a single room manually
        System.out.println("\nCreating test room manually...");
        model.Room testRoom = new model.Room(999, "Test", 99.99);
        testRoom.addAmenity("WiFi");
        
        Room savedRoom = roomRepo.save(testRoom);
        System.out.println("Saved room: " + savedRoom);
        
        // Check count again
        System.out.println("Room count after manual creation: " + roomRepo.count());
        
        // Try to find it
        var foundRoom = roomRepo.findByRoomNumber(999);
        System.out.println("Found test room: " + foundRoom.isPresent());
        
        // List all rooms
        List<Room> allRooms = roomRepo.findAll();
        System.out.println("Total rooms in repository: " + allRooms.size());
        
        for (int i = 0; i < Math.min(5, allRooms.size()); i++) {
            Room room = allRooms.get(i);
            if (room instanceof model.Room) {
                model.Room r = (model.Room) room;
                System.out.println("Room " + (i+1) + ": #" + r.getRoomNumber() + " - " + r.getRoomType());
            }
        }
        
        context.shutdown();
    }
}