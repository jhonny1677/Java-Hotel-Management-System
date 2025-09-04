# 🎯 **WHERE TO FIND THE NEW USER FEATURES**

## 🚀 **Step 1: Start the Application**
```bash
cd "C:\Users\ankit\Downloads\project-h5d8p-main\project-h5d8p-main"
run.bat
```

## 🔐 **Step 2: Login as Guest User**
- **Email**: `guest@hotel.com`
- **Password**: `password123`

## 📱 **Step 3: Navigate to the Right Tabs**

### 🏨 **BOOKINGS TAB** - Booking Cancellation Features
**Location**: First tab after login

**What you'll see:**
- ✅ **Book Room (Async)** button
- ✅ **Cancel Booking** button ← **THIS IS THE CANCELLATION FEATURE**
- ✅ **View My Bookings** button
- ✅ **Check In** and **Check Out** buttons

**How to test cancellation:**
1. First book a room using room number (try: 101, 150, 205)
2. Note the booking ID that appears in the system output
3. Click **"Cancel Booking"**
4. Enter the booking ID
5. ✅ **You should see "Booking cancelled! Refund will be processed..."**

---

### 👤 **USER PROFILE TAB** - Payment Features
**Location**: Third tab (between Rooms and Notifications)

**What you'll see:**
- ✅ **User Information** section (name, email, role, phone)
- ✅ **Payment Methods** section ← **THIS IS THE PAYMENT FEATURE**
  - **Add Credit Card** button
  - **Add PayPal Account** button  
  - **Make Payment** button ← **DIRECT PAYMENT PROCESSING**
  - **Remove Payment Method** button
- ✅ **Room Preferences & Favorites** section
- ✅ **Recent Bookings** preview

**How to test payments:**
1. Click **"Add Credit Card"**
2. Enter any 16-digit number (e.g., 1234567890123456)
3. Enter cardholder name
4. Click **"Make Payment"**
5. Enter amount (e.g., 100.50)
6. Select payment method
7. ✅ **You should see "Payment successful!" with transaction ID**

---

### 🔔 **NOTIFICATIONS TAB** - Loyalty & Alerts
**Location**: Fourth tab

**What you'll see:**
- ✅ **Notifications & Alerts** section
- ✅ **Loyalty Program Status** (Gold Member - 750 points)
- ✅ **System alerts and promotions**

---

## 🛠️ **If Features Are Missing:**

### **Check Tab Order:**
After login, you should see these tabs for GUEST users:
1. **Bookings** ← Cancellation here
2. **Rooms** 
3. **User Profile** ← Payment features here
4. **Notifications** ← Loyalty program here
5. **Dynamic Pricing**

### **If You Don't See User Profile Tab:**
```bash
# Recompile to ensure latest version
cd "C:\Users\ankit\Downloads\project-h5d8p-main\project-h5d8p-main"
javac -encoding UTF-8 -cp "lib\json-20240303.jar;build\classes" -d build\classes "src\main\ui\EnterpriseHotelManagementGUI.java"
run.bat
```

## 🎯 **Quick Feature Test:**

1. **Login** as guest@hotel.com
2. **Bookings tab** → Book room 101 → Cancel it
3. **User Profile tab** → Add credit card → Make $50 payment  
4. **User Profile tab** → Set preferences → Add room 205 to favorites
5. **Notifications tab** → View loyalty status

## 📞 **Troubleshooting:**

**Q: I don't see the User Profile tab**
- A: Make sure you compiled the latest version and restart the app

**Q: Cancel Booking doesn't work** 
- A: You need to create a booking first, then use the booking ID shown in system output

**Q: Payment features not visible**
- A: They're in the **User Profile tab**, not the Bookings tab

**Q: No notifications showing**
- A: Click "Refresh Notifications" button in the Notifications tab

---

**✅ All features are implemented and working! You just need to know where to look for them.**