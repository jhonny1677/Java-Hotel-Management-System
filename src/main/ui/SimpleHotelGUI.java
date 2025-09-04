package ui;

import model.*;
import persistence.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimpleHotelGUI extends JFrame {
    private Hotel hotel;
    private JTextArea displayArea;

    public SimpleHotelGUI() {
        // Create hotel with rooms (using original logic)
        hotel = new Hotel("Test Hotel", "Test City");
        
        setupGUI();
        displayRooms();
    }

    private void setupGUI() {
        setTitle("Simple Hotel Management - Working Version");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Rooms");
        JButton bookButton = new JButton("Book Room 1");
        JButton availableButton = new JButton("Show Available");
        
        refreshButton.addActionListener(e -> displayRooms());
        bookButton.addActionListener(e -> bookRoom());
        availableButton.addActionListener(e -> showAvailable());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(availableButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    private void displayRooms() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HOTEL ROOMS ===\n\n");
        
        List<Room> allRooms = hotel.getAllRooms();
        sb.append("Total Rooms: ").append(allRooms.size()).append("\n\n");
        
        for (int i = 0; i < Math.min(20, allRooms.size()); i++) {
            Room room = allRooms.get(i);
            sb.append(String.format("Room %d: %s - $%.2f - %s\n", 
                room.getRoomNumber(), 
                room.getRoomType(), 
                room.getPrice(),
                room.isAvailable() ? "Available" : "Booked"));
        }
        
        if (allRooms.size() > 20) {
            sb.append("... and ").append(allRooms.size() - 20).append(" more rooms\n");
        }
        
        displayArea.setText(sb.toString());
    }

    private void bookRoom() {
        boolean success = hotel.bookRoom(1);
        if (success) {
            JOptionPane.showMessageDialog(this, "Room 1 booked successfully!");
            displayRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed - room may already be booked.");
        }
    }

    private void showAvailable() {
        List<Room> available = hotel.findAvailableRooms();
        JOptionPane.showMessageDialog(this, "Available rooms: " + available.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleHotelGUI());
    }
}