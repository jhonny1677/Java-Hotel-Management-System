package testing;

import config.ApplicationContext;
import model.*;
import auth.Role;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class SimpleTestRunner {
    private final ApplicationContext context;
    private final Random random = new Random();

    public SimpleTestRunner() {
        this.context = new ApplicationContext();
    }

    public void runBasicTests() {
        System.out.println("Running Basic System Tests...");
        System.out.println("===============================");
        
        try {
            testAuthentication();
            testRoomOperations();
            testBookingOperations();
            testAnalytics();
            
            System.out.println("\nAll basic tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.shutdown();
        }
    }

    private void testAuthentication() {
        System.out.println("\nTesting Authentication...");
        
        var authResult = context.getAuthenticationService().authenticate("admin@hotel.com", "password123");
        if (authResult.isSuccessful()) {
            System.out.println("✓ Authentication successful");
        } else {
            System.out.println("✗ Authentication failed");
        }
    }

    private void testRoomOperations() {
        System.out.println("\nTesting Room Operations...");
        
        List<Room> rooms = context.getRoomRepository().findAll();
        System.out.printf("✓ Found %d rooms in repository\n", rooms.size());
        
        List<Room> availableRooms = context.getRoomRepository().findAvailableRooms();
        System.out.printf("✓ Found %d available rooms\n", availableRooms.size());
    }

    private void testBookingOperations() {
        System.out.println("\nTesting Booking Operations...");
        
        List<User> users = context.getUserRepository().findAll();
        if (!users.isEmpty()) {
            User testUser = users.get(0);
            
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = checkIn.plusDays(2);
            
            try {
                var result = context.getConcurrentBookingService().createBooking(
                    testUser.getId(), 1, checkIn, checkOut);
                
                if (result.isSuccessful()) {
                    System.out.println("✓ Booking creation successful");
                } else {
                    System.out.println("✗ Booking failed: " + result.getMessage());
                }
            } catch (Exception e) {
                System.out.println("✗ Booking test failed: " + e.getMessage());
            }
        }
    }

    private void testAnalytics() {
        System.out.println("\nTesting Analytics...");
        
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            
            var report = context.getRevenueAnalytics().generateRevenueReport(startDate, endDate);
            System.out.printf("✓ Revenue report generated: $%.2f total revenue\n", report.getTotalRevenue());
            
        } catch (Exception e) {
            System.out.println("✗ Analytics test failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SimpleTestRunner runner = new SimpleTestRunner();
        runner.runBasicTests();
    }
}