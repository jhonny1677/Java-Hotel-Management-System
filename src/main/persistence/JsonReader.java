package persistence;

import model.Hotel;
import model.Room;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Represents a reader that reads a hotel from JSON data stored in a file.
 * This class enables loading previously saved hotel data, restoring the
 * hotel state, including room availability and amenities. /*
 */

public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from the given source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads hotel from file and returns it; throws IOException if file
    // cannot be read
    public Hotel read() throws IOException {
        String jsonData = new String(Files.readAllBytes(Paths.get(source)));
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseHotel(jsonObject);
    }

    // EFFECTS: parses hotel from JSON object and returns it
    private Hotel parseHotel(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String location = jsonObject.getString("location");
        Hotel hotel = new Hotel(name, location);
        hotel.getAllRooms().clear();
        addRooms(hotel, jsonObject.getJSONArray("rooms"));
        return hotel;
    }

    // MODIFIES: hotel
    // EFFECTS: parses rooms from JSON array and adds them to hotel
    private void addRooms(Hotel hotel, JSONArray roomsArray) {
        for (int i = 0; i < roomsArray.length(); i++) {
            JSONObject roomJson = roomsArray.getJSONObject(i);
            Room room = parseRoom(roomJson);
            hotel.addRoom(room.getRoomType(), room.getPrice());
            hotel.getRoom(room.getRoomNumber()).setAvailable(room.isAvailable());
        }
    }

    // EFFECTS: parses a room from JSON object and returns it
    private Room parseRoom(JSONObject jsonObject) {
        int roomNumber = jsonObject.getInt("roomNumber");
        String roomType = jsonObject.getString("roomType");
        double price = jsonObject.getDouble("price");
        boolean isAvailable = jsonObject.getBoolean("isAvailable");

        Room room = new Room(roomNumber, roomType, price);
        room.setAvailable(isAvailable);

        JSONArray amenitiesJson = jsonObject.getJSONArray("amenities");
        for (int i = 0; i < amenitiesJson.length(); i++) {
            room.addAmenity(amenitiesJson.getString(i));
        }

        return room;
    }
}
