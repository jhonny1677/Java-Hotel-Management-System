package model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

/**
 * Represents a hotel with a name and loation that contains multiple rooms of
 * different types.
 * The hotel allows booking, canceling reservations, and managing room
 * availability.
 * It provides functionality to retrieve available rooms, rooms by type, adding
 * amenities, removing amenities, calculating cost of stay, applying discounts,
 * adding and deleting rooms.
 */

public class Hotel implements Writable {

    private String name;
    private String location;
    private List<Room> rooms;

    // EFFECTS: constructs hotel with name, location and initializes 100 rooms in
    // the hotel
    public Hotel(String name, String location) {

        this.name = name;
        this.location = location;
        this.rooms = new ArrayList<>();
        initializeRooms(100);

    }

    // REQUIRES: number of rooms > 0
    // EFFECTS: Fills the hotel with different types of rooms
    private void initializeRooms(int numberOfRooms) {

        for (int i = 1; i <= numberOfRooms; i++) {
            String roomType;
            double price;

            // Assign types based on position
            if (i % 3 == 1) {
                roomType = "Single";
                price = 100.0;
            } else if (i % 3 == 2) {
                roomType = "Double";
                price = 150.0;
            } else {
                roomType = "Suite";
                price = 300.0;
            }

            rooms.add(new Room(i, roomType, price));
        }

    }

    // REQUIRES: Room must exist and be available
    // MODIFIES: this
    // EFFECTS: Books the room and makes the room no longer available and records
    // the log
    public boolean bookRoom(int roomNumber) {

        Room room = getRoom(roomNumber);
        if (room != null && room.isAvailable()) {

            room.setAvailable(false);
            EventLog.getInstance().logEvent(new Event("Room " + roomNumber + " booked successfully."));
            return true;
        }

        return false;
    }

    // REQUIRES: Room must exist and be booked
    // MODIFIES: this
    // EFFECTS: Cancels the reservation for the room and records the log

    public boolean cancelReservation(int roomNumber) {

        Room room = getRoom(roomNumber);
        if (room != null && !room.isAvailable()) {

            room.setAvailable(true);
            EventLog.getInstance().logEvent(new Event("Reservation for Room " + roomNumber + " canceled."));
            return true;

        }

        return false;
    }

    // REQUIRES: Hotel must exist and rooms must be initialized
    // EFFECTS: Finds the avilable room with the given type

    public Room findAvailableRoom(String roomType) {
        for (Room room : rooms) {
            if (room.isAvailable() && room.getRoomType().equalsIgnoreCase(roomType)) {
                return room; // Return the first available room found
            }
        }
        return null; // Return null if no available room is found
    }
    // REQUIRES: Hotel must exist and rooms must be initialized
    // EFFECTS: Prints the details of rooms matching the specified type

    public List<Room> getRoomsByType(String roomType) {

        List<Room> matchingRooms = new ArrayList<>();

        for (Room room : rooms) {

            if (room.getRoomType().equalsIgnoreCase(roomType)) {
                matchingRooms.add(room);
            }
        }
        return matchingRooms;
    }

    // REQUIRES: Hotel must exist and rooms must be initialized
    // EFFECTS: Computes and returns the number of available rooms

    public int countAvailableRooms() {

        int count = 0;
        for (Room room : rooms) {

            if (room.isAvailable()) {

                count++;

            }
        }

        return count;

    }

    public String getName() {

        return name;
    }

    public String getLocation() {

        return location;
    }

    public List<Room> getAllRooms() {
        return rooms;
    }

    // REQUIRES: room should exist
    // EFFECTS: returns whether the room with given room number exist or not
    public Room getRoom(int roomNumber) {

        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;

    }

    // REQUIRES: Hotel must exist and rooms must be initialized
    // EFFECTS: Returns a list of all available rooms in the hotel
    public List<Room> findAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {

            if (room.isAvailable()) {

                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    // REQUIRES: Hotel must exist and rooms must be initialized
    // EFFECTS: Returns a list of all available rooms of a given type
    public List<Room> findAvailableRoomsByType(String roomType) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable() && room.getRoomType().equalsIgnoreCase(roomType)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    // REQUIRES: room with a particular type and price
    // MODIFIES: this
    // EFFECTS: add a new room to list of rooms and records the log
    public Room addRoom(String roomType, double price) {
        int newRoomNumber = rooms.size() + 1;
        Room newRoom = new Room(newRoomNumber, roomType, price);
        rooms.add(newRoom);
        EventLog.getInstance().logEvent(new Event("Room " + newRoom.getRoomNumber() + " added to Hotel."));
        return newRoom;
    }

    // REQUIRES: room with a particular type and price
    // MODIFIES: this
    // EFFECTS: removes a new room to list of rooms and records the log
    public boolean removeRoom(int roomNumber) {
        Room roomToRemove = getRoom(roomNumber);

        if (roomToRemove != null) {
            rooms.remove(roomToRemove);
            EventLog.getInstance().logEvent(new Event("Room " + roomNumber + " removed from Hotel."));
            return true;
        } else {
            return false;
        }
    }

    // EFFECTS: Converts this Hotel object into a JSON representation
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("location", location);
        JSONArray roomsJson = new JSONArray();
        for (Room room : rooms) {
            roomsJson.put(room.toJson());
        }
        json.put("rooms", roomsJson);
        return json;
    }

    // EFFECTS: Logs an event when hotel data is saved
    public void logSaveEvent() {
        EventLog.getInstance().logEvent(new Event("Hotel data saved to file."));
    }

    // EFFECTS: Logs an event when hotel data is loaded
    public void logLoadEvent() {
        EventLog.getInstance().logEvent(new Event("Hotel data loaded from file."));
    }

    // EFFECTS: Prints all the events in the EventLog to the console.
    public void printEventLog() {
        EventLog eventLog = EventLog.getInstance();
        for (Event event : eventLog) {
            System.out.println(event.toString());
        }
    }

    // EFFECTS: Prints all events from the EventLog to the console
    public void printLog() {
        System.out.println("\n==== Event Log ====");
        for (Event event : EventLog.getInstance()) {
            System.out.println(event.toString());
        }
    }

}
