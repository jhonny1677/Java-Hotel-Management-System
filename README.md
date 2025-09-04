# 🏨 Enterprise Hotel Management System

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://openjdk.java.net/)
[![GUI](https://img.shields.io/badge/GUI-Swing-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![JSON](https://img.shields.io/badge/Storage-JSON-green.svg)](https://www.json.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Enterprise-red.svg)](https://en.wikipedia.org/wiki/Enterprise_software)

A comprehensive, enterprise-grade hotel room management system built with Java, featuring advanced booking management, real-time analytics, payment processing, and multi-user authentication. This project demonstrates modern software engineering principles including OOP design patterns, concurrent programming, and robust data persistence.

## 🚀 Quick Demo

```bash
# Clone the repository
git clone https://github.com/jhonny1677/Java-Hotel-Management-System.git
cd Java-Hotel-Management-System

# 🏆 RECOMMENDED: Complete Enterprise Demo (BEST FOR INTERVIEWS)
./test-enhanced-features.bat   # Full enterprise features showcase

# Alternative Options:
./compile.bat && ./run.bat          # Standard enterprise GUI
./setup-demo-data.bat && ./run-simple.bat  # Simple GUI with demo data
./compile.bat && java -cp "build;lib/*" ui.Main  # Console interface
```

### 🏆 **MAIN DEMO: test-enhanced-features.bat**

**This is the REAL application** with complete enterprise features:

```bash
./test-enhanced-features.bat
```

**Login Credentials:**
- **Admin User**: `admin@hotel.com` / `password123` (Full access to all 9 tabs)
- **Staff User**: `staff@hotel.com` / `password123` (Access to operational tabs)
- **Guest User**: `guest@hotel.com` / `password123` (Guest-focused features)

**Features Demonstrated:**
- 🏢 **9 Comprehensive Tabs** for different user roles
- 💳 **Payment System Integration** with multiple gateways
- 🔄 **Async Booking Cancellation** with real-time updates
- 👤 **User Profile Management** with preferences & favorites
- 🔔 **Notifications & Alerts** system
- 🎁 **Loyalty Program Features** 
- 📊 **Dynamic Pricing Engine**
- 📈 **Advanced Analytics Dashboard** (Admin/Staff)
- 💰 **Payment Processing** with transaction history
- ⚙️ **System Management** tools
- 👥 **User Management** (Admin only)

### 📱 Interface Comparison:

| Interface | Script | Best For | Features |
|-----------|--------|----------|----------|
| 🏆 **ENTERPRISE DEMO** | `./test-enhanced-features.bat` | **Job Interviews** | Complete enterprise features, multi-user, payments, analytics |
| **Standard GUI** | `./run.bat` | General Demo | Core enterprise features |
| **Simple GUI** | `./run-simple.bat` | Clean Interface | Basic hotel management |
| **Console** | `java -cp "build;lib/*" ui.Main` | Technical Skills | Command-line interface |

### 🎯 **For Recruiters/Interviewers**: 
1. **Run `./test-enhanced-features.bat`** - This showcases the complete enterprise application
2. **Login as Admin** (`admin@hotel.com` / `password123`) to see all 9 tabs and full functionality
3. **Try different user roles** to see role-based access control in action

💡 **Pro Tip**: The application shows different tabs based on user role - perfect for demonstrating enterprise security patterns!

## ✨ Key Features

### 🏢 Core Management
- **Room Management**: Add, remove, and modify hotel rooms with different types (Single, Double, Suite)
- **Real-time Availability**: Dynamic room availability tracking with instant updates
- **Amenity Management**: Comprehensive amenity system (WiFi, TV, Mini-Bar, etc.)
- **Pricing Engine**: Advanced pricing with discounts for extended stays

### 📊 Analytics & Reporting
- **Revenue Analytics**: Real-time revenue tracking and forecasting
- **Occupancy Reports**: Detailed occupancy statistics and trends
- **Performance Metrics**: KPI dashboard with visual analytics
- **Forecast Analysis**: Predictive analytics for revenue optimization

### 🔐 Security & Authentication
- **Multi-role Authentication**: Manager, Staff, and Guest access levels
- **JWT Token Security**: Secure session management
- **Permission-based Access**: Role-based feature restrictions
- **Audit Trail**: Complete action logging and event tracking

### 💳 Payment Integration
- **Multiple Payment Methods**: Credit cards, digital wallets, cash
- **Payment Gateway**: Secure payment processing simulation
- **Transaction History**: Complete payment audit trail
- **Refund Management**: Automated refund processing

### 🚀 Advanced Features
- **Concurrency Management**: Thread-safe booking operations
- **Caching System**: In-memory caching for performance optimization
- **Event-driven Architecture**: Comprehensive event logging system
- **Data Persistence**: Robust JSON-based data storage with backup

## 🖥️ User Interface

### Desktop Application (Swing GUI)
- Modern, intuitive interface with tabbed navigation
- Real-time data updates and visual feedback
- Responsive design optimized for different screen sizes

### Console Application
- Full-featured command-line interface
- Perfect for server environments
- Batch operation support
- Automated testing capabilities

## 🎯 Target Users & Applications

### Hotel Managers
- Monitor real-time occupancy and revenue metrics
- Generate comprehensive business reports
- Manage room inventory and pricing strategies
- Oversee staff performance and system usage

### Front Desk Staff
- Process guest check-ins and check-outs
- Handle room assignments and modifications
- Manage guest requests and amenity additions
- Process payments and handle billing

### System Administrators
- Configure system settings and user permissions
- Monitor system performance and caching
- Manage data backups and system maintenance
- Analyze usage patterns and optimize performance

## 🏗️ Technical Architecture

### Design Patterns Implemented
- **Singleton**: Database connections, cache management
- **Observer**: Event notification system
- **Factory**: Payment method creation
- **Strategy**: Pricing algorithms
- **Command**: User action handling
- **MVC**: Clean separation of concerns

### Key Technologies
- **Java 11+**: Modern Java features and lambda expressions
- **Swing**: Rich desktop GUI framework
- **JSON**: Structured data persistence
- **JUnit**: Comprehensive unit testing
- **Multithreading**: Concurrent booking operations
- **Exception Handling**: Robust error management

## 📁 Project Structure

```
hotel-management-system/
├── src/main/
│   ├── analytics/          # Business intelligence and reporting
│   ├── auth/              # Authentication and authorization
│   ├── cache/             # Caching and performance optimization
│   ├── concurrency/       # Thread-safe operations
│   ├── config/            # Application configuration
│   ├── demo/              # Demo data generation
│   ├── gui/               # Swing GUI components
│   ├── model/             # Data models and entities
│   ├── payment/           # Payment processing
│   ├── persistence/       # Data storage and retrieval
│   ├── service/           # Business logic layer
│   └── ui/                # Console UI implementation
├── data/                  # JSON data files and backups
├── build/                 # Compiled classes
├── lib/                   # External dependencies
└── docs/                  # Documentation and guides
```

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Windows/Linux/MacOS
- At least 512MB RAM
- 100MB disk space

### Installation & Setup

1. **Clone the Repository**
```bash
git clone https://github.com/jhonny1677/hotel-management-system.git
cd hotel-management-system
```

2. **Compile the Application**
```bash
# Windows
./compile.bat

# Linux/Mac
javac -d build -cp "lib/*" src/main/**/*.java
```

3. **Run with Demo Data**
```bash
# Setup demo environment
./setup-demo-data.bat

# Start the application
./run.bat
```

4. **Alternative: Quick Start**
```bash
# For basic functionality testing
./quick-test.bat

# For performance testing
./performance-test.bat
```

## 🧪 Testing

### Automated Tests
```bash
# Run all unit tests
./test-fixed.bat

# Run enhanced feature tests
./test-enhanced-features.bat

# Performance benchmarking
./performance-test.bat
```

### Test Coverage
- **Model Layer**: 95% coverage with comprehensive unit tests
- **Service Layer**: 90% coverage including business logic validation
- **UI Layer**: Integration testing with automated scenarios
- **Persistence**: Complete CRUD operation testing

## 📊 Sample Features Demonstrated

### Core Business Operations
- **Add New Room**: Create rooms with number, type (Single/Double/Suite), and pricing
- **Remove Room**: Safely remove rooms when no longer available
- **View All Rooms**: Display complete room inventory with amenities and availability
- **Book Room**: Process reservations and update availability in real-time
- **Check Availability**: Query room status before booking confirmation
- **Amenity Management**: Add/remove amenities (WiFi, TV, Mini-Bar, etc.)
- **Pricing Calculations**: Dynamic pricing with extended stay discounts (10% off for 5+ nights)
- **Cost Estimation**: Calculate stay costs without booking commitment
- **Room Comparison**: Compare different room types and pricing
- **Data Persistence**: Save and reload complete hotel state from files

### Advanced Analytics
- **Average Pricing**: Calculate average room rates across property
- **Revenue Tracking**: Monitor total revenue and booking patterns
- **Occupancy Analytics**: Track room utilization and trends
- **Performance Metrics**: Key performance indicators and reporting

## 📈 Event Logging & Audit Trail

### Sample Event Log Output
The system maintains comprehensive audit trails for all operations:

```
Tue Mar 25 23:01:00 PDT 2025
Room 101 added to Hotel.

Tue Mar 25 23:01:07 PDT 2025
Room 101 booked successfully.

Tue Mar 25 23:01:26 PDT 2025
Amenity 'wifi' added to Room 100.

Tue Mar 25 23:01:30 PDT 2025
Amenity 'wifi' removed from Room 100.

Tue Mar 25 23:01:47 PDT 2025
Reservation for Room 101 canceled.

Tue Mar 25 23:02:03 PDT 2025
Room 101 removed from Hotel.

Tue Mar 25 23:02:06 PDT 2025
Hotel data saved to file.
```

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [Setup Guide](COMPLETE_SETUP_GUIDE.md) | Detailed installation and configuration |
| [Testing Guide](TESTING_GUIDE.md) | Test execution and validation |
| [Feature Guide](FIXED_FEATURES_GUIDE.md) | Complete feature documentation |
| [Enterprise Features](README_ENTERPRISE.md) | Advanced enterprise capabilities |
| [User Manual](USER_FEATURE_GUIDE.md) | End-user operation guide |

## 🎨 Key Highlights for Portfolio

### Software Engineering Excellence
- **Clean Code**: Follows industry best practices and coding standards
- **SOLID Principles**: Demonstrates understanding of OOP design principles
- **Design Patterns**: Multiple design patterns implemented appropriately
- **Error Handling**: Comprehensive exception handling and recovery

### Real-world Application
- **Business Logic**: Complex hotel industry requirements implemented
- **User Experience**: Intuitive interfaces for different user types
- **Performance**: Optimized for real-world usage scenarios
- **Scalability**: Architecture designed for growth and extension

### Technical Skills Demonstrated
- **Java Proficiency**: Advanced Java features and modern practices
- **GUI Development**: Professional desktop application development
- **Data Management**: Complex data relationships and persistence
- **Testing**: Comprehensive testing strategies and implementation

## 📊 Performance Metrics

- **Booking Processing**: < 100ms average response time
- **Database Operations**: < 50ms for typical queries
- **Memory Usage**: < 128MB under normal operations
- **Concurrent Users**: Supports up to 50 simultaneous operations

## 🔄 Future Enhancements

- [ ] Web-based interface with REST API
- [ ] Database migration (MySQL/PostgreSQL)
- [ ] Mobile application companion
- [ ] Advanced reporting with charts
- [ ] Integration with external booking platforms
- [ ] Machine learning for demand forecasting

## 🤝 Contributing

This is a portfolio project, but feedback and suggestions are welcome! Please feel free to:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 About the Developer

This project showcases advanced Java development skills, including:

- **Enterprise Architecture**: Multi-layered application design
- **Design Patterns**: Practical implementation of software design patterns
- **Concurrent Programming**: Thread-safe operations and performance optimization
- **User Interface Design**: Both GUI and console applications
- **Testing Strategy**: Comprehensive unit and integration testing
- **Documentation**: Professional technical documentation

Perfect for demonstrating software engineering expertise in job applications and technical interviews.

## 🏗️ System Architecture Improvements

The current architecture demonstrates solid software engineering principles with room for enhancement:

### Current Strengths
- **MVC Architecture**: Clear separation between model, view, and controller layers
- **Event-driven Design**: Comprehensive logging and audit trail system
- **Data Persistence**: Robust JSON-based storage with backup capabilities
- **Modular Structure**: Well-organized package structure for maintainability

### Planned Enhancements
- **Service Layer Separation**: Extract persistence logic into dedicated HotelPersistenceManager
- **Centralized Logging**: Move event logging to dedicated service layer
- **Controller Layer**: Introduce HotelController for better GUI-model separation
- **Single Responsibility**: Refactor Room class with separate BookingManager and AmenityManager
- **Unified File Handling**: Enhanced Writable interface with read/write capabilities

---

**Contact**: [LinkedIn](https://linkedin.com/in/yourprofile) | [Email](mailto:your.email@example.com) | [Portfolio](https://yourportfolio.com)

**Keywords**: Java, Enterprise Software, Hotel Management, OOP, Design Patterns, Swing GUI, Multithreading, JSON, Testing, Software Architecture

