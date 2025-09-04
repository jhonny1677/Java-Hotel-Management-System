package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HotelTest {

    private Hotel hotel;

    @BeforeEach
    void setUp() {

        hotel = new Hotel("Hilton Hotel", "New York");
        EventLog.getInstance().clear();

    }

    @Test
    void testHotelConstructor() {

        assertEquals("Hilton Hotel", hotel.getName());
        assertEquals("New York", hotel.getLocation());

        assertEquals(100, hotel.countAvailableRooms());

        // Verify the first few rooms to check correct initialization
        Room firstRoom = hotel.findAvailableRoom("Single");
        assertNotNull(firstRoom);
        assertEquals(1, firstRoom.getRoomNumber());
        assertEquals("Single", firstRoom.getRoomType());
        assertEquals(100.0, firstRoom.getPrice());

        Room secondRoom = hotel.findAvailableRoom("Double");
        assertNotNull(secondRoom);
        assertEquals(2, secondRoom.getRoomNumber());
        assertEquals("Double", secondRoom.getRoomType());
        assertEquals(150.0, secondRoom.getPrice());

        Room thirdRoom = hotel.findAvailableRoom("Suite");
        assertNotNull(thirdRoom);
        assertEquals(3, thirdRoom.getRoomNumber());
        assertEquals("Suite", thirdRoom.getRoomType());
        assertEquals(300.0, thirdRoom.getPrice());

        hotel.bookRoom(1);
        Room fourthRoom = hotel.findAvailableRoom("Single");
        assertNotNull(fourthRoom);
        assertEquals(4, fourthRoom.getRoomNumber());
    }

    @Test
    void testHotelHasExactly100Rooms() {

        assertEquals(100, hotel.countAvailableRooms());

    }

    @Test
    void testBookingAlreadyBookedRoom() {

        hotel.bookRoom(10);
        assertFalse(hotel.bookRoom(10));
    }

    @Test
    void testCancelingUnbookedRoom() {

        assertFalse(hotel.cancelReservation(20));

    }

    @Test
    void testBookingInvalidRoomNumber() {

        assertFalse(hotel.bookRoom(200));
        assertFalse(hotel.bookRoom(0));
    }

    @Test
    void testFindingAvailableRoomAfterMultipleBookings() {

        hotel.bookRoom(5);
        hotel.bookRoom(10);
        hotel.bookRoom(15);

        assertEquals(97, hotel.countAvailableRooms());

        Room foundRoom = hotel.findAvailableRoom("Single");
        assertNotNull(foundRoom);
        assertTrue(foundRoom.isAvailable());
    }

    @Test
    void testDisplayAvailableRoomsAfterBooking() {

        hotel.bookRoom(3);
        hotel.bookRoom(8);

       // int availableBeforeCancel = hotel.countAvailableRooms();
       // hotel.cancelReservation(3);
      //  int availableAfterCancel = hotel.countAvailableRooms();

        // assertTrue(availableAfterCancel > availableBeforeCancel);
    }

    @Test
    void testDisplayAllRooms() {

        List<Room> allRooms = hotel.getAllRooms();

        assertEquals(100, allRooms.size());

        // Check first room
        Room firstRoom = allRooms.get(0);
        assertEquals(1, firstRoom.getRoomNumber());
        assertNotNull(firstRoom.getRoomType());

        // Check last room
        Room lastRoom = allRooms.get(99);
        assertEquals(100, lastRoom.getRoomNumber());
    }

    @Test
    void testGetRoom() {

        Room room1 = hotel.getRoom(1);
        assertNotNull(room1);
        assertEquals(1, room1.getRoomNumber());

        Room room50 = hotel.getRoom(50);
        assertNotNull(room50);
        assertEquals(50, room50.getRoomNumber());

        Room nonExistentRoom = hotel.getRoom(200);
        assertNull(nonExistentRoom);
    }

    @Test
    void testFindAvailableRoomsByType() {

        Room singleRoom = hotel.findAvailableRoom("Single");
        assertNotNull(singleRoom);
        assertEquals("Single", singleRoom.getRoomType());

        Room doubleRoom = hotel.findAvailableRoom("Double");
        assertNotNull(doubleRoom);
        assertEquals("Double", doubleRoom.getRoomType());

        Room suiteRoom = hotel.findAvailableRoom("Suite");
        assertNotNull(suiteRoom);
        assertEquals("Suite", suiteRoom.getRoomType());
    }

    @Test
    void testGetRoomsByType() {

        List<Room> singleRooms = hotel.getRoomsByType("Single");
        assertEquals(34, singleRooms.size());

        List<Room> doubleRooms = hotel.getRoomsByType("Double");
        assertEquals(33, doubleRooms.size());

        List<Room> suiteRooms = hotel.getRoomsByType("Suite");
        assertEquals(33, suiteRooms.size());

        for (Room room : singleRooms) {
            assertEquals("Single", room.getRoomType());
        }

        for (Room room : doubleRooms) {
            assertEquals("Double", room.getRoomType());
        }

        for (Room room : suiteRooms) {
            assertEquals("Suite", room.getRoomType());
        }

    }

    @Test
    void testFindAvailableRoomWhenNoneAvailableByType() {

        for (Room room : hotel.getRoomsByType("Single")) {
            hotel.bookRoom(room.getRoomNumber());
        }

        Room singleRoom = hotel.findAvailableRoom("Single");

        assertNull(singleRoom);
    }

    @Test
    void testCancelReservationForUnbookedRoom() {

        Room room = hotel.getRoom(5);
        assertNotNull(room);
        assertTrue(room.isAvailable());
        boolean result = hotel.cancelReservation(5);

        assertFalse(result);
        assertTrue(room.isAvailable());
    }

    @Test
    void testCancelReservationForNonExistentRoom() {

        boolean result = hotel.cancelReservation(200);
        assertFalse(result);
    }

    @Test
    void testFindAvailableRooms() {
        // Initially, all rooms should be available
        List<Room> availableRooms = hotel.findAvailableRooms();
        assertEquals(100, availableRooms.size(), "Initially, all 100 rooms should be available.");

        // Check that all returned rooms are actually available
        for (Room room : availableRooms) {
            assertTrue(room.isAvailable(), "Every room in the available list should be available.");
        }

        // Book some rooms
        hotel.bookRoom(1);
        hotel.bookRoom(10);
        hotel.bookRoom(20);

        availableRooms = hotel.findAvailableRooms();
        assertEquals(97, availableRooms.size());

        for (Room room : availableRooms) {
            assertTrue(room.isAvailable());
        }
    }

    @Test
    void testFindAvailableRoomsByTypeAdvance() {

        List<Room> availableSingleRooms = hotel.findAvailableRoomsByType("Single");
        assertFalse(availableSingleRooms.isEmpty());

        for (Room room : availableSingleRooms) {
            assertEquals("Single", room.getRoomType());
            assertTrue(room.isAvailable());
        }

        // Book some Single rooms
        hotel.bookRoom(1);
        hotel.bookRoom(4);

        availableSingleRooms = hotel.findAvailableRoomsByType("Single");
        assertEquals(32, availableSingleRooms.size());

        for (Room room : availableSingleRooms) {
            assertEquals("Single", room.getRoomType());
            assertTrue(room.isAvailable());
        }

        for (Room room : availableSingleRooms) {
            hotel.bookRoom(room.getRoomNumber());
        }

        availableSingleRooms = hotel.findAvailableRoomsByType("Single");
        assertTrue(availableSingleRooms.isEmpty());
    }

    @Test
    void testAddRoom() {

        int initialRoomCount = hotel.getAllRooms().size();

        Room newRoom = hotel.addRoom("Suite", 350.0);

        assertNotNull(newRoom);
        assertEquals(initialRoomCount + 1, hotel.getAllRooms().size());
        assertEquals("Suite", newRoom.getRoomType());
        assertEquals(350.0, newRoom.getPrice());
        assertTrue(newRoom.isAvailable());
    }

    @Test
    void testRemoveRoom() {

        Room newRoom = hotel.addRoom("Double", 200.0);
        int roomNumber = newRoom.getRoomNumber();

        assertNotNull(hotel.getRoom(roomNumber));

        boolean removed = hotel.removeRoom(roomNumber);
        assertTrue(removed);
        assertNull(hotel.getRoom(roomNumber));
    }

    @Test
    void testRemoveNonExistentRoom() {
        boolean removed = hotel.removeRoom(999);
        assertFalse(removed);
    }

    @Test
    void testLogRoomBooking() {
        hotel.bookRoom(1);
        List<Event> events = new ArrayList<>();

        for (Event event : EventLog.getInstance()) {
            events.add(event);
        }

        assertEquals(2, events.size());
        assertEquals("Room 1 booked successfully.", events.get(1).getDescription());
    }

    @Test
    void testLogRoomCancellation() {
        hotel.bookRoom(1);
        hotel.cancelReservation(1);

        List<Event> events = new ArrayList<>();

        for (Event event : EventLog.getInstance()) {
            events.add(event);
        }

        assertEquals(3, events.size());
        assertEquals("Room 1 booked successfully.", events.get(1).getDescription());
        assertEquals("Reservation for Room 1 canceled.", events.get(2).getDescription());
    }

    @Test
    void testLogRoomAddition() {
        hotel.addRoom("Double", 150.0);

        List<Event> events = new ArrayList<>();

        for (Event event : EventLog.getInstance()) {
            events.add(event);
        }

        assertEquals(2, events.size());
        assertEquals("Room 101 added to Hotel.",events.get(1).getDescription());
    }

    @Test
    void testLogRoomRemoval() {
        hotel.addRoom("Double", 150.0);
        hotel.removeRoom(101);

        List<Event> events = new ArrayList<>();

        for (Event event : EventLog.getInstance()) {
            events.add(event);
        }

        assertEquals(3, events.size());
        assertEquals("Room 101 removed from Hotel.",events.get(2).getDescription());
    }

    @Test
    void testLogAmenityAdditionAndRemoval() {
        Room room = new Room(101, "Single", 100.0); 
        room.addAmenity("WiFi");
        room.removeAmenity("WiFi");

        List<Event> events = new ArrayList<>();

        for (Event event : EventLog.getInstance()) {
            events.add(event);
        }

        assertEquals(3, events.size());
        assertEquals("Amenity 'WiFi' added to Room 101", events.get(1).getDescription());
        assertEquals("Amenity 'WiFi' removed from Room 101", events.get(2).getDescription());
    }

    @Test
    void testEventLogClear() {
        EventLog.getInstance().clear();

        List<Event> events = new ArrayList<>();

        for (Event event : EventLog.getInstance()) {
            events.add(event);
        }

        assertEquals(1, events.size());
        assertEquals("Event log cleared.", events.get(0).getDescription());
    }

}
