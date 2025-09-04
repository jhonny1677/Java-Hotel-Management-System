package persistence;

import model.Hotel;
//import model.Room;

//import org.json.JSONObject;
//import model.Room;
import org.junit.jupiter.api.Test;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;

//import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderGeneralHotel() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralHotel.json");
        try {
            Hotel loadedHotel = reader.read();

            assertEquals("Test Hotel", loadedHotel.getName());
            assertEquals("Test City", loadedHotel.getLocation());

            assertEquals(100, loadedHotel.getAllRooms().size());

            // assertNull(loadedHotel.getRoom(101).isAvailable());

            assertEquals(300.0, loadedHotel.getRoom(3).getPrice(), 0.01);

            assertFalse(loadedHotel.getRoom(2).getAmenities().contains("WiFi"));

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderEmptyHotel() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyHotel.json");
        try {
            Hotel loadedHotel = reader.read();

            assertEquals("Test Hotel", loadedHotel.getName());
            assertEquals("Test City", loadedHotel.getLocation());

            assertEquals(100, loadedHotel.getAllRooms().size());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

   

}
