package persistence;

import model.Hotel;
//import model.Room;

//import model.Room;
import org.junit.jupiter.api.Test;
import java.io.IOException;
//import java.util.List;

//import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest extends JsonTest {
    private Hotel hotel;

    @SuppressWarnings("methodlength")
    @Test
    void testWriterGeneralHotel() {
        try {
            hotel = new Hotel("Hilton Hotel", "New York");

            hotel.bookRoom(1);
            hotel.getRoom(3).setPrice(350.0);
            hotel.getRoom(2).addAmenity("WiFi");

            JsonWriter writer = new JsonWriter("./data/testResetHotel.json");
            writer.open();
            writer.write(hotel);
            writer.close();

            JsonReader reader = new JsonReader("./data/testResetHotel.json");
            Hotel loadedHotel = reader.read();

            assertEquals("Hilton Hotel", loadedHotel.getName());
            assertEquals("New York", loadedHotel.getLocation());
            assertEquals(100, loadedHotel.getAllRooms().size());
            assertFalse(loadedHotel.getRoom(1).isAvailable());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @SuppressWarnings("methodlength")
    @Test
    void testAllRoomFeatures() {
        try {
            hotel = new Hotel("Hilton Hotel", "New York");

            hotel.bookRoom(1);
            assertFalse(hotel.getRoom(1).isAvailable());

            hotel.cancelReservation(1);
            assertTrue(hotel.getRoom(1).isAvailable());

            hotel.getRoom(3).setPrice(350.0);
            assertEquals(350.0, hotel.getRoom(3).getPrice(), 0.01);

            hotel.getRoom(2).addAmenity("WiFi");
            hotel.getRoom(2).addAmenity("TV");
            assertEquals(2, hotel.getRoom(2).getAmenities().size());

            hotel.getRoom(2).removeAmenity("TV");
            assertEquals(1, hotel.getRoom(2).getAmenities().size());
            hotel.getRoom(2).removeAmenity("WiFi");
            assertEquals(0, hotel.getRoom(2).getAmenities().size());

            JsonWriter writer = new JsonWriter("./data/testResetHotel.json");
            writer.open();
            writer.write(hotel);
            writer.close();

            JsonReader reader = new JsonReader("./data/testResetHotel.json");
            Hotel loadedHotel = reader.read();

            assertTrue(loadedHotel.getRoom(1).isAvailable());

            assertEquals(0, loadedHotel.getRoom(2).getAmenities().size());
            assertFalse(loadedHotel.getRoom(2).getAmenities().contains("WiFi"));

        } catch (IOException e) {
            fail("Exception should not have been thrown.");
        }
    }

    @Test
    void testWriterWithoutOpening() {
        JsonWriter writer = new JsonWriter("./data/testHotel.json");

        try {
            writer.write(new Hotel("Test Hotel", "Test City")); // Calling write() without open()
            fail("Expected an IllegalStateException but none was thrown");
        } catch (IllegalStateException e) {
            assertEquals("Writer not opened! Call open() first.", e.getMessage());
            System.out.println(" Handled null writer case correctly.");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testWriterCloseAfterWriting() {
        JsonWriter jsonWriter = new JsonWriter("./data/testHotel.json");
        try {
            jsonWriter.open(); // Open the writer
            jsonWriter.write(new Hotel("Test Hotel", "Test City"));
            jsonWriter.close(); // close the writer

            System.out.println(" Writer closed successfully after writing.");
        } catch (Exception e) {
            fail("Unexpected exception during close: " + e.getMessage());
        }
    }

    @Test
    void testWriterCloseWithoutOpening() {
        JsonWriter jsonWriter = new JsonWriter("./data/testHotel.json");
        try {
            jsonWriter.close(); // Close without opening
            System.out.println("Closing without opening did not cause an error.");
        } catch (Exception e) {
            fail(" Closing without opening should not throw an error, but got: " + e.getMessage());
        }
    }

}
