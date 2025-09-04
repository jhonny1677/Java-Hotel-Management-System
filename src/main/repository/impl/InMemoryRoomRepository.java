package repository.impl;

import model.Room;
import repository.RoomRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryRoomRepository implements RoomRepository {
    private final Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private final Map<Integer, Room> roomsByNumber = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Room save(Room room) {
        if (room instanceof model.Room) {
            model.Room r = (model.Room) room;
            // Create a unique ID if not set
            Long id = idGenerator.getAndIncrement();
            
            // Store in both maps
            rooms.put(id, room);
            roomsByNumber.put(r.getRoomNumber(), room);
            
            // Room saved successfully
        }
        return room;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    @Override
    public boolean deleteById(Long id) {
        Room room = rooms.remove(id);
        if (room != null && room instanceof model.Room) {
            roomsByNumber.remove(((model.Room) room).getRoomNumber());
            return true;
        }
        return false;
    }

    @Override
    public void delete(Room room) {
        if (room instanceof model.Room) {
            model.Room r = (model.Room) room;
            // Find and remove by room number
            rooms.entrySet().removeIf(entry -> 
                entry.getValue() instanceof model.Room && 
                ((model.Room) entry.getValue()).getRoomNumber() == r.getRoomNumber());
            roomsByNumber.remove(r.getRoomNumber());
        }
    }

    @Override
    public boolean exists(Long id) {
        return rooms.containsKey(id);
    }

    @Override
    public long count() {
        return rooms.size();
    }

    @Override
    public Optional<Room> findByRoomNumber(int roomNumber) {
        return Optional.ofNullable(roomsByNumber.get(roomNumber));
    }

    @Override
    public List<Room> findByRoomType(String roomType) {
        return rooms.values().stream()
                .filter(room -> room instanceof model.Room)
                .filter(room -> ((model.Room) room).getRoomType().equalsIgnoreCase(roomType))
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findAvailableRooms() {
        return rooms.values().stream()
                .filter(room -> room instanceof model.Room)
                .filter(room -> ((model.Room) room).isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findAvailableRoomsByType(String roomType) {
        return rooms.values().stream()
                .filter(room -> room instanceof model.Room)
                .filter(room -> ((model.Room) room).isAvailable())
                .filter(room -> ((model.Room) room).getRoomType().equalsIgnoreCase(roomType))
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findByPriceRange(double minPrice, double maxPrice) {
        return rooms.values().stream()
                .filter(room -> room instanceof model.Room)
                .filter(room -> {
                    double price = ((model.Room) room).getPrice();
                    return price >= minPrice && price <= maxPrice;
                })
                .collect(Collectors.toList());
    }
}