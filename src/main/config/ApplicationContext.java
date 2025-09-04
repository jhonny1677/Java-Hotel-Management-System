package config;

import auth.*;
import cache.*;
import services.*;
import payment.*;
import pricing.*;
import analytics.*;
import concurrency.*;
import patterns.command.CommandInvoker;
import patterns.observer.RoomAvailabilityObserver;
import repository.*;
import repository.impl.*;
import model.*;
import persistence.*;
import org.json.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Optional;

public class ApplicationContext {
    // Repositories
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    
    // Core Services
    private final CacheService cacheService;
    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final UserService userService;
    
    // Payment System
    private final PaymentGatewayManager paymentGatewayManager;
    private final PaymentService paymentService;
    
    // Booking System
    private final CommandInvoker commandInvoker;
    private final ConcurrentBookingService concurrentBookingService;
    private final BookingService bookingService;
    
    // Caching
    private final CacheableRoomService cacheableRoomService;
    
    // Pricing
    private final DynamicPricingEngine pricingEngine;
    
    // Analytics
    private final RevenueAnalytics revenueAnalytics;
    private final AnalyticsDashboard analyticsDashboard;

    public ApplicationContext() {
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.bookingRepository = new InMemoryBookingRepository();
        this.roomRepository = new InMemoryRoomRepository();
        this.paymentRepository = new InMemoryPaymentRepository();
        this.notificationRepository = new InMemoryNotificationRepository();
        
        // Initialize cache
        this.cacheService = new InMemoryCacheService();
        this.cacheableRoomService = new CacheableRoomService(roomRepository, cacheService);
        
        // Initialize authentication
        this.jwtService = new JWTService();
        this.authenticationService = new AuthenticationService(userRepository, jwtService);
        
        // Initialize notification service
        this.notificationService = new NotificationService(notificationRepository, userRepository);
        
        // Initialize user service
        this.userService = new UserService(userRepository, authenticationService);
        
        // Initialize payment system
        this.paymentGatewayManager = new PaymentGatewayManager();
        setupPaymentGateways();
        this.paymentService = new PaymentService(paymentRepository);
        
        // Initialize booking system
        this.commandInvoker = new CommandInvoker();
        this.concurrentBookingService = new ConcurrentBookingService(
            bookingRepository, roomRepository, notificationService, paymentService);
        this.bookingService = new BookingService(
            bookingRepository, roomRepository, notificationService, paymentService, commandInvoker);
        
        // Setup observers
        setupObservers();
        
        // Initialize pricing
        this.pricingEngine = new DynamicPricingEngine(bookingRepository);
        
        // Initialize analytics
        this.revenueAnalytics = new RevenueAnalytics(bookingRepository, paymentRepository, roomRepository);
        this.analyticsDashboard = new AnalyticsDashboard(bookingRepository, paymentRepository, roomRepository);
        
        // Initialize rooms FIRST (before demo data)
        initializeHotelRooms();
        
        // Load existing data or initialize demo data
        loadDataFromFile();
        
        // Initialize demo data if needed
        if (userRepository.findAll().isEmpty()) {
            initializeDemoData();
        }
    }
    
    private void setupPaymentGateways() {
        // Register Stripe
        StripePaymentGateway stripe = new StripePaymentGateway("test_key", "webhook_secret", true);
        paymentGatewayManager.registerGateway("Stripe", stripe);
        
        // Register PayPal
        PayPalPaymentGateway paypal = new PayPalPaymentGateway("client_id", "client_secret", true);
        paymentGatewayManager.registerGateway("PayPal", paypal);
    }
    
    private void setupObservers() {
        RoomAvailabilityObserver availabilityObserver = new RoomAvailabilityObserver(notificationService);
        bookingService.addObserver(availabilityObserver);
    }
    
    private void initializeDemoData() {
        try {
            // Create demo users
            createDemoUser("admin@hotel.com", "Admin User", Role.ADMIN, "password123");
            createDemoUser("staff@hotel.com", "Staff Member", Role.STAFF, "password123");
            createDemoUser("guest@hotel.com", "Guest User", Role.GUEST, "password123");
            createDemoUser("john.doe@email.com", "John Doe", Role.GUEST, "password123");
            createDemoUser("jane.smith@email.com", "Jane Smith", Role.GUEST, "password123");
            
            System.out.println("✓ Demo users created successfully!");
            System.out.println("Login credentials:");
            System.out.println("- Admin: admin@hotel.com / password123");
            System.out.println("- Staff: staff@hotel.com / password123");
            System.out.println("- Guest: guest@hotel.com / password123");
            
        } catch (Exception e) {
            System.err.println("Error creating demo data: " + e.getMessage());
        }
    }
    
    private void createDemoUser(String email, String name, Role role, String password) {
        if (!userRepository.existsByEmail(email)) {
            String hashedPassword = authenticationService.hashPassword(password);
            User user = new User(name, email, hashedPassword, role);
            user.setPhoneNumber("+1-555-" + String.format("%04d", (int)(Math.random() * 10000)));
            userRepository.save(user);
        }
    }
    
    private void initializeHotelRooms() {
        // Check if rooms already exist
        if (roomRepository.count() > 0) {
            System.out.println("Rooms already initialized: " + roomRepository.count() + " rooms found");
            return;
        }
        
        System.out.println("Initializing 100 hotel rooms...");
        
        // Create 100 rooms like the original Hotel class
        for (int i = 1; i <= 100; i++) {
            String roomType;
            double price;
            
            // Assign types based on position (same logic as original)
            if (i % 3 == 1) {
                roomType = "Single";
                price = 100.0;
            } else if (i % 3 == 2) {
                roomType = "Double";
                price = 150.0;
            } else {
                roomType = "Suite";
                price = 300.0;
            }
            
            // Create room using the original Room constructor
            model.Room room = new model.Room(i, roomType, price);
            
            // Add some default amenities based on room type
            if (roomType.equals("Single")) {
                room.addAmenity("WiFi");
                room.addAmenity("TV");
            } else if (roomType.equals("Double")) {
                room.addAmenity("WiFi");
                room.addAmenity("TV");
                room.addAmenity("Mini Fridge");
            } else { // Suite
                room.addAmenity("WiFi");
                room.addAmenity("TV");
                room.addAmenity("Mini Fridge");
                room.addAmenity("Balcony");
                room.addAmenity("Room Service");
            }
            
            // Set additional properties
            room.setMaxOccupancy(roomType.equals("Suite") ? 4 : (roomType.equals("Double") ? 2 : 1));
            room.setSize(roomType.equals("Suite") ? 600 : (roomType.equals("Double") ? 350 : 250));
            room.setDescription("Standard " + roomType.toLowerCase() + " room with modern amenities");
            
            roomRepository.save(room);
        }
        
        System.out.println("✓ Successfully created " + roomRepository.count() + " hotel rooms");
    }

    // Getters for all services
    public UserRepository getUserRepository() { return userRepository; }
    public BookingRepository getBookingRepository() { return bookingRepository; }
    public RoomRepository getRoomRepository() { return roomRepository; }
    public PaymentRepository getPaymentRepository() { return paymentRepository; }
    public NotificationRepository getNotificationRepository() { return notificationRepository; }
    
    public CacheService getCacheService() { return cacheService; }
    public JWTService getJwtService() { return jwtService; }
    public AuthenticationService getAuthenticationService() { return authenticationService; }
    public NotificationService getNotificationService() { return notificationService; }
    public UserService getUserService() { return userService; }
    
    public PaymentGatewayManager getPaymentGatewayManager() { return paymentGatewayManager; }
    public PaymentService getPaymentService() { return paymentService; }
    
    public CommandInvoker getCommandInvoker() { return commandInvoker; }
    public ConcurrentBookingService getConcurrentBookingService() { return concurrentBookingService; }
    public BookingService getBookingService() { return bookingService; }
    
    public CacheableRoomService getCacheableRoomService() { return cacheableRoomService; }
    public DynamicPricingEngine getPricingEngine() { return pricingEngine; }
    public RevenueAnalytics getRevenueAnalytics() { return revenueAnalytics; }
    public AnalyticsDashboard getAnalyticsDashboard() { return analyticsDashboard; }
    
    public void shutdown() {
        // Save data before shutdown
        saveDataToFile();
        
        if (cacheService instanceof InMemoryCacheService) {
            ((InMemoryCacheService) cacheService).shutdown();
        }
        if (concurrentBookingService != null) {
            concurrentBookingService.shutdown();
        }
    }
    
    private void saveDataToFile() {
        try {
            System.out.println("💾 Saving application data...");
            
            JSONObject appData = new JSONObject();
            
            // Save bookings
            JSONArray bookingsArray = new JSONArray();
            for (Booking booking : bookingRepository.findAll()) {
                bookingsArray.put(booking.toJson());
            }
            appData.put("bookings", bookingsArray);
            
            // Save payments
            JSONArray paymentsArray = new JSONArray();
            for (Payment payment : paymentRepository.findAll()) {
                paymentsArray.put(payment.toJson());
            }
            appData.put("payments", paymentsArray);
            
            // Save rooms (with current availability status)
            JSONArray roomsArray = new JSONArray();
            for (model.Room room : roomRepository.findAll()) {
                roomsArray.put(room.toJson());
            }
            appData.put("rooms", roomsArray);
            
            // Save users
            JSONArray usersArray = new JSONArray();
            for (User user : userRepository.findAll()) {
                usersArray.put(user.toJson());
            }
            appData.put("users", usersArray);
            
            // Write to file
            try (FileWriter file = new FileWriter("data/applicationData.json")) {
                file.write(appData.toString(4));
            }
            
            System.out.println("✓ Application data saved successfully!");
            
        } catch (Exception e) {
            System.err.println("Error saving application data: " + e.getMessage());
        }
    }
    
    private void loadDataFromFile() {
        try {
            File dataFile = new File("data/applicationData.json");
            if (!dataFile.exists()) {
                System.out.println("📁 No existing data file found, will initialize with demo data");
                return;
            }
            
            System.out.println("📂 Loading application data...");
            
            String content = new String(java.nio.file.Files.readAllBytes(dataFile.toPath()));
            JSONObject appData = new JSONObject(content);
            
            // Load users first (needed for other data)
            if (appData.has("users")) {
                JSONArray usersArray = appData.getJSONArray("users");
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = usersArray.getJSONObject(i);
                    User user = new User();
                    // Set basic fields
                    user.setName(userJson.getString("name"));
                    user.setEmail(userJson.getString("email"));
                    user.setPasswordHash(userJson.getString("passwordHash"));
                    user.setRole(Role.valueOf(userJson.getString("role")));
                    if (userJson.has("phoneNumber")) {
                        user.setPhoneNumber(userJson.getString("phoneNumber"));
                    }
                    userRepository.save(user);
                }
            }
            
            // Load rooms and update availability
            if (appData.has("rooms")) {
                JSONArray roomsArray = appData.getJSONArray("rooms");
                for (int i = 0; i < roomsArray.length(); i++) {
                    JSONObject roomJson = roomsArray.getJSONObject(i);
                    int roomNumber = roomJson.getInt("roomNumber");
                    
                    Optional<model.Room> existingRoom = roomRepository.findByRoomNumber(roomNumber);
                    if (existingRoom.isPresent()) {
                        // Update availability status
                        existingRoom.get().setAvailable(roomJson.getBoolean("isAvailable"));
                    }
                }
            }
            
            // Load bookings
            if (appData.has("bookings")) {
                JSONArray bookingsArray = appData.getJSONArray("bookings");
                for (int i = 0; i < bookingsArray.length(); i++) {
                    JSONObject bookingJson = bookingsArray.getJSONObject(i);
                    Booking booking = new Booking();
                    
                    booking.setUserId(bookingJson.getLong("userId"));
                    booking.setRoomNumber(bookingJson.getInt("roomNumber"));
                    booking.setCheckInDate(LocalDate.parse(bookingJson.getString("checkInDate")));
                    booking.setCheckOutDate(LocalDate.parse(bookingJson.getString("checkOutDate")));
                    booking.setTotalPrice(bookingJson.getDouble("totalPrice"));
                    booking.setBookingStatus(BookingStatus.valueOf(bookingJson.getString("bookingStatus")));
                    booking.setPaymentStatus(PaymentStatus.valueOf(bookingJson.getString("paymentStatus")));
                    
                    if (bookingJson.has("paymentId") && !bookingJson.isNull("paymentId")) {
                        booking.setPaymentId(bookingJson.getString("paymentId"));
                    }
                    
                    bookingRepository.save(booking);
                }
            }
            
            // Load payments
            if (appData.has("payments")) {
                JSONArray paymentsArray = appData.getJSONArray("payments");
                for (int i = 0; i < paymentsArray.length(); i++) {
                    JSONObject paymentJson = paymentsArray.getJSONObject(i);
                    Payment payment = new Payment();
                    
                    payment.setUserId(paymentJson.getLong("userId"));
                    payment.setAmount(paymentJson.getDouble("amount"));
                    payment.setDescription(paymentJson.getString("description"));
                    payment.setStatus(PaymentStatus.valueOf(paymentJson.getString("status")));
                    if (paymentJson.has("paymentId") && !paymentJson.isNull("paymentId")) {
                        payment.setPaymentId(paymentJson.getString("paymentId"));
                    }
                    
                    paymentRepository.save(payment);
                }
            }
            
            System.out.println("✓ Application data loaded successfully!");
            
        } catch (Exception e) {
            System.err.println("Error loading application data: " + e.getMessage());
            System.out.println("Will continue with initialization...");
        }
    }
}