package model;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Event class.
 */
public class EventTest {

    private Event event;
    private String description;

    @BeforeEach
    void setUp() {
        description = "Room 101 booked.";
        event = new Event(description);
    }

    @Test
    void testGetDate() {
        Date dateLogged = event.getDate();

        // Check if dateLogged is not null and is of type Date
        assertNotNull(dateLogged);
        assertTrue(dateLogged instanceof Date);

        // Check if dateLogged is reasonably close to the current date
        long currentTime = new Date().getTime();
        long eventTime = dateLogged.getTime();
        long timeDifference = Math.abs(currentTime - eventTime);
        
        assertTrue(timeDifference < 1000); // 1 second difference is acceptable
    }

    @Test
    void testGetDescription() {
        assertEquals(description, event.getDescription());
    }

    @Test
    void testToString() {
        String eventString = event.toString();
        
        // Check if the output contains the date and description
        assertTrue(eventString.contains(description));
        assertTrue(eventString.contains(event.getDate().toString()));
    }

   
}
