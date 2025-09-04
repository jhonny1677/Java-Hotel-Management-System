package repository;

import model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends Repository<Room, Long> {
    Optional<Room> findByRoomNumber(int roomNumber);
    List<Room> findByRoomType(String roomType);
    List<Room> findAvailableRooms();
    List<Room> findAvailableRoomsByType(String roomType);
    List<Room> findByPriceRange(double minPrice, double maxPrice);
}