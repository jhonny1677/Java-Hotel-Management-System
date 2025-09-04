package ui;

import config.ApplicationContext;
import auth.*;
import model.*;
import services.*;
import analytics.*;
import payment.*;
import pricing.*;
import concurrency.ConcurrentBookingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class EnterpriseHotelManagementGUI extends JFrame {
    private final ApplicationContext context;
    private User currentUser;
    private String currentToken;
    
    // Simple preference storage (in real app, this would be in database)
    private static final Map<String, Map<String, String>> userPreferences = new ConcurrentHashMap<>();
    private static final Map<String, Set<Integer>> userFavorites = new ConcurrentHashMap<>();
    
    // UI Components
    private JTabbedPane mainTabbedPane;
    private JTextArea outputArea;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPanel loginPanel;
    private JPanel mainPanel;

    public EnterpriseHotelManagementGUI() {
        this.context = new ApplicationContext();
        initializeGUI();
        showLoginScreen();
        
        // Add shutdown hook to save data on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down...");
            context.shutdown();
        }));
    }

    private void initializeGUI() {
        setTitle("Enterprise Hotel Management System - FAANG Level");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create main components
        createLoginPanel();
        createMainPanel();
        
        // Start with login screen
        add(loginPanel, BorderLayout.CENTER);
        
        // Add window listener for cleanup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                context.shutdown();
                System.exit(0);
            }
        });
        
        setVisible(true);
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Enterprise Hotel Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("FAANG-Level Features Demo");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        loginPanel.add(subtitleLabel, gbc);
        
        // Login form
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        
        gbc.gridx = 0; gbc.gridy = 3;
        loginPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(20);
        emailField.setText("admin@hotel.com"); // Default for demo
        loginPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setText("password123"); // Default for demo
        loginPanel.add(passwordField, gbc);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.addActionListener(this::handleLogin);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);
        
        // Demo credentials info
        JTextArea credentialsInfo = new JTextArea();
        credentialsInfo.setText("Demo Credentials:\n" +
                              "Admin: admin@hotel.com / password123\n" +
                              "Staff: staff@hotel.com / password123\n" +
                              "Guest: guest@hotel.com / password123");
        credentialsInfo.setEditable(false);
        credentialsInfo.setBackground(new Color(245, 245, 245));
        credentialsInfo.setBorder(BorderFactory.createTitledBorder("Available Accounts"));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(credentialsInfo, gbc);
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Create tabbed pane
        mainTabbedPane = new JTabbedPane();
        
        // Top panel with user info and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userInfoLabel = new JLabel();
        JButton logoutButton = new JButton("Logout & Save Data");
        
        logoutButton.addActionListener(e -> {
            // Save data before logout
            context.shutdown();
            
            // Clear current user
            currentUser = null;
            currentToken = null;
            SecurityContext.clear();
            
            // Show login screen
            outputArea.setText(""); // Clear output
            showLoginScreen();
            
            JOptionPane.showMessageDialog(this, "Logged out successfully! All data has been saved.");
        });
        
        topPanel.add(userInfoLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Output area
        outputArea = new JTextArea(8, 0);
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Output"));
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(mainTabbedPane, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        AuthenticationService.AuthResult result = context.getAuthenticationService().authenticate(email, password);
        
        if (result.isSuccessful()) {
            currentUser = result.getUser();
            currentToken = result.getAccessToken();
            SecurityContext.setCurrentUser(currentUser);
            SecurityContext.setCurrentToken(currentToken);
            
            showMainApplication();
            appendOutput("✓ Login successful! Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        } else {
            JOptionPane.showMessageDialog(this, "Login failed: " + result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoginScreen() {
        remove(mainPanel);
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showMainApplication() {
        remove(loginPanel);
        
        // Update user info label
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getComponentCount() > 0) {
                Component firstChild = ((JPanel) comp).getComponent(0);
                if (firstChild instanceof JLabel) {
                    ((JLabel) firstChild).setText("Logged in as: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
                    break;
                }
            }
        }
        
        // Clear and rebuild tabs based on user role
        mainTabbedPane.removeAll();
        
        // Common tabs for all users
        createBookingTab();
        createRoomsTab();
        createUserProfileTab();
        createNotificationsTab();
        createPricingTab();
        
        // Role-specific tabs
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.STAFF) {
            createAnalyticsTab();
            createPaymentTab();
            createSystemTab();
        }
        
        if (currentUser.getRole() == Role.ADMIN) {
            createUserManagementTab();
        }
        
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        // Load initial data
        refreshRoomData();
    }

    private void createBookingTab() {
        JPanel bookingPanel = new JPanel(new BorderLayout());
        
        // Bookings list (declare early for use in lambda expressions)
        JTextArea bookingsArea = new JTextArea(15, 0);
        bookingsArea.setEditable(false);
        
        // Booking form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Booking"));
        
        JTextField roomNumberField = new JTextField();
        JTextField checkInField = new JTextField("2024-01-15");
        JTextField checkOutField = new JTextField("2024-01-17");
        
        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumberField);
        formPanel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        formPanel.add(checkInField);
        formPanel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        formPanel.add(checkOutField);
        
        JButton bookButton = new JButton("Book Room (Async)");
        JButton cancelButton = new JButton("Cancel Booking");
        JButton viewBookingsButton = new JButton("View My Bookings");
        JButton checkInButton = new JButton("Check In");
        JButton checkOutButton = new JButton("Check Out");
        
        formPanel.add(bookButton);
        formPanel.add(cancelButton);
        formPanel.add(viewBookingsButton);
        formPanel.add(checkInButton);
        formPanel.add(checkOutButton);
        
        // Booking actions
        bookButton.addActionListener(e -> {
            try {
                int roomNumber = Integer.parseInt(roomNumberField.getText());
                LocalDate checkIn = LocalDate.parse(checkInField.getText());
                LocalDate checkOut = LocalDate.parse(checkOutField.getText());
                
                appendOutput("🔄 Processing booking asynchronously...");
                
                CompletableFuture<ConcurrentBookingService.BookingResult> future = 
                    context.getConcurrentBookingService().createBookingAsync(
                        currentUser.getId(), roomNumber, checkIn, checkOut);
                
                future.thenAccept(result -> {
                    SwingUtilities.invokeLater(() -> {
                        if (result.isSuccessful()) {
                            Long bookingId = result.getBooking().getId();
                            appendOutput("✓ Booking successful! BOOKING ID: " + bookingId);
                            appendOutput("📝 Remember this Booking ID for cancellation: " + bookingId);
                            JOptionPane.showMessageDialog(this, 
                                "Booking Successful!\n" +
                                "Booking ID: " + bookingId + "\n" +
                                "Room: " + result.getBooking().getRoomNumber() + "\n" +
                                "Please remember your Booking ID for cancellation!");
                            refreshRoomData();
                            refreshBookingsList(bookingsArea);
                        } else {
                            appendOutput("✗ Booking failed: " + result.getMessage());
                            JOptionPane.showMessageDialog(this, "Booking failed: " + result.getMessage());
                        }
                    });
                });
                
            } catch (Exception ex) {
                appendOutput("✗ Error: " + ex.getMessage());
            }
        });
        
        // Cancel booking action
        cancelButton.addActionListener(e -> {
            String bookingIdStr = JOptionPane.showInputDialog(this, "Enter Booking ID to Cancel:");
            if (bookingIdStr != null && !bookingIdStr.trim().isEmpty()) {
                try {
                    Long bookingId = Long.parseLong(bookingIdStr.trim());
                    appendOutput("🔄 Processing cancellation...");
                    
                    CompletableFuture<Boolean> future = 
                        context.getConcurrentBookingService().cancelBookingAsync(bookingId, currentUser.getId());
                    
                    future.thenAccept(success -> {
                        SwingUtilities.invokeLater(() -> {
                            if (success) {
                                appendOutput("✓ Booking #" + bookingId + " cancelled successfully!");
                                JOptionPane.showMessageDialog(this, "Booking cancelled! Refund will be processed within 3-5 business days.");
                                refreshBookingsList(bookingsArea);
                                refreshRoomData(); // Update room availability
                            } else {
                                appendOutput("✗ Cancellation failed - booking not found or cannot be cancelled");
                                JOptionPane.showMessageDialog(this, "Cancellation failed. Please check the booking ID.");
                            }
                        });
                    });
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid booking ID number.");
                }
            }
        });
        
        // View bookings action
        viewBookingsButton.addActionListener(e -> refreshBookingsList(bookingsArea));
        
        // Check-in action
        checkInButton.addActionListener(e -> {
            String bookingIdStr = JOptionPane.showInputDialog(this, "Enter Booking ID for Check-in:");
            if (bookingIdStr != null && !bookingIdStr.trim().isEmpty()) {
                try {
                    Long bookingId = Long.parseLong(bookingIdStr.trim());
                    
                    CompletableFuture<Boolean> future = 
                        context.getConcurrentBookingService().checkInAsync(bookingId);
                    
                    future.thenAccept(success -> {
                        SwingUtilities.invokeLater(() -> {
                            if (success) {
                                appendOutput("✓ Checked in successfully!");
                                JOptionPane.showMessageDialog(this, "Welcome! You have been checked in.");
                                refreshBookingsList(bookingsArea);
                            } else {
                                appendOutput("✗ Check-in failed");
                                JOptionPane.showMessageDialog(this, "Check-in failed. Please contact front desk.");
                            }
                        });
                    });
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid booking ID number.");
                }
            }
        });
        
        // Check-out action
        checkOutButton.addActionListener(e -> {
            String bookingIdStr = JOptionPane.showInputDialog(this, "Enter Booking ID for Check-out:");
            if (bookingIdStr != null && !bookingIdStr.trim().isEmpty()) {
                try {
                    Long bookingId = Long.parseLong(bookingIdStr.trim());
                    
                    CompletableFuture<Boolean> future = 
                        context.getConcurrentBookingService().checkOutAsync(bookingId);
                    
                    future.thenAccept(success -> {
                        SwingUtilities.invokeLater(() -> {
                            if (success) {
                                appendOutput("✓ Checked out successfully!");
                                JOptionPane.showMessageDialog(this, "Thank you for staying with us!");
                                refreshBookingsList(bookingsArea);
                            } else {
                                appendOutput("✗ Check-out failed");
                                JOptionPane.showMessageDialog(this, "Check-out failed. Please contact front desk.");
                            }
                        });
                    });
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid booking ID number.");
                }
            }
        });
        
        // Bookings list scroll pane
        JScrollPane bookingsScroll = new JScrollPane(bookingsArea);
        bookingsScroll.setBorder(BorderFactory.createTitledBorder("Your Bookings"));
        
        JButton refreshBookingsButton = new JButton("Refresh Bookings");
        refreshBookingsButton.addActionListener(e -> refreshBookingsList(bookingsArea));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(refreshBookingsButton, BorderLayout.SOUTH);
        
        bookingPanel.add(topPanel, BorderLayout.NORTH);
        bookingPanel.add(bookingsScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("Bookings", bookingPanel);
    }

    private void createRoomsTab() {
        JPanel roomsPanel = new JPanel(new BorderLayout());
        
        JTextArea roomsArea = new JTextArea();
        roomsArea.setEditable(false);
        JScrollPane roomsScroll = new JScrollPane(roomsArea);
        roomsScroll.setBorder(BorderFactory.createTitledBorder("Available Rooms"));
        
        JButton refreshRoomsButton = new JButton("Refresh Rooms");
        refreshRoomsButton.addActionListener(e -> displayRooms(roomsArea));
        
        roomsPanel.add(refreshRoomsButton, BorderLayout.NORTH);
        roomsPanel.add(roomsScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("Rooms", roomsPanel);
    }

    private void createUserProfileTab() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        
        // User Info Section
        JPanel userInfoPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        
        JLabel nameLabel = new JLabel("Name: " + (currentUser != null ? currentUser.getName() : "Guest"));
        JLabel emailLabel = new JLabel("Email: " + (currentUser != null ? currentUser.getEmail() : "N/A"));
        JLabel roleLabel = new JLabel("Role: " + (currentUser != null ? currentUser.getRole() : "GUEST"));
        JLabel phoneLabel = new JLabel("Phone: " + (currentUser != null && currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Not set"));
        JLabel membershipLabel = new JLabel("Membership: Gold Member");
        
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(new JLabel()); // Empty cell
        userInfoPanel.add(emailLabel);
        userInfoPanel.add(new JLabel());
        userInfoPanel.add(roleLabel);
        userInfoPanel.add(new JLabel());
        userInfoPanel.add(phoneLabel);
        userInfoPanel.add(new JLabel());
        userInfoPanel.add(membershipLabel);
        userInfoPanel.add(new JLabel());
        
        // Payment Methods Section
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Methods"));
        
        JTextArea paymentMethodsArea = new JTextArea(8, 0);
        paymentMethodsArea.setEditable(false);
        JScrollPane paymentScroll = new JScrollPane(paymentMethodsArea);
        
        JPanel paymentButtonPanel = new JPanel(new FlowLayout());
        JButton addCardButton = new JButton("Add Credit Card");
        JButton addPayPalButton = new JButton("Add PayPal Account");
        JButton removePaymentButton = new JButton("Remove Payment Method");
        JButton processPaymentButton = new JButton("Make Payment");
        
        // Add payment method handlers
        addCardButton.addActionListener(e -> {
            String cardNumber = JOptionPane.showInputDialog(this, "Enter Credit Card Number (16 digits):");
            if (cardNumber != null && cardNumber.length() == 16) {
                String cardName = JOptionPane.showInputDialog(this, "Enter Cardholder Name:");
                if (cardName != null && !cardName.trim().isEmpty()) {
                    // Simulate adding payment method
                    StringBuilder sb = new StringBuilder(paymentMethodsArea.getText());
                    sb.append("Credit Card: ****-****-****-" + cardNumber.substring(12) + " (" + cardName + ")\n");
                    paymentMethodsArea.setText(sb.toString());
                    appendOutput("✓ Credit card added successfully!");
                    JOptionPane.showMessageDialog(this, "Credit card added to your account!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid card number. Please enter 16 digits.");
            }
        });
        
        addPayPalButton.addActionListener(e -> {
            String paypalEmail = JOptionPane.showInputDialog(this, "Enter PayPal Email:");
            if (paypalEmail != null && paypalEmail.contains("@")) {
                StringBuilder sb = new StringBuilder(paymentMethodsArea.getText());
                sb.append("PayPal Account: " + paypalEmail + "\n");
                paymentMethodsArea.setText(sb.toString());
                appendOutput("✓ PayPal account linked successfully!");
                JOptionPane.showMessageDialog(this, "PayPal account linked to your profile!");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            }
        });
        
        processPaymentButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Please log in to make payments.");
                return;
            }
            
            String amountStr = JOptionPane.showInputDialog(this, "Enter payment amount:");
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    String method = (String) JOptionPane.showInputDialog(this, 
                        "Select payment method:", "Payment Processing",
                        JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"Credit Card", "PayPal", "Stripe"}, "Credit Card");
                    
                    if (method != null) {
                        // Process payment using the payment service
                        PaymentResult paymentResult = context.getPaymentService().processPayment(currentUser.getId(), amount, "User payment via " + method);
                        
                        if (paymentResult.isSuccessful()) {
                            appendOutput("✓ Payment of $" + amount + " processed successfully via " + method);
                            JOptionPane.showMessageDialog(this, 
                                "Payment successful!\nAmount: $" + amount + "\nMethod: " + method +
                                "\nTransaction ID: " + paymentResult.getPaymentId());
                        } else {
                            appendOutput("✗ Payment failed: " + paymentResult.getMessage());
                            JOptionPane.showMessageDialog(this, "Payment failed: " + paymentResult.getMessage());
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                }
            }
        });
        
        paymentButtonPanel.add(addCardButton);
        paymentButtonPanel.add(addPayPalButton);
        paymentButtonPanel.add(processPaymentButton);
        paymentButtonPanel.add(removePaymentButton);
        
        paymentPanel.add(paymentScroll, BorderLayout.CENTER);
        paymentPanel.add(paymentButtonPanel, BorderLayout.SOUTH);
        
        // Initialize payment methods display
        paymentMethodsArea.setText("=== YOUR PAYMENT METHODS ===\n\n" +
            "Credit Card: ****-****-****-1234 (John Doe)\n" +
            "PayPal Account: user@example.com\n" +
            "Stripe Account: Connected\n\n" +
            "Use the buttons below to add or manage payment methods.");
        
        // Booking History Section (compact version)
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Recent Bookings"));
        
        JTextArea historyArea = new JTextArea(6, 0);
        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        
        JButton viewFullHistoryButton = new JButton("View Full History");
        viewFullHistoryButton.addActionListener(e -> {
            // Switch to bookings tab
            mainTabbedPane.setSelectedIndex(0); // Bookings is first tab
        });
        
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        historyPanel.add(viewFullHistoryButton, BorderLayout.SOUTH);
        
        // Room Preferences Section
        JPanel preferencesPanel = new JPanel(new BorderLayout());
        preferencesPanel.setBorder(BorderFactory.createTitledBorder("Room Preferences & Favorites"));
        
        JTextArea preferencesArea = new JTextArea(8, 0);
        preferencesArea.setEditable(false);
        JScrollPane preferencesScroll = new JScrollPane(preferencesArea);
        
        JPanel preferencesButtonPanel = new JPanel(new FlowLayout());
        JButton setPreferencesButton = new JButton("Set Preferences");
        JButton addToFavoritesButton = new JButton("Add Room to Favorites");
        JButton viewFavoritesButton = new JButton("View All Favorites");
        JButton clearPreferencesButton = new JButton("Clear Preferences");
        
        // Set preferences handler
        setPreferencesButton.addActionListener(e -> {
            String roomType = (String) JOptionPane.showInputDialog(this,
                "Select preferred room type:", "Room Preferences",
                JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Single", "Double", "Suite", "Any"}, "Any");
            
            if (roomType != null) {
                String[] priceRanges = {"Under $100", "$100-$200", "$200-$300", "Above $300", "Any"};
                String priceRange = (String) JOptionPane.showInputDialog(this,
                    "Select price range:", "Price Preferences",
                    JOptionPane.QUESTION_MESSAGE, null, priceRanges, "Any");
                
                if (priceRange != null) {
                    String[] amenityOptions = {"WiFi", "TV", "Mini Fridge", "Balcony", "Room Service", "None"};
                    String amenities = (String) JOptionPane.showInputDialog(this,
                        "Select required amenity:", "Amenity Preferences",
                        JOptionPane.QUESTION_MESSAGE, null, amenityOptions, "None");
                    
                    // Save preferences
                    String userEmail = currentUser.getEmail();
                    Map<String, String> prefs = userPreferences.computeIfAbsent(userEmail, k -> new HashMap<>());
                    prefs.put("roomType", roomType);
                    prefs.put("priceRange", priceRange);
                    prefs.put("amenity", amenities != null ? amenities : "None");
                    
                    // Update preferences display
                    updatePreferencesDisplay(preferencesArea);
                    
                    appendOutput("✓ Room preferences updated and saved!");
                    JOptionPane.showMessageDialog(this, "Preferences saved! We'll use these to recommend rooms for you.");
                }
            }
        });
        
        // Add to favorites handler
        addToFavoritesButton.addActionListener(e -> {
            String roomNumberStr = JOptionPane.showInputDialog(this, "Enter room number to add to favorites:");
            if (roomNumberStr != null && !roomNumberStr.trim().isEmpty()) {
                try {
                    int roomNumber = Integer.parseInt(roomNumberStr.trim());
                    Optional<model.Room> room = context.getRoomRepository().findByRoomNumber(roomNumber);
                    
                    if (room.isPresent()) {
                        model.Room r = room.get();
                        
                        // Save to favorites
                        String userEmail = currentUser.getEmail();
                        Set<Integer> favorites = userFavorites.computeIfAbsent(userEmail, k -> new HashSet<>());
                        favorites.add(roomNumber);
                        
                        // Update display
                        updatePreferencesDisplay(preferencesArea);
                        
                        appendOutput("✓ Room " + roomNumber + " added to favorites and saved!");
                        JOptionPane.showMessageDialog(this, "Room " + roomNumber + " added to your favorites!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Room " + roomNumber + " not found.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid room number.");
                }
            }
        });
        
        // View favorites handler
        viewFavoritesButton.addActionListener(e -> {
            String userEmail = currentUser.getEmail();
            Set<Integer> favorites = userFavorites.get(userEmail);
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== ALL FAVORITE ROOMS ===\n\n");
            
            if (favorites == null || favorites.isEmpty()) {
                sb.append("No favorite rooms yet.\n");
                sb.append("Use 'Add Room to Favorites' to save rooms you like!");
            } else {
                boolean foundFavorites = false;
                for (int roomNum : favorites) {
                    Optional<model.Room> room = context.getRoomRepository().findByRoomNumber(roomNum);
                    if (room.isPresent()) {
                        model.Room r = room.get();
                        foundFavorites = true;
                        sb.append(String.format("⭐ Room %d - %s - $%.2f/night\n", 
                            r.getRoomNumber(), r.getRoomType(), r.getPrice()));
                        sb.append(String.format("   Status: %s | Amenities: %s\n", 
                            r.isAvailable() ? "Available" : "Occupied",
                            String.join(", ", r.getAmenities())));
                        sb.append("   " + r.getDescription() + "\n\n");
                    }
                }
                
                if (!foundFavorites) {
                    sb.append("Your favorite rooms are no longer available in the system.");
                }
            }
            
            preferencesArea.setText(sb.toString());
            appendOutput("Displaying your saved favorite rooms");
        });
        
        preferencesButtonPanel.add(setPreferencesButton);
        preferencesButtonPanel.add(addToFavoritesButton);
        preferencesButtonPanel.add(viewFavoritesButton);
        preferencesButtonPanel.add(clearPreferencesButton);
        
        preferencesPanel.add(preferencesScroll, BorderLayout.CENTER);
        preferencesPanel.add(preferencesButtonPanel, BorderLayout.SOUTH);
        
        // Initialize preferences display
        updatePreferencesDisplay(preferencesArea);
        
        // Layout the profile panel with 3 sections
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(userInfoPanel, BorderLayout.WEST);
        topSection.add(historyPanel, BorderLayout.EAST);
        
        JPanel middleSection = new JPanel(new BorderLayout());
        middleSection.add(preferencesPanel, BorderLayout.CENTER);
        
        profilePanel.add(topSection, BorderLayout.NORTH);
        profilePanel.add(middleSection, BorderLayout.CENTER);
        profilePanel.add(paymentPanel, BorderLayout.SOUTH);
        
        // Initialize booking history display
        refreshUserBookingHistory(historyArea);
        
        mainTabbedPane.addTab("User Profile", profilePanel);
    }

    private void createNotificationsTab() {
        JPanel notificationsPanel = new JPanel(new BorderLayout());
        
        JTextArea notificationsArea = new JTextArea(15, 0);
        notificationsArea.setEditable(false);
        JScrollPane notificationsScroll = new JScrollPane(notificationsArea);
        notificationsScroll.setBorder(BorderFactory.createTitledBorder("Notifications & Alerts"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshNotificationsButton = new JButton("Refresh Notifications");
        JButton markAllReadButton = new JButton("Mark All as Read");
        JButton clearNotificationsButton = new JButton("Clear All");
        
        refreshNotificationsButton.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=== NOTIFICATIONS & ALERTS ===\n\n");
            sb.append("🔔 Welcome to the hotel management system!\n");
            sb.append("📅 Your booking #12345 check-in is tomorrow (Room 205)\n");
            sb.append("💳 Payment of $450.00 processed successfully\n");
            sb.append("⭐ Room 101 is now available - one of your favorites!\n");
            sb.append("🎉 Congratulations! You've earned Gold Member status\n");
            sb.append("📧 Booking confirmation sent to your email\n");
            sb.append("🔧 Scheduled maintenance: Room 150 (your favorite) unavailable Dec 15-16\n");
            sb.append("💰 Special offer: 20% off weekend bookings for Gold members\n");
            sb.append("📱 New mobile app features available - check out room virtual tours!\n");
            sb.append("⚠️  Weather alert: Heavy rain expected - indoor amenities recommended\n\n");
            sb.append("=== LOYALTY PROGRAM UPDATES ===\n\n");
            sb.append("🏆 Current Status: Gold Member (750 points)\n");
            sb.append("🎯 Next Level: Platinum (1000 points) - 250 points to go!\n");
            sb.append("💎 Platinum Benefits: Free room upgrades, late checkout, complimentary breakfast\n");
            sb.append("📈 This month: +150 points from 3 bookings\n\n");
            sb.append("=== SYSTEM ALERTS ===\n\n");
            sb.append("✅ All services operational\n");
            sb.append("🔄 Cache performance: Excellent (97% hit rate)\n");
            sb.append("💻 System uptime: 99.8%\n");
            
            notificationsArea.setText(sb.toString());
            appendOutput("[NOTIFICATIONS] Notifications refreshed");
        });
        
        markAllReadButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "All notifications marked as read!");
            appendOutput("✓ All notifications marked as read");
        });
        
        clearNotificationsButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to clear all notifications?", 
                "Clear Notifications", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                notificationsArea.setText("=== NOTIFICATIONS & ALERTS ===\n\nNo notifications.\n\nYou're all caught up!");
                appendOutput("✓ All notifications cleared");
            }
        });
        
        buttonPanel.add(refreshNotificationsButton);
        buttonPanel.add(markAllReadButton);
        buttonPanel.add(clearNotificationsButton);
        
        notificationsPanel.add(notificationsScroll, BorderLayout.CENTER);
        notificationsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize with sample notifications
        StringBuilder sb = new StringBuilder();
        sb.append("=== NOTIFICATIONS & ALERTS ===\n\n");
        sb.append("🔔 Welcome to the hotel management system!\n");
        sb.append("📅 Your booking #12345 check-in is tomorrow (Room 205)\n");
        sb.append("💳 Payment of $450.00 processed successfully\n");
        sb.append("⭐ Room 101 is now available - one of your favorites!\n");
        sb.append("🎉 Congratulations! You've earned Gold Member status\n");
        sb.append("📧 Booking confirmation sent to your email\n");
        sb.append("🔧 Scheduled maintenance: Room 150 (your favorite) unavailable Dec 15-16\n");
        sb.append("💰 Special offer: 20% off weekend bookings for Gold members\n");
        sb.append("📱 New mobile app features available - check out room virtual tours!\n");
        sb.append("⚠️  Weather alert: Heavy rain expected - indoor amenities recommended\n\n");
        sb.append("=== LOYALTY PROGRAM STATUS ===\n\n");
        sb.append("🏆 Current Status: Gold Member (750 points)\n");
        sb.append("🎯 Next Level: Platinum (1000 points) - 250 points to go!\n");
        sb.append("💎 Platinum Benefits: Free room upgrades, late checkout, complimentary breakfast\n");
        sb.append("📈 This month: +150 points from 3 bookings\n\n");
        sb.append("=== QUICK ACTIONS ===\n\n");
        sb.append("• Use 'Mark All as Read' to clear notification badges\n");
        sb.append("• Check your User Profile for loyalty program details\n");
        sb.append("• Visit Bookings tab to view upcoming reservations");
        
        notificationsArea.setText(sb.toString());
        
        mainTabbedPane.addTab("Notifications", notificationsPanel);
    }

    private void createPricingTab() {
        JPanel pricingPanel = new JPanel(new BorderLayout());
        
        // Pricing form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dynamic Pricing Calculator"));
        
        JTextField roomNumField = new JTextField("101");
        JTextField checkInDateField = new JTextField("2024-07-15");
        JTextField checkOutDateField = new JTextField("2024-07-18");
        
        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumField);
        formPanel.add(new JLabel("Check-in Date:"));
        formPanel.add(checkInDateField);
        formPanel.add(new JLabel("Check-out Date:"));
        formPanel.add(checkOutDateField);
        
        JButton calculateButton = new JButton("Calculate Dynamic Price");
        formPanel.add(calculateButton);
        
        JTextArea pricingResults = new JTextArea(20, 0);
        pricingResults.setEditable(false);
        pricingResults.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane pricingScroll = new JScrollPane(pricingResults);
        
        calculateButton.addActionListener(e -> {
            try {
                int roomNumber = Integer.parseInt(roomNumField.getText());
                LocalDate checkIn = LocalDate.parse(checkInDateField.getText());
                LocalDate checkOut = LocalDate.parse(checkOutDateField.getText());
                
                Optional<model.Room> roomOpt = context.getRoomRepository().findByRoomNumber(roomNumber);
                if (roomOpt.isPresent()) {
                    model.Room room = roomOpt.get();
                    
                    DynamicPricingEngine.PricingBreakdown breakdown = 
                        context.getPricingEngine().calculatePriceBreakdown(room, checkIn, checkOut);
                    
                    pricingResults.setText("=== DYNAMIC PRICING BREAKDOWN ===\n\n" + breakdown.toString());
                    appendOutput("💰 Dynamic pricing calculated for room " + roomNumber);
                } else {
                    pricingResults.setText("Room not found!");
                }
            } catch (Exception ex) {
                pricingResults.setText("Error: " + ex.getMessage());
            }
        });
        
        pricingPanel.add(formPanel, BorderLayout.NORTH);
        pricingPanel.add(pricingScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("Dynamic Pricing", pricingPanel);
    }

    private void createAnalyticsTab() {
        JPanel analyticsPanel = new JPanel(new BorderLayout());
        
        JTextArea analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        JScrollPane analyticsScroll = new JScrollPane(analyticsArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton revenueReportButton = new JButton("Generate Revenue Report");
        JButton dashboardButton = new JButton("Analytics Dashboard");
        JButton forecastButton = new JButton("30-Day Forecast");
        JButton executiveSummaryButton = new JButton("Executive Summary");
        
        buttonPanel.add(revenueReportButton);
        buttonPanel.add(dashboardButton);
        buttonPanel.add(forecastButton);
        buttonPanel.add(executiveSummaryButton);
        
        revenueReportButton.addActionListener(e -> {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(1);
            RevenueReport report = context.getRevenueAnalytics().generateRevenueReport(startDate, endDate);
            analyticsArea.setText(report.toString());
            appendOutput("📊 Revenue report generated for last 30 days");
        });
        
        dashboardButton.addActionListener(e -> {
            var dashboard = context.getAnalyticsDashboard().getDashboardData();
            StringBuilder sb = new StringBuilder();
            sb.append("=== ANALYTICS DASHBOARD ===\n\n");
            
            @SuppressWarnings("unchecked")
            var keyMetrics = (java.util.Map<String, Double>) dashboard.get("keyMetrics");
            if (keyMetrics != null) {
                sb.append("KEY METRICS:\n");
                keyMetrics.forEach((key, value) -> 
                    sb.append(String.format("- %s: %.2f\n", key, value)));
                sb.append("\n");
            }
            
            analyticsArea.setText(sb.toString());
            appendOutput("📈 Analytics dashboard loaded");
        });
        
        forecastButton.addActionListener(e -> {
            ForecastReport forecast = context.getRevenueAnalytics().generateForecast(LocalDate.now().plusDays(1), 30);
            analyticsArea.setText(forecast.toString());
            appendOutput("🔮 30-day forecast generated");
        });
        
        executiveSummaryButton.addActionListener(e -> {
            String summary = context.getAnalyticsDashboard().generateExecutiveSummary();
            analyticsArea.setText(summary);
            appendOutput("📋 Executive summary generated");
        });
        
        analyticsPanel.add(buttonPanel, BorderLayout.NORTH);
        analyticsPanel.add(analyticsScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("Analytics", analyticsPanel);
    }

    private void createPaymentTab() {
        JPanel paymentPanel = new JPanel(new BorderLayout());
        
        JTextArea paymentArea = new JTextArea();
        paymentArea.setEditable(false);
        JScrollPane paymentScroll = new JScrollPane(paymentArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton gatewayStatusButton = new JButton("Gateway Status");
        JButton revenueButton = new JButton("Total Revenue");
        JButton paymentsButton = new JButton("Recent Payments");
        
        buttonPanel.add(gatewayStatusButton);
        buttonPanel.add(revenueButton);
        buttonPanel.add(paymentsButton);
        
        gatewayStatusButton.addActionListener(e -> {
            var statuses = context.getPaymentGatewayManager().getGatewayStatuses();
            StringBuilder sb = new StringBuilder();
            sb.append("=== PAYMENT GATEWAY STATUS ===\n\n");
            
            statuses.forEach((gatewayName, status) -> {
                @SuppressWarnings("unchecked")
                var statusMap = (java.util.Map<String, Object>) status;
                sb.append(String.format("Gateway: %s\n", gatewayName));
                sb.append(String.format("- Healthy: %s\n", statusMap.get("healthy")));
                sb.append(String.format("- Failures: %s\n", statusMap.get("failures")));
                sb.append(String.format("- Last Health Check: %s\n\n", statusMap.get("lastHealthCheck")));
            });
            
            paymentArea.setText(sb.toString());
            appendOutput("💳 Payment gateway status checked");
        });
        
        revenueButton.addActionListener(e -> {
            double revenue = context.getPaymentService().getTotalRevenue();
            double refunds = context.getPaymentService().getTotalRefunds();
            String result = String.format("Total Revenue: $%.2f\nTotal Refunds: $%.2f\nNet Revenue: $%.2f", 
                revenue, refunds, revenue - refunds);
            paymentArea.setText(result);
            appendOutput("💰 Revenue calculated");
        });
        
        paymentPanel.add(buttonPanel, BorderLayout.NORTH);
        paymentPanel.add(paymentScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("Payments", paymentPanel);
    }

    private void createSystemTab() {
        JPanel systemPanel = new JPanel(new BorderLayout());
        
        JTextArea systemArea = new JTextArea();
        systemArea.setEditable(false);
        JScrollPane systemScroll = new JScrollPane(systemArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cacheStatsButton = new JButton("Cache Statistics");
        JButton undoButton = new JButton("Undo Last Booking");
        JButton clearCacheButton = new JButton("Clear Cache");
        
        buttonPanel.add(cacheStatsButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(clearCacheButton);
        
        cacheStatsButton.addActionListener(e -> {
            var stats = context.getCacheableRoomService().getCacheStats();
            String result = String.format(
                "Cache Statistics:\n" +
                "- Total Cache Size: %d entries\n" +
                "- Available Rooms Cached: %s\n" +
                "- Room Types Cached: %d\n",
                stats.getTotalCacheSize(),
                stats.isAvailableRoomsCached() ? "Yes" : "No",
                stats.getRoomTypesCached()
            );
            systemArea.setText(result);
            appendOutput("🔧 Cache statistics retrieved");
        });
        
        undoButton.addActionListener(e -> {
            if (context.getCommandInvoker().canUndo()) {
                context.getBookingService().undoLastBooking();
                systemArea.setText("Last booking operation undone successfully.");
                appendOutput("↶ Last booking undone");
            } else {
                systemArea.setText("No operations to undo.");
            }
        });
        
        systemPanel.add(buttonPanel, BorderLayout.NORTH);
        systemPanel.add(systemScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("System", systemPanel);
    }

    private void createUserManagementTab() {
        JPanel userPanel = new JPanel(new BorderLayout());
        
        JTextArea userArea = new JTextArea();
        userArea.setEditable(false);
        JScrollPane userScroll = new JScrollPane(userArea);
        
        JButton listUsersButton = new JButton("List All Users");
        listUsersButton.addActionListener(e -> {
            List<User> users = context.getUserRepository().findAll();
            StringBuilder sb = new StringBuilder();
            sb.append("=== ALL USERS ===\n\n");
            for (User user : users) {
                sb.append(String.format("ID: %d | %s | %s | %s | Active: %s\n", 
                    user.getId(), user.getName(), user.getEmail(), 
                    user.getRole(), user.isActive()));
            }
            userArea.setText(sb.toString());
            appendOutput("👥 User list loaded");
        });
        
        userPanel.add(listUsersButton, BorderLayout.NORTH);
        userPanel.add(userScroll, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("User Management", userPanel);
    }

    private void displayRooms(JTextArea area) {
        List<model.Room> rooms = context.getRoomRepository().findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ROOM INVENTORY ===\n\n");
        
        for (model.Room room : rooms) {
            sb.append(String.format("Room %d | %s | $%.2f/night | %s | Amenities: %s\n", 
                room.getRoomNumber(), 
                room.getRoomType(), 
                room.getPrice(),
                room.isAvailable() ? "Available" : "Occupied",
                String.join(", ", room.getAmenities())));
        }
        
        area.setText(sb.toString());
        appendOutput("[ROOMS] Room data refreshed");
    }

    private void refreshRoomData() {
        // Find and refresh the rooms tab if it exists
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            if ("Rooms".equals(mainTabbedPane.getTitleAt(i))) {
                Component roomsTab = mainTabbedPane.getComponentAt(i);
                if (roomsTab instanceof JPanel) {
                    JPanel roomsPanel = (JPanel) roomsTab;
                    // Find the text area in the rooms panel
                    Component[] components = roomsPanel.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JScrollPane) {
                            JScrollPane scroll = (JScrollPane) comp;
                            Component view = scroll.getViewport().getView();
                            if (view instanceof JTextArea) {
                                displayRooms((JTextArea) view);
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }
        appendOutput("🔄 Room data refreshed");
    }

    private void refreshBookingsList(JTextArea bookingsArea) {
        if (currentUser == null) {
            bookingsArea.setText("Please log in to view bookings.");
            return;
        }
        
        List<Booking> userBookings = context.getBookingRepository().findByUserId(currentUser.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("=== YOUR BOOKINGS ===\n\n");
        
        if (userBookings.isEmpty()) {
            sb.append("No bookings found.\n");
        } else {
            for (Booking booking : userBookings) {
                sb.append("========================================\n");
                sb.append(String.format("🎫 BOOKING ID: %d (Use this to cancel)\n", booking.getId()));
                sb.append(String.format("🏨 Room: %d\n", booking.getRoomNumber()));
                sb.append(String.format("📅 Check-in: %s\n", booking.getCheckInDate()));
                sb.append(String.format("📅 Check-out: %s\n", booking.getCheckOutDate()));
                sb.append(String.format("📊 Status: %s\n", booking.getBookingStatus()));
                sb.append(String.format("💰 Total: $%.2f\n", booking.getTotalPrice()));
                
                // Show payment status if exists
                if (booking.getPaymentId() != null) {
                    Optional<Payment> payment = context.getPaymentRepository().findByPaymentId(booking.getPaymentId());
                    if (payment.isPresent()) {
                        sb.append(String.format("💳 Payment: %s (ID: %s)\n", 
                            payment.get().getStatus(), payment.get().getPaymentId()));
                    }
                }
                
                // Show cancellation instructions
                if (booking.getBookingStatus() == BookingStatus.CONFIRMED || 
                    booking.getBookingStatus() == BookingStatus.PENDING) {
                    sb.append("❗ To cancel: Use 'Cancel Booking' button with ID: " + booking.getId() + "\n");
                }
                
                sb.append("========================================\n\n");
            }
        }
        
        bookingsArea.setText(sb.toString());
        appendOutput("[BOOKINGS] Booking data refreshed for user: " + currentUser.getEmail());
    }

    private void refreshUserBookingHistory(JTextArea historyArea) {
        if (currentUser == null) {
            historyArea.setText("Please log in to view booking history.");
            return;
        }
        
        List<Booking> userBookings = context.getBookingRepository().findByUserId(currentUser.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("=== RECENT BOOKINGS ===\n\n");
        
        if (userBookings.isEmpty()) {
            sb.append("No bookings found.\n");
            sb.append("Book your first room to see history here!");
        } else {
            // Show last 3 bookings
            int count = 0;
            for (Booking booking : userBookings) {
                if (count >= 3) break;
                sb.append(String.format("Room %d | %s | $%.2f\n", 
                    booking.getRoomNumber(), 
                    booking.getBookingStatus(), 
                    booking.getTotalPrice()));
                count++;
            }
            if (userBookings.size() > 3) {
                sb.append("\n... and " + (userBookings.size() - 3) + " more");
            }
        }
        
        historyArea.setText(sb.toString());
    }
    
    private void updatePreferencesDisplay(JTextArea preferencesArea) {
        if (currentUser == null) {
            preferencesArea.setText("Please log in to view preferences.");
            return;
        }
        
        String userEmail = currentUser.getEmail();
        Map<String, String> prefs = userPreferences.get(userEmail);
        Set<Integer> favorites = userFavorites.get(userEmail);
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== YOUR ROOM PREFERENCES ===\n\n");
        
        if (prefs == null || prefs.isEmpty()) {
            sb.append("Preferred Room Type: Any\n");
            sb.append("Price Range: Any\n");
            sb.append("Required Amenity: None\n");
        } else {
            sb.append("Preferred Room Type: " + prefs.getOrDefault("roomType", "Any") + "\n");
            sb.append("Price Range: " + prefs.getOrDefault("priceRange", "Any") + "\n");
            sb.append("Required Amenity: " + prefs.getOrDefault("amenity", "None") + "\n");
        }
        
        sb.append("\n=== FAVORITE ROOMS ===\n\n");
        
        if (favorites == null || favorites.isEmpty()) {
            sb.append("No favorite rooms yet.\n");
            sb.append("Use 'Add Room to Favorites' to save rooms you like!\n");
        } else {
            for (int roomNum : favorites) {
                Optional<model.Room> room = context.getRoomRepository().findByRoomNumber(roomNum);
                if (room.isPresent()) {
                    model.Room r = room.get();
                    sb.append(String.format("⭐ Room %d - %s - $%.2f/night\n", 
                        r.getRoomNumber(), r.getRoomType(), r.getPrice()));
                }
            }
        }
        
        sb.append("\n=== RECENT SEARCHES ===\n\n");
        sb.append("Suite rooms under $350\n");
        sb.append("Double rooms with balcony\n");
        sb.append("Single rooms with WiFi\n\n");
        sb.append("Use the buttons below to customize your preferences!");
        
        preferencesArea.setText(sb.toString());
    }

    private void appendOutput(String message) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(String.format("[%s] %s\n", 
                java.time.LocalTime.now().toString().substring(0, 8), message));
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to set system look and feel
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Windows".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Use default look and feel
            }
            new EnterpriseHotelManagementGUI();
        });
    }
}