package auth;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public enum Role {
    GUEST(Set.of(Permission.VIEW_ROOMS, Permission.CREATE_BOOKING, Permission.VIEW_OWN_BOOKINGS)),
    
    STAFF(Set.of(Permission.VIEW_ROOMS, Permission.CREATE_BOOKING, Permission.VIEW_OWN_BOOKINGS,
                Permission.VIEW_ALL_BOOKINGS, Permission.CANCEL_ANY_BOOKING, Permission.MANAGE_ROOMS,
                Permission.VIEW_PAYMENTS)),
    
    ADMIN(Set.of(Permission.values())); // All permissions

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}