package cache;

import model.Room;
import repository.RoomRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CacheableRoomService {
    private final RoomRepository roomRepository;
    private final CacheService cacheService;
    
    private static final String ROOM_KEY_PREFIX = "room:";
    private static final String AVAILABLE_ROOMS_KEY = "available_rooms";
    private static final String ROOMS_BY_TYPE_PREFIX = "rooms_by_type:";
    private static final long CACHE_TTL_MINUTES = 15;

    public CacheableRoomService(RoomRepository roomRepository, CacheService cacheService) {
        this.roomRepository = roomRepository;
        this.cacheService = cacheService;
    }

    public Optional<Room> findByRoomNumber(int roomNumber) {
        String cacheKey = ROOM_KEY_PREFIX + roomNumber;
        
        // Try cache first
        Optional<Room> cachedRoom = cacheService.get(cacheKey, Room.class);
        if (cachedRoom.isPresent()) {
            return cachedRoom;
        }
        
        // If not in cache, get from repository
        Optional<Room> room = roomRepository.findByRoomNumber(roomNumber);
        if (room.isPresent()) {
            cacheService.put(cacheKey, room.get(), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        
        return room;
    }

    @SuppressWarnings("unchecked")
    public List<Room> findAvailableRooms() {
        // Try cache first
        @SuppressWarnings("unchecked")
        Optional<Object> cachedObj = cacheService.get(AVAILABLE_ROOMS_KEY);
        if (cachedObj.isPresent() && cachedObj.get() instanceof List) {
            return (List<Room>) cachedObj.get();
        }
        
        // If not in cache, get from repository
        List<Room> availableRooms = roomRepository.findAvailableRooms();
        cacheService.put(AVAILABLE_ROOMS_KEY, availableRooms, 5, TimeUnit.MINUTES); // Shorter TTL for availability
        
        return availableRooms;
    }

    @SuppressWarnings("unchecked")
    public List<Room> findByRoomType(String roomType) {
        String cacheKey = ROOMS_BY_TYPE_PREFIX + roomType.toLowerCase();
        
        // Try cache first
        @SuppressWarnings("unchecked")
        Optional<Object> cachedRoomsObj = cacheService.get(cacheKey);
        if (cachedRoomsObj.isPresent() && cachedRoomsObj.get() instanceof List) {
            return (List<Room>) cachedRoomsObj.get();
        }
        
        // If not in cache, get from repository
        List<Room> rooms = roomRepository.findByRoomType(roomType);
        cacheService.put(cacheKey, rooms, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        
        return rooms;
    }

    public Room save(Room room) {
        Room savedRoom = roomRepository.save(room);
        
        // Update cache
        String roomKey = ROOM_KEY_PREFIX + room.getRoomNumber();
        cacheService.put(roomKey, savedRoom, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        
        // Invalidate related caches
        invalidateRelatedCaches(room);
        
        return savedRoom;
    }

    public boolean deleteById(Long id) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        boolean deleted = roomRepository.deleteById(id);
        
        if (deleted && roomOpt.isPresent()) {
            Room room = roomOpt.get();
            // Remove from cache
            String roomKey = ROOM_KEY_PREFIX + room.getRoomNumber();
            cacheService.delete(roomKey);
            
            // Invalidate related caches
            invalidateRelatedCaches(room);
        }
        
        return deleted;
    }

    private void invalidateRelatedCaches(Room room) {
        // Invalidate available rooms cache
        cacheService.delete(AVAILABLE_ROOMS_KEY);
        
        // Invalidate room type cache
        String roomTypeKey = ROOMS_BY_TYPE_PREFIX + room.getRoomType().toLowerCase();
        cacheService.delete(roomTypeKey);
    }

    public void invalidateAllRoomCaches() {
        // Get all keys and remove room-related ones
        cacheService.delete(AVAILABLE_ROOMS_KEY);
        
        // In a real implementation, we'd iterate through all keys
        // For simplicity, we'll clear common room type caches
        String[] commonTypes = {"single", "double", "suite", "deluxe", "presidential"};
        for (String type : commonTypes) {
            cacheService.delete(ROOMS_BY_TYPE_PREFIX + type);
        }
    }

    public CacheStats getCacheStats() {
        return new CacheStats(
            cacheService.size(),
            cacheService.exists(AVAILABLE_ROOMS_KEY),
            getTypeCacheStatus()
        );
    }

    private int getTypeCacheStatus() {
        String[] commonTypes = {"single", "double", "suite", "deluxe", "presidential"};
        int cachedTypes = 0;
        for (String type : commonTypes) {
            if (cacheService.exists(ROOMS_BY_TYPE_PREFIX + type)) {
                cachedTypes++;
            }
        }
        return cachedTypes;
    }

    public static class CacheStats {
        private final long totalCacheSize;
        private final boolean availableRoomsCached;
        private final int roomTypesCached;

        public CacheStats(long totalCacheSize, boolean availableRoomsCached, int roomTypesCached) {
            this.totalCacheSize = totalCacheSize;
            this.availableRoomsCached = availableRoomsCached;
            this.roomTypesCached = roomTypesCached;
        }

        public long getTotalCacheSize() { return totalCacheSize; }
        public boolean isAvailableRoomsCached() { return availableRoomsCached; }
        public int getRoomTypesCached() { return roomTypesCached; }
    }
}