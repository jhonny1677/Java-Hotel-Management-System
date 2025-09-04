package persistence;

import model.Hotel;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Represents a writer that writes the JSON representation of a hotel to a file.
 * This class allows saving the current hotel state (rooms, availability,
 * amenities, etc.)
 * to a JSON file, enabling persistence between application runs.
 */

public class JsonWriter {
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws IOException if file cannot be opened
    public void open() throws IOException {
        writer = new PrintWriter(new FileWriter(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of hotel to file
    public void write(Hotel hotel) {
        if (writer == null) {
            throw new IllegalStateException("Writer not opened! Call open() first.");
        }
        JSONObject json = hotel.toJson();
        saveToFile(json.toString(4));
    }

    // MODIFIES: this
    // EFFECTS: closes the writer
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
