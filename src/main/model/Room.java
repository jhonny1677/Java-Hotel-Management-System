package model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

/**
 * Constructs a room with the specified room number, type, and price.
 * By default, the room is available and has no amenities.
 * roomNumber --- The unique room number.
 * roomType --- The type of the room (Single, Double, Suite).
 * price --- The nightly price of the room.
 */

public class Room implements Writable {

    private int roomNumber;
    private String roomType;
    private boolean isAvailable;
    private double price;
    private List<String> amenities; // List of amenities (e.g., WiFi, TV)
    private int maxOccupancy;
    private int size; // in square feet
    private String description;

    // EFFECTS: Initializes a new room with default availability set to true and an
    // empty list of amenities.
    public Room(int roomNumber, String roomType, double price) {

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = Math.max(price, 0);
        this.isAvailable = true;
        this.amenities = new ArrayList<>();

    }

    public int getRoomNumber() {

        return roomNumber;
    }

    public String getRoomType() {

        return roomType;
    }

    public boolean isAvailable() {

        return isAvailable;
    }

    public double getPrice() {

        return price;
    }

    public List<String> getAmenities() {

        return amenities;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // REQUIRES: room must exist
    // MODIFIES: this
    // EFFECTS: changes the rooms availibility status to available.

    public void setAvailable(boolean available) {

        this.isAvailable = available;
    }

    // REQUIRES: price >=0
    // MODIFIES: this
    // EFFECTS: sets the price of the room to given price

    public void setPrice(double price) {

        this.price = price;
    }

    // MODIFIES: this
    // EFFECTS: adds an amenity to the room if not already present and records the
    // log

    public void addAmenity(String amenity) {

        if (!amenities.contains(amenity)) {

            amenities.add(amenity);
            EventLog.getInstance().logEvent(new Event("Amenity '" + amenity + "' added to Room " + roomNumber));
        }

    }

    // MODIFIES: this
    // EFFECTS: removes an amenity from the room and records the log

    public void removeAmenity(String amenity) {

        amenities.remove(amenity);
        EventLog.getInstance().logEvent(new Event("Amenity '" + amenity + "' removed from Room " + roomNumber));

    }

    // REQUIRES: nights > 0
    // EFFECTS: calculates the total cost based on the room price and number of
    // nights

    public double calculateTotalPrice(int nights) {

        return price * Math.max(nights, 1);
    }

    // REQUIRES: nights > 0
    // EFFECTS: Returns the total cost with a 10% discount if nights > 5

    public double calculateDiscountedPrice(int nights) {

        double totalPrice = calculateTotalPrice(nights);
        if (nights > 5) {
            totalPrice *= 0.9; // 10% discount
        }
        return totalPrice;
    }

    // EFFECTS: Converts this Room object into a JSON representation
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("roomNumber", roomNumber);
        json.put("roomType", roomType);
        json.put("isAvailable", isAvailable);
        json.put("price", price);
        JSONArray amenitiesJson = new JSONArray();
        for (String amenity : amenities) {
            amenitiesJson.put(amenity);
        }
        json.put("amenities", amenitiesJson);
        json.put("maxOccupancy", maxOccupancy);
        json.put("size", size);
        json.put("description", description);
        return json;

    }

}
