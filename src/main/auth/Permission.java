package auth;

public enum Permission {
    // Room permissions
    VIEW_ROOMS,
    MANAGE_ROOMS,
    DELETE_ROOMS,
    
    // Booking permissions
    CREATE_BOOKING,
    VIEW_OWN_BOOKINGS,
    VIEW_ALL_BOOKINGS,
    CANCEL_OWN_BOOKING,
    CANCEL_ANY_BOOKING,
    
    // Payment permissions
    VIEW_PAYMENTS,
    PROCESS_REFUNDS,
    VIEW_REVENUE_REPORTS,
    
    // User management permissions
    CREATE_USERS,
    VIEW_USERS,
    MANAGE_USERS,
    DELETE_USERS,
    
    // System permissions
    VIEW_SYSTEM_LOGS,
    MANAGE_SYSTEM_SETTINGS,
    BACKUP_DATA,
    
    // Notification permissions
    SEND_NOTIFICATIONS,
    VIEW_ALL_NOTIFICATIONS
}