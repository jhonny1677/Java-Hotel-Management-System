package ui;

import persistence.JsonReader;
import persistence.JsonWriter;
import model.*;
import model.Event;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Hotel Management System GUI
 * Provides a graphical user interface (GUI) for managing hotel rooms.
 */
public class HotelManagementGUI extends JFrame {
    private static final String JSON_FILE = "./data/hotelData.json";

    private Hotel hotel;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private JTextArea displayArea;
    private JButton viewRoomsBtn;
    private JButton bookRoomBtn;
    private JButton cancelRoomBtn;
    private JButton saveDataBtn;
    private JButton loadDataBtn;
    private JButton searchRoomBtn;
    private JButton addRoomBtn;
    private JButton deleteRoomBtn;
    private JButton addAmenityBtn;
    private JButton removeAmenityBtn;
    private JTextField roomTypeField;
    private JTextField roomPriceField;
    private JTextField deleteRoomField;
    private JTextField amenityRoomField;
    private JTextField amenityField;

    /**
     * REQUIRES: JSON file path must be valid, or a new file will be created if not
     * found.
     * MODIFIES: this
     * EFFECTS: Initializes the GUI by setting up hotel data, JSON reader and
     * writer,
     * and invoking setupGUI() method to construct the interface.
     */
    public HotelManagementGUI() {
        showSplashScreen();

        hotel = new Hotel("Hilton Hotel", "New York");
        jsonWriter = new JsonWriter(JSON_FILE);
        jsonReader = new JsonReader(JSON_FILE);

        setupGUI();
    }

    /**
     * MODIFIES: this
     * EFFECTS: Sets up the main GUI layout, including title label, text area,
     * buttons, and input fields.
     * Adds all components to the frame and sets it to be visible.
     */

    private void setupGUI() {
        setTitle("Hotel Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Hotel Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setupButtons();
        setupInputFields();

        addEventListeners();
        setVisible(true);

        // EFFECTS: Adds a window listener to capture application exit and print event
        // logs.
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                hotel.printEventLog(); // Print the event log when application closes
                e.getWindow().dispose();
            }
        });
    }

    /**
     * MODIFIES: this
     * EFFECTS: Creates and adds all the buttons required for the user interface.
     * Adds buttons to a button panel and positions them at the bottom of the
     * window.
     */

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        viewRoomsBtn = createButton("View Available Rooms");
        bookRoomBtn = createButton("Book a Room");
        cancelRoomBtn = createButton("Cancel a Booking");
        searchRoomBtn = createButton("Find Room by Type");
        saveDataBtn = createButton("Save Data");
        loadDataBtn = createButton("Load Data");
        addRoomBtn = createButton("Add Room");
        deleteRoomBtn = createButton("Delete Room");
        addAmenityBtn = createButton("Add Amenity");
        removeAmenityBtn = createButton("Remove Amenity");

        buttonPanel.add(viewRoomsBtn);
        buttonPanel.add(bookRoomBtn);
        buttonPanel.add(cancelRoomBtn);
        buttonPanel.add(searchRoomBtn);
        buttonPanel.add(saveDataBtn);
        buttonPanel.add(loadDataBtn);
        buttonPanel.add(addRoomBtn);
        buttonPanel.add(deleteRoomBtn);
        buttonPanel.add(addAmenityBtn);
        buttonPanel.add(removeAmenityBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * MODIFIES: this
     * EFFECTS: Creates input fields for entering room type, price, deletion,
     * and amenities. Adds them to a panel positioned on the right of the window.
     */
    private void setupInputFields() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Room Management"));

        roomTypeField = new JTextField();
        roomPriceField = new JTextField();
        deleteRoomField = new JTextField();
        amenityRoomField = new JTextField();
        amenityField = new JTextField();

        inputPanel.add(new JLabel("Room Type:"));
        inputPanel.add(roomTypeField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(roomPriceField);
        inputPanel.add(new JLabel("Room to Delete:"));
        inputPanel.add(deleteRoomField);
        inputPanel.add(new JLabel("Room Number (Amenity):"));
        inputPanel.add(amenityRoomField);
        inputPanel.add(new JLabel("Amenity:"));
        inputPanel.add(amenityField);

        add(inputPanel, BorderLayout.EAST);
    }

    /**
     * REQUIRES: text must not be null.
     * EFFECTS: Creates a JButton with the given text and returns it.
     */

    private JButton createButton(String text) {
        return new JButton(text);
    }

    /**
     * REQUIRES: message must not be null.
     * EFFECTS: Displays an error message dialog with the given message.
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * REQUIRES: Buttons must be initialized and not null.
     * MODIFIES: this
     * EFFECTS: Adds action listeners to all buttons, mapping each button to its
     * corresponding event handling method.
     */

    private void addEventListeners() {
        viewRoomsBtn.addActionListener(e -> displayAvailableRooms());
        bookRoomBtn.addActionListener(e -> bookRoom());
        cancelRoomBtn.addActionListener(e -> cancelBooking());
        searchRoomBtn.addActionListener(e -> searchRoomByType());
        saveDataBtn.addActionListener(e -> saveHotelData());
        loadDataBtn.addActionListener(e -> loadHotelData());
        addRoomBtn.addActionListener(e -> addRoom());
        deleteRoomBtn.addActionListener(e -> deleteRoom());
        addAmenityBtn.addActionListener(e -> addAmenityToRoom());
        removeAmenityBtn.addActionListener(e -> removeAmenityFromRoom());
    }

    /**
     * REQUIRES: Hotel object must be initialized.
     * MODIFIES: displayArea
     * EFFECTS: Displays a list of all available rooms in the text area.
     * Displays room number, type, and price for each available room.
     */

    private void displayAvailableRooms() {
        displayArea.setText("=== Available Rooms ===\n");
        for (Room room : hotel.findAvailableRooms()) {
            displayArea.append("Room " + room.getRoomNumber() + " - Type: " + room.getRoomType()
                    + " - Price: $" + room.getPrice() + "\n");
        }
    }

    /**
     * REQUIRES: Hotel object must be initialized.
     * MODIFIES: hotel
     * EFFECTS: Books a room if it is available and displays a success message.
     * Displays an error message if the room is unavailable or invalid input is
     * provided.
     */

    private void bookRoom() {
        String input = JOptionPane.showInputDialog(this, "Enter Room Number to Book:");
        if (input != null) {
            int roomNumber = Integer.parseInt(input);
            if (hotel.bookRoom(roomNumber)) {
                JOptionPane.showMessageDialog(this, "Room " + roomNumber + " booked successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed! Room unavailable.");
            }
        }
    }

    /**
     * REQUIRES: Hotel object must be initialized.
     * MODIFIES: hotel
     * EFFECTS: Cancels a booking for a specified room and displays a success
     * message.
     * Displays an error message if the room is not booked or invalid input is
     * provided.
     */

    private void cancelBooking() {
        String input = JOptionPane.showInputDialog(this, "Enter Room Number to Cancel Booking:");
        if (input != null) {
            int roomNumber = Integer.parseInt(input);
            if (hotel.cancelReservation(roomNumber)) {
                JOptionPane.showMessageDialog(this, "Booking for Room " + roomNumber + " canceled.");
            } else {
                JOptionPane.showMessageDialog(this, "Cancellation failed! Room was not booked.");
            }
        }
    }

    /**
     * REQUIRES: Hotel object must be initialized.
     * MODIFIES: displayArea
     * EFFECTS: Searches for available rooms of a specified type and displays them
     * in the text area.
     * Displays an error message if no rooms of the specified type are available.
     */

    private void searchRoomByType() {
        String roomType = JOptionPane.showInputDialog(this, "Enter Room Type (Single, Double, Suite):");
        if (roomType != null) {
            displayArea.setText("=== Available " + roomType + " Rooms ===\n");
            for (Room room : hotel.findAvailableRoomsByType(roomType)) {
                displayArea.append("Room " + room.getRoomNumber() + " - Price: $" + room.getPrice() + "\n");
            }
        }
    }

    /**
     * REQUIRES: hotel object must be initialized, valid room type and price must be
     * provided.
     * MODIFIES: hotel
     * EFFECTS: Adds a new room to the hotel with the specified type and price.
     * Displays a success message upon addition or an error message for invalid
     * input.
     */

    private void addRoom() {
        String type = roomTypeField.getText();
        String priceText = roomPriceField.getText();

        if (type.isEmpty() || priceText.isEmpty()) {
            showErrorMessage("Please enter both Room Type and Price.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            Room newRoom = hotel.addRoom(type, price);
            JOptionPane.showMessageDialog(this, "Room " + newRoom.getRoomNumber() + " added successfully.");
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid price! Please enter a valid number.");
        }
    }

    /**
     * REQUIRES: hotel object must be initialized.
     * MODIFIES: hotel
     * EFFECTS: Removes a room with the specified room number.
     * Displays success message upon successful removal or an error message if room
     * is not found.
     */

    private void deleteRoom() {
        String roomNumberText = deleteRoomField.getText();

        if (roomNumberText.isEmpty()) {
            showErrorMessage("Please enter a Room Number to delete.");
            return;
        }

        try {
            int roomNumber = Integer.parseInt(roomNumberText);
            if (hotel.removeRoom(roomNumber)) {
                JOptionPane.showMessageDialog(this, "Room " + roomNumber + " deleted successfully.");
            } else {
                showErrorMessage("Room not found.");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid Room Number! Please enter a valid number.");
        }
    }

    /**
     * REQUIRES: hotel object must be initialized.
     * MODIFIES: hotel
     * EFFECTS: Removes an amenity from the specified room.
     * Displays success message if amenity is removed or error message if it doesn't
     * exist.
     */

    private void removeAmenityFromRoom() {
        int roomNumber = Integer.parseInt(amenityRoomField.getText());
        String amenity = amenityField.getText();

        Room room = hotel.getRoom(roomNumber);
        if (room != null && room.getAmenities().contains(amenity)) {
            room.removeAmenity(amenity);
            JOptionPane.showMessageDialog(this, "Amenity removed from Room " + roomNumber);
        } else {
            JOptionPane.showMessageDialog(this, "Amenity not found.");
        }
    }

    /**
     * REQUIRES: hotel object must be initialized and non-null.
     * MODIFIES: JSON file specified by jsonWriter
     * EFFECTS: Writes the current hotel data to a JSON file and displays a success
     * message.
     * Displays an error message if an IOException occurs during the write
     * operation.
     */

    private void saveHotelData() {
        try {
            jsonWriter.open();
            jsonWriter.write(hotel);
            jsonWriter.close();
            hotel.logSaveEvent(); // Log the event from the Hotel class
            JOptionPane.showMessageDialog(this, "Hotel data saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving hotel data: " + e.getMessage());
        }
    }

    /**
     * REQUIRES: The JSON file specified by jsonReader must exist and be properly
     * formatted.
     * MODIFIES: hotel
     * EFFECTS: Loads hotel data from a JSON file, replaces the current hotel object
     * with the loaded data,
     * and displays the available rooms. Shows an error message if an IOException
     * occurs during the read operation.
     */

    private void loadHotelData() {
        try {
            hotel = jsonReader.read();
            hotel.logLoadEvent(); // Log the event from the Hotel class
            JOptionPane.showMessageDialog(this, "Hotel data loaded successfully.");
            displayAvailableRooms();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading hotel data: " + e.getMessage());
        }
    }

    /**
     * REQUIRES: hotel object must be initialized and non-null.
     * roomNumberText and amenity must be provided (not empty).
     * MODIFIES: The amenities list of the specified room (if found).
     * EFFECTS: Adds an amenity to a specified room if it exists.
     * Displays a success message if the operation is successful.
     * Displays an error message if the room does not exist or the room number is
     * invalid.
     */

    private void addAmenityToRoom() {
        String roomNumberText = amenityRoomField.getText();
        String amenity = amenityField.getText();

        // Check if fields are empty and show an error message
        if (roomNumberText.isEmpty() || amenity.isEmpty()) {
            showErrorMessage("Please enter both Room Number and Amenity.");
            return;
        }

        try {
            int roomNumber = Integer.parseInt(roomNumberText);
            Room room = hotel.getRoom(roomNumber);

            if (room != null) {
                room.addAmenity(amenity);
                JOptionPane.showMessageDialog(this,
                        "Amenity '" + amenity + "' added to Room " + roomNumber + " successfully.");
            } else {
                showErrorMessage("Room not found.");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid Room Number! Please enter a valid number.");
        }
    }

    /**
     * REQUIRES: The splash image file must exist at the specified location.
     * MODIFIES: This JFrame (creates a temporary JWindow for splash screen display)
     * EFFECTS: Displays a splash screen for 2 seconds and then closes it.
     * If the image is not found, an empty window is displayed instead.
     */

    private void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(400, 300);
        splash.setLocationRelativeTo(null);

        ImageIcon splashIcon = new ImageIcon("data/splash.jpg");
        JLabel splashLabel = new JLabel(
                new ImageIcon(splashIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
        splash.add(splashLabel);

        splash.setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        splash.setVisible(false);
        splash.dispose();
    }

    // private void printLogOnExit() {
    // System.out.println("\n=== Event Log ===");
    // for (Event event : EventLog.getInstance()) {
    // System.out.println(event);
    // }

    // // EFFECTS: Prints all the events in the EventLog to the console.
    // public void printEventLog() {
    // EventLog eventLog = EventLog.getInstance();
    // for (Event event : eventLog) {
    // System.out.println(event.toString());
    // }
    // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelManagementGUI::new);
    }
}

/**
 * CITATIONS:
 * 
 * - Java Swing Library:
 * Documentation:
 * https://docs.oracle.com/javase/8/docs/technotes/guides/swing/index.html
 * Description: The official Java Swing library documentation provides
 * information about how to
 * build graphical user interfaces (GUIs) using the Swing framework. The
 * documentation includes details
 * about JFrame, JPanel, JButton, JTextArea, JScrollPane, and other Swing
 * components used in this project.
 * 
 * - Java Swing Components Tutorial:
 * Tutorial:
 * https://docs.oracle.com/javase/tutorial/uiswing/components/index.html
 * Description: A comprehensive guide on how to use Java Swing components for
 * creating user interfaces.
 * This tutorial covers various Swing components such as JButton, JLabel,
 * JTextArea, JOptionPane,
 * and layouts like BorderLayout, GridLayout which are used to organize the GUI
 * in this application.
 * 
 * - JOptionPane Usage:
 * Documentation:
 * https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html
 * Description: The official documentation for the JOptionPane class which
 * provides standard methods
 * for creating pop-up dialogs. This class is heavily used in the application to
 * interact with users,
 * providing dialogs for booking rooms, canceling reservations, saving/loading
 * data, and showing error messages.
 * 
 * - UBC CPSC 210 practice questions on EDX
 * Course Repository: Provided as part of UBC CPSC 210 course material.
 * 
 */
