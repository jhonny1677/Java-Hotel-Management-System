package ui;

import model.Event;
import model.EventLog;
import model.Hotel;
import model.Room;
import persistence.JsonReader;
import persistence.JsonWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/*
 * Class: Main (Hotel Management System)
 * --------------------------------------
 * This class provides a console-based User Interface (UI) for the Hotel Management System.
 * It allows users to interact with hotel data, book rooms, cancel reservations,
 * add/remove amenities, and calculate costs. The system also supports saving and 
 * loading data from a JSON file.
 *
 * Features:
 * - View all rooms and available rooms
 * - Book and cancel room reservations
 * - Add and remove room amenities
 * - Calculate total and discounted stay prices
 * - Save and load hotel data using JSON persistence
 *
 * JSON Persistence:
 * - Saves hotel and room data to a JSON file
 * - Loads existing hotel data from a JSON file
 *
 
 */

public class Main {

    private static final String JSON_FILE = "./data/originalHotelData.json";
    private static JsonWriter jsonWriter;
    private static JsonReader jsonReader;
    private Hotel hotel;

    public static void main(String[] args) throws Exception {
        EventLog.getInstance().clear();
        System.out.println("Welcome to my project!!!");

        Scanner scanner = new Scanner(System.in);
        jsonWriter = new JsonWriter(JSON_FILE);
        jsonReader = new JsonReader(JSON_FILE);

        Hotel hotel = loadHotelData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getUserChoice(scanner);
            running = handleUserChoice(choice, hotel, scanner);
        }

        System.out.println("Thank you for using the Hotel Management System. Goodbye!!!");
        scanner.close();
    }

    private static Hotel loadHotelData() {
        try {
            Hotel hotel = jsonReader.read();
            System.out.println("✅ Hotel data loaded successfully.");
            return hotel;
        } catch (IOException e) {
            System.out.println(" No previous data found. Starting with a new hotel.");
            return new Hotel("Hilton Hotel", "New York");
        }
    }

    private static void saveHotelData(Hotel hotel) {
        try {
            jsonWriter.open();
            jsonWriter.write(hotel);
            jsonWriter.close();
            System.out.println("✅ Hotel data saved successfully.");
        } catch (IOException e) {
            System.out.println(" Error in saving hotel data.");
        }
    }

    private static void printMenu() {
        System.out.println("\n=== HOTEL MANAGEMENT SYSTEM ===");
        System.out.println("1. View All Rooms");
        System.out.println("2. View Available Rooms");
        System.out.println("3. Book a Room");
        System.out.println("4. Cancel a Reservation");
        System.out.println("5. Find Available Rooms by Type");
        System.out.println("6. View Rooms by Type");
        System.out.println("7. Add an Amenity to a Room");
        System.out.println("8. Remove an Amenity from a Room");
        System.out.println("9. View Amenities of a Room");
        System.out.println("10. Calculate Total Price for a Stay");
        System.out.println("11. Calculate Discounted Price for a Stay");
        System.out.println("12. Add a Room");
        System.out.println("13. Remove a Room");
        System.out.println("14. Save Data");
        System.out.println("15. Load Data");
        System.out.println("16. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return -1;
        }
    }

    @SuppressWarnings("methodlength")
    private static boolean handleUserChoice(int choice, Hotel hotel, Scanner scanner) {
        switch (choice) {
            case 1:
                displayAllRooms(hotel);
                break;
            case 2:
                displayAvailableRooms(hotel);
                break;
            case 3:
                bookRoom(hotel, scanner);
                break;
            case 4:
                cancelReservation(hotel, scanner);
                break;
            case 5:
                findAvailableRoomByType(hotel, scanner);
                break;
            case 6:
                displayRoomsByType(hotel, scanner);
                break;
            case 7:
                addAmenityToRoom(hotel, scanner);
                break;
            case 8:
                removeAmenityFromRoom(hotel, scanner);
                break;
            case 9:
                viewRoomAmenities(hotel, scanner);
                break;
            case 10:
                calculateTotalPrice(hotel, scanner);
                break;
            case 11:
                calculateDiscountedPrice(hotel, scanner);
                break;
            case 12:
                addRoomToHotel(hotel, scanner);
                break;
            case 13:
                removeRoomFromHotel(hotel, scanner);
                break;
            case 14:
                saveHotelData(hotel);
                break;
            case 15:
                hotel = loadHotelData();
                break;
            case 16:
                hotel.printLog();
                return false; // Exit the application
            default:
                System.out.println("Invalid choice! Please select a valid option.");
        }
        return true;
    }

    private static void displayAllRooms(Hotel hotel) {
        System.out.println("\n=== All Hotel Rooms ===");
        for (Room room : hotel.getAllRooms()) {
            System.out.println(room.getRoomNumber() + " - Type: " + room.getRoomType() + " - Price: $" + room.getPrice()
                    + " - Available: " + room.isAvailable());
        }
    }

    private static void displayAvailableRooms(Hotel hotel) {
        System.out.println("\n=== Available Rooms ===");
        List<Room> availableRooms = hotel.findAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms at the moment.");
        } else {
            for (Room room : availableRooms) {
                System.out.println(
                        room.getRoomNumber() + " - Type: " + room.getRoomType() + " - Price: $" + room.getPrice());
            }
        }
    }

    private static void bookRoom(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number to book: ");
        int roomNumber = getUserChoice(scanner);
        if (hotel.bookRoom(roomNumber)) {
            System.out.println("Room " + roomNumber + " booked successfully!");
        } else {
            System.out.println("Room " + roomNumber + " is either unavailable or does not exist.");
        }
    }

    private static void cancelReservation(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number to cancel the reservation: ");
        int roomNumber = getUserChoice(scanner);
        if (hotel.cancelReservation(roomNumber)) {
            System.out.println("Reservation for Room " + roomNumber + " has been canceled.");
        } else {
            System.out.println("Cancellation failed! The room was either not booked or does not exist.");
        }
    }

    private static void findAvailableRoomByType(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room type (Single, Double, Suite): ");
        String roomType = scanner.nextLine();

        List<Room> availableRooms = hotel.findAvailableRoomsByType(roomType);

        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms of type " + roomType + ".");
        } else {
            System.out.println("\n=== Available " + roomType + " Rooms ===");
            for (Room room : availableRooms) {
                System.out.println("Room " + room.getRoomNumber() + " - Type: " + room.getRoomType() + " - Price: $"
                        + room.getPrice());
            }
        }
    }

    private static void displayRoomsByType(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room type (Single, Double, Suite): ");
        String roomType = scanner.nextLine();
        List<Room> roomsByType = hotel.getRoomsByType(roomType);
        if (roomsByType.isEmpty()) {
            System.out.println("No rooms of type " + roomType + " found.");
        } else {
            for (Room room : roomsByType) {
                System.out.println(
                        room.getRoomNumber() + " - Type: " + room.getRoomType() + " - Price: $" + room.getPrice());
            }
        }
    }

    private static void addAmenityToRoom(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number: ");
        int roomNumber = getUserChoice(scanner);
        Room room = hotel.getRoom(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }

        System.out.print("Enter the amenity to add: ");
        String amenity = scanner.nextLine();

        room.addAmenity(amenity);
        System.out.println("Amenity '" + amenity + "' added to Room " + roomNumber + ".");
    }

    private static void removeAmenityFromRoom(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number: ");
        int roomNumber = getUserChoice(scanner);
        Room room = hotel.getRoom(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }

        System.out.print("Enter the amenity to remove: ");
        String amenity = scanner.nextLine();

        if (room.getAmenities().contains(amenity)) {
            room.removeAmenity(amenity);
            System.out.println("Amenity '" + amenity + "' removed from Room " + roomNumber + ".");
        } else {
            System.out.println("Amenity '" + amenity + "' is not found in Room " + roomNumber + ".");
        }
    }

    private static void calculateTotalPrice(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number: ");
        int roomNumber = getUserChoice(scanner);
        Room room = hotel.getRoom(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }

        System.out.print("Enter the number of nights: ");
        int nights = getUserChoice(scanner);

        if (nights <= 0) {
            System.out.println("Number of nights must be greater than 0.");
            return;
        }

        double totalPrice = room.calculateTotalPrice(nights);
        System.out.println("Total price for " + nights + " nights in Room " + roomNumber + ": $" + totalPrice);
    }

    private static void calculateDiscountedPrice(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number: ");
        int roomNumber = getUserChoice(scanner);
        Room room = hotel.getRoom(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }

        System.out.print("Enter the number of nights: ");
        int nights = getUserChoice(scanner);

        if (nights <= 0) {
            System.out.println("Number of nights must be greater than 0.");
            return;
        }

        double discountedPrice = room.calculateDiscountedPrice(nights);
        System.out
                .println("Discounted price for " + nights + " nights in Room " + roomNumber + ": $" + discountedPrice);
    }

    private static void viewRoomAmenities(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number: ");
        int roomNumber = getUserChoice(scanner);
        Room room = hotel.getRoom(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }

        List<String> amenities = room.getAmenities();
        if (amenities.isEmpty()) {
            System.out.println("Room " + roomNumber + " has no added amenities.");
        } else {
            System.out.println("\n=== Amenities in Room " + roomNumber + " ===");
            for (String amenity : amenities) {
                System.out.println("- " + amenity);
            }
        }
    }

    private static void addRoomToHotel(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room type (Single, Double, Suite): ");
        String roomType = scanner.nextLine();

        System.out.print("Enter the price per night: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price! Please enter a valid number.");
            return;
        }

        Room newRoom = hotel.addRoom(roomType, price);
        System.out.println("New room added: Room " + newRoom.getRoomNumber() + " - Type: " + newRoom.getRoomType()
                + " - Price: $" + newRoom.getPrice());
    }

    private static void removeRoomFromHotel(Hotel hotel, Scanner scanner) {
        System.out.print("\nEnter the room number to remove: ");
        int roomNumber = getUserChoice(scanner);

        if (hotel.removeRoom(roomNumber)) {
            System.out.println("Room " + roomNumber + " has been removed.");
        } else {
            System.out.println("Could not remove Room " + roomNumber + ". It may not exist.");
        }
    }

    // // EFFECTS: Prints all events from the EventLog to the console
    // private static void printLog() {
    // System.out.println("\n=== Event Log ===");
    // for (Event event : EventLog.getInstance()) {
    // System.out.println(event.toString());
    // }
    // }

}

/*
 * =============================================================================
 * ===
 * Java Code Citations for Hotel Management System
 * =============================================================================
 * ===
 * This project follows Java best practices, object-oriented principles, and
 * industry-standard development patterns. The implementation references various
 * official Java documentation, CPSC 210 course materials, and community
 * tutorials.
 * 
 * -----------------------------------------------------------------------------
 * ---
 * CPSC 210 References (UBC)
 * -----------------------------------------------------------------------------
 * ---
 * - **CPSC 210: Teller App Example**
 * - **CPSC 210: Gym Kiosk Example**
 * -**CPSC 210: Abstraction Module Practice code and solutions**
 * --**CPSC 210: Basics Module Practice code and solutions**
 * -----------------------------------------------------------------------------
 * ---
 * Java Programming & OOP Principles
 * -----------------------------------------------------------------------------
 * ---
 * - **Object-Oriented Programming (OOP) in Java**
 * https://docs.oracle.com/javase/tutorial/java/concepts/
 * - **Encapsulation, Getters & Setters**
 * https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html
 * - **Inheritance and Polymorphism in Java**
 * https://www.geeksforgeeks.org/object-oriented-programming-oops-concept-in-
 * java/
 * - **Constructors and Parameterized Initialization**
 * https://docs.oracle.com/javase/tutorial/java/javaOO/constructors.html
 * - **Method Overriding in Java**
 * https://www.geeksforgeeks.org/method-overriding-in-java-with-examples/
 * - **Using Math.max() for Input Validation**
 * https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html
 * - **Using equalsIgnoreCase() for Case-Insensitive Comparisons**
 * https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#
 * equalsIgnoreCase-java.lang.String-
 * - **Using the "this" Keyword in Java**
 * https://www.w3schools.com/java/ref_keyword_this.asp
 * 
 * -----------------------------------------------------------------------------
 * ---
 * Java Collections & Data Structures
 * -----------------------------------------------------------------------------
 * ---
 * - **Using ArrayList for Storing Rooms**
 * https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
 * - **Working with List<String> for Amenities**
 * https://www.baeldung.com/java-list
 * - **Iterating Over Lists in Java (for-each loop)**
 * https://www.geeksforgeeks.org/for-each-loop-in-java/
 * 
 * -----------------------------------------------------------------------------
 * ---
 * Console-Based User Interface (UI)
 * -----------------------------------------------------------------------------
 * ---
 * - **Console-Based UI in Java**
 * https://www.geeksforgeeks.org/java-console-based-application/
 * - **Using Scanner for User Input**
 * https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
 * - **Handling User Input Errors with try-catch**
 * https://docs.oracle.com/javase/tutorial/essential/exceptions/
 * - **Using switch-case for Menu Handling**
 * https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
 * - **Looping the Menu Until Exit (while loop)**
 * https://www.baeldung.com/java-while-loop
 * - **Checking User Input with nextLine() vs nextInt()**
 * https://stackoverflow.com/questions/13102045/scanner-is-skipping-nextline-
 * after-using-next-or-nextfoo
 * 
 * -----------------------------------------------------------------------------
 * ---
 * Exception Handling & Input Validation
 * -----------------------------------------------------------------------------
 * ---
 * - **Catching Invalid Input with try-catch**
 * https://docs.oracle.com/javase/tutorial/essential/exceptions/
 * - **Using NumberFormatException for Input Validation**
 * https://www.baeldung.com/java-exceptions
 * - **Ensuring User Inputs Valid Numbers**
 * https://stackoverflow.com/questions/19421809/how-to-make-a-java-scanner-ask-
 * again-for-an-input-if-the-input-wasnt-an-integer
 * 
 * -----------------------------------------------------------------------------
 * ---
 * JUnit Testing
 * -----------------------------------------------------------------------------
 * ---
 * - **JUnit 5 Testing Framework**
 * https://junit.org/junit5/docs/current/user-guide/
 * - **Using @BeforeEach to Initialize Tests**
 * https://www.baeldung.com/junit-5
 * - **Assertions (assertEquals(), assertTrue(), assertFalse())**
 * https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/
 * jupiter/api/Assertions.html
 * - **Mocking and Stubbing in Unit Tests**
 * https://www.baeldung.com/mockito-series
 * - **Testing Exception Handling in Java**
 * https://www.baeldung.com/java-exceptions
 * 
 * -----------------------------------------------------------------------------
 * ---
 * Java Best Practices & Code Optimization
 * -----------------------------------------------------------------------------
 * ---
 * - **Java Code Formatting Guidelines**
 * https://google.github.io/styleguide/javaguide.html
 * - **Optimizing Java Performance**
 * https://www.baeldung.com/java-performance-optimization
 * - **Using Constants Instead of Hardcoded Values**
 * https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/
 * - **Avoiding NullPointerException (NPE) in Java**
 * https://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and
 * -how-do-i-fix-it
 * 
 * -----------------------------------------------------------------------------
 * ---
 * Code Attribution
 * -----------------------------------------------------------------------------
 * ---
 * This Java-based Hotel Management System (Hotel.java, Room.java,
 * HotelApp.java,
 * and HotelTest.java) was developed using Java best practices,
 * industry-standard
 * programming techniques, and community learning resources. The project adheres
 * to official Java documentation, UBC CPSC 210 course materials, and widely
 * accepted coding guidelines to ensure maintainability, efficiency, and
 * readability.
 * All referenced materials are cited above to ensure transparency and best
 * practices compliance.
 * 
 * =============================================================================
 * ===
 */

/*
 * Citations for JSON Implementation in Java:
 * 
 * 1. CPSC 210: Teller App Example (UBC)
 * - Used for implementing JSON persistence in Java applications.
 * 
 * 
 * 2. CPSC 210: Workroom App Example
 * - Helped design the JSON structure for saving and loading data.
 * 
 * 
 * 3. Reading JSON Files in Java
 * - Used for reading hotel data from a JSON file.
 * - Link: https://www.baeldung.com/java-org-json
 * 
 * 4. Writing JSON to a File in Java
 * - Used to save hotel data into a JSON file.
 * - Link: https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html
 * 
 * 5. Using JSONObject and JSONArray in Java
 * - Used for converting Hotel and Room objects to JSON format.
 * - Link: https://stleary.github.io/JSON-java/index.html
 * 
 * 6. Handling null Cases in JSON
 * - Ensures that writing or reading null values doesn't crash the program.
 * - Link: https://www.baeldung.com/java-null
 * 
 * 7. JUnit Testing for JSON Persistence
 * - Used to test reading and writing JSON files correctly.
 * - Link: https://junit.org/junit5/docs/current/user-guide/
 * - Link: https://www.baeldung.com/jackson-tutorial-to-json
 * 
 * 8. Using JSONArray for Storing Lists
 * - Used to store lists of rooms and amenities in JSON.
 * - Link: https://www.tutorialspoint.com/json/json_java_example.htm
 * 
 * 9. Best Practices for Java File Handling
 * - Ensures proper opening, writing, and closing of files.
 * - Link: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
 * 
 * 10. Handling Edge Cases in JSON (Missing or Corrupt Data)
 * - Helps prevent crashes when JSON files are missing or contain errors.
 * - Link: https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
 * 
 * /* /*
 */
