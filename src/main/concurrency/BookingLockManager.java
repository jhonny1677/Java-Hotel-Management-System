package concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.util.Map;

public class BookingLockManager {
    private final ConcurrentHashMap<Integer, RoomLock> roomLocks;
    private final long lockTimeoutMs;

    public BookingLockManager() {
        this(30000); // Default 30 second timeout
    }

    public BookingLockManager(long lockTimeoutMs) {
        this.roomLocks = new ConcurrentHashMap<>();
        this.lockTimeoutMs = lockTimeoutMs;
    }

    public boolean acquireRoomLock(int roomNumber, String operationType) {
        RoomLock roomLock = roomLocks.computeIfAbsent(roomNumber, k -> new RoomLock());
        
        try {
            boolean acquired = roomLock.getLock().tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS);
            if (acquired) {
                roomLock.setLastOperation(operationType);
                roomLock.setLockTime(LocalDateTime.now());
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseRoomLock(int roomNumber) {
        RoomLock roomLock = roomLocks.get(roomNumber);
        if (roomLock != null && roomLock.getLock().isHeldByCurrentThread()) {
            roomLock.getLock().unlock();
        }
    }

    public boolean isRoomLocked(int roomNumber) {
        RoomLock roomLock = roomLocks.get(roomNumber);
        return roomLock != null && roomLock.getLock().isLocked();
    }

    public String getRoomLockInfo(int roomNumber) {
        RoomLock roomLock = roomLocks.get(roomNumber);
        if (roomLock == null) {
            return "Room " + roomNumber + " is not locked";
        }
        
        return String.format("Room %d - Locked: %s, Operation: %s, Lock Time: %s", 
                           roomNumber, 
                           roomLock.getLock().isLocked(),
                           roomLock.getLastOperation(),
                           roomLock.getLockTime());
    }

    public Map<Integer, String> getAllLockedRooms() {
        Map<Integer, String> lockedRooms = new ConcurrentHashMap<>();
        for (Map.Entry<Integer, RoomLock> entry : roomLocks.entrySet()) {
            if (entry.getValue().getLock().isLocked()) {
                lockedRooms.put(entry.getKey(), entry.getValue().getLastOperation());
            }
        }
        return lockedRooms;
    }

    public void cleanupExpiredLocks() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(5);
        roomLocks.entrySet().removeIf(entry -> {
            RoomLock roomLock = entry.getValue();
            return !roomLock.getLock().isLocked() && 
                   roomLock.getLockTime() != null && 
                   roomLock.getLockTime().isBefore(expireTime);
        });
    }

    private static class RoomLock {
        private final ReentrantLock lock;
        private String lastOperation;
        private LocalDateTime lockTime;

        public RoomLock() {
            this.lock = new ReentrantLock();
        }

        public ReentrantLock getLock() { return lock; }
        public String getLastOperation() { return lastOperation; }
        public void setLastOperation(String lastOperation) { this.lastOperation = lastOperation; }
        public LocalDateTime getLockTime() { return lockTime; }
        public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
    }
}