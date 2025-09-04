package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class HotelLoggerEventTest {

    private Hotel logger;

    @BeforeEach
    public void setup() {
        logger = new Hotel("Hilton Hotel", "New York");
        EventLog.getInstance().clear();
    }

    @Test
    public void testLogSaveEvent() {
        logger.logSaveEvent();
        EventLog eventLog = EventLog.getInstance();

        assertFalse(eventLog.equals(new Event("Hotel data saved to file.")));
    }

    @Test
    public void testLogLoadEvent() {
        logger.logLoadEvent();
        EventLog eventLog = EventLog.getInstance();

        assertFalse(eventLog.equals(new Event("Hotel data loaded from file.")));
    }

    @Test
    public void testPrintEventLog() {

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        logger.logSaveEvent();
        logger.logLoadEvent();
        logger.printEventLog();

        String output = outContent.toString().trim();
        assertTrue(output.contains("Hotel data saved to file."));
        assertTrue(output.contains("Hotel data loaded from file."));

        System.setOut(System.out);
    }

    @Test
    public void testPrintLog() {

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        logger.logSaveEvent();
        logger.logLoadEvent();
        logger.printLog();

        String output = outContent.toString().trim();
        assertTrue(output.contains("==== Event Log ===="));
        assertTrue(output.contains("Hotel data saved to file."));
        assertTrue(output.contains("Hotel data loaded from file."));

        System.setOut(System.out);
    }
}
