package persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.*;
//import model.Hotel;
//import model.Room;

public class JsonTest {
    private static final String TEST_FILE = "./data/testReaderEmptyHotel.json";
    private static final String BACKUP_FILE = "./data/originalHotelData.json";

    @BeforeEach
    void resetJsonFile() {
        try {
            copyFile(BACKUP_FILE, TEST_FILE);
        } catch (IOException e) {
            fail("Failed to reset JSON file");
        }
    }

    @AfterEach
    void restoreJsonFile() {
        try {
            copyFile(BACKUP_FILE, TEST_FILE);
        } catch (IOException e) {
            fail("Failed to restore original JSON file");
        }
    }

    private void copyFile(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        File destFile = new File(destination);
        try (InputStream is = new FileInputStream(sourceFile);
                OutputStream os = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

}
