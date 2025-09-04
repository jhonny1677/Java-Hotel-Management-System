package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONObject;
import persistence.Writable;

public class Booking implements Writable {
    private Long id;
    private Long userId;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String specialRequests;

    public Booking() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bookingStatus = BookingStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Booking(Long userId, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double totalPrice) {
        this();
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { 
        this.userId = userId; 
        this.updatedAt = LocalDateTime.now();
    }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { 
        this.roomNumber = roomNumber; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { 
        this.checkInDate = checkInDate; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { 
        this.checkOutDate = checkOutDate; 
        this.updatedAt = LocalDateTime.now();
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { 
        this.totalPrice = totalPrice; 
        this.updatedAt = LocalDateTime.now();
    }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { 
        this.bookingStatus = bookingStatus; 
        this.updatedAt = LocalDateTime.now();
    }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        this.paymentStatus = paymentStatus; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { 
        this.paymentId = paymentId; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { 
        this.specialRequests = specialRequests; 
        this.updatedAt = LocalDateTime.now();
    }

    public long getNumberOfNights() {
        return checkOutDate.toEpochDay() - checkInDate.toEpochDay();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("userId", userId);
        json.put("roomNumber", roomNumber);
        json.put("checkInDate", checkInDate != null ? checkInDate.toString() : null);
        json.put("checkOutDate", checkOutDate != null ? checkOutDate.toString() : null);
        json.put("totalPrice", totalPrice);
        json.put("bookingStatus", bookingStatus != null ? bookingStatus.name() : null);
        json.put("paymentStatus", paymentStatus != null ? paymentStatus.name() : null);
        json.put("paymentId", paymentId);
        json.put("createdAt", createdAt != null ? createdAt.toString() : null);
        json.put("updatedAt", updatedAt != null ? updatedAt.toString() : null);
        json.put("specialRequests", specialRequests);
        return json;
    }

    @Override
    public String toString() {
        return String.format("Booking{id=%d, userId=%d, roomNumber=%d, checkIn=%s, checkOut=%s, status=%s}", 
                           id, userId, roomNumber, checkInDate, checkOutDate, bookingStatus);
    }
}