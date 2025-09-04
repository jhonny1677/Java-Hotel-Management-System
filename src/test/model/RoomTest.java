package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class RoomTest {

    private Room room;
    private static final double TOLERANCE = 0.01;

    @BeforeEach
    void setUp() {

        room = new Room(101, "Single", 100.0);
    }

    @Test
    void testRoomInitialization() {

        assertEquals(101, room.getRoomNumber());
        assertEquals("Single", room.getRoomType());
        assertEquals(100.0, room.getPrice());
        assertTrue(room.isAvailable());
        assertEquals(0, room.getAmenities().size());

    }

    @Test
    void testSetAvailability() {

        room.setAvailable(false);
        assertFalse(room.isAvailable());

        room.setAvailable(true);
        assertTrue(room.isAvailable());
    }

    @Test
    void testAddingAmenities() {

        room.addAmenity("WiFi");
        room.addAmenity("TV");

        List<String> amenities = room.getAmenities();
        assertEquals(2, amenities.size());
        assertTrue(amenities.contains("WiFi"));
        assertTrue(amenities.contains("TV"));

    }

    @Test
    void testAddingDuplicateAmenity() {
        room.addAmenity("WiFi");
        room.addAmenity("WiFi"); // Adding duplicate

        assertEquals(1, room.getAmenities().size());
    }

    @Test
    void testRemovingAmenities() {

        room.addAmenity("WiFi");
        room.addAmenity("TV");

        room.removeAmenity("WiFi");

        assertEquals(1, room.getAmenities().size());
        assertFalse(room.getAmenities().contains("WiFi"));
    }

    @Test
    void testRemovingNonExistingAmenity() {

        room.addAmenity("WiFi");
        room.removeAmenity("TV"); // Trying to remove an amenity that does not exist

        assertEquals(1, room.getAmenities().size());
    }

    @Test
    void testSetPrice() {

        room.setPrice(150.0);
        assertEquals(150.0, room.getPrice(), TOLERANCE);

    }

    @Test
    void testCalculateTotalPrice() {

        assertEquals(300.0, room.calculateTotalPrice(3), TOLERANCE);
        assertEquals(100.0, room.calculateTotalPrice(1), TOLERANCE);

    }

    @Test
    void testCalculateDiscountedPrice() {

        assertEquals(900.0, room.calculateDiscountedPrice(10), TOLERANCE);
        assertEquals(500.0, room.calculateDiscountedPrice(5), TOLERANCE);
        assertEquals(4500.0, room.calculateDiscountedPrice(50), TOLERANCE);

    }

}
