package jdbc.dao;

import entity.HostelOrder;
import entity.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class RoomDAO {

    private final EntityManager entityManager;

    public Room createRoom(Room room) {
        log.debug("Creating a new Room...");
        entityManager.getTransaction().begin();
        entityManager.persist(room);
        entityManager.getTransaction().commit();
        log.info("Created a new Room with id {}", room.getId());
        return room;
    }

    public Room getRoomById(Long id) {
        log.debug("Retrieving Room with id {}...", id);
        Room room = entityManager.find(Room.class, id);
        if (room != null) {
            log.info("Retrieved Room with id {}", id);
        } else {
            log.warn("No Room found with id {}", id);
        }
        return room;
    }

    public Set<Room> getAllRoomsByStatus(Room.Status status) {
        log.debug("Retrieving all Rooms with status {}...", status);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Room> cq = cb.createQuery(Room.class);
        Root<Room> room = cq.from(Room.class);
        cq.where(cb.equal(room.get("status"), status));
        Set<Room> rooms = entityManager.createQuery(cq).getResultStream().collect(Collectors.toSet());
        log.info("Retrieved {} Rooms with status {}", rooms.size(), status);
        return rooms;
    }

    public double applyRoomsForOrder(Set<Long> roomIds, Long orderId) {
        log.debug("Applying rooms for order with id {}...", orderId);
        double rentPerDay = 0d;
        entityManager.getTransaction().begin();
        for (Long roomId : roomIds) {
            Room roomToConnect = this.getRoomById(roomId);
            if (roomToConnect.getStatus() != Room.Status.FREE) {
                log.warn("Room with id {} is not free. Skipping...", roomId);
                continue;
            }
            rentPerDay += roomToConnect.getRentPricePerDay();
            roomToConnect.setStatus(Room.Status.RESERVED);
            this.updateRoom(roomToConnect);

            HostelOrder order = entityManager.find(HostelOrder.class, orderId);
            order.getRooms().add(roomToConnect);
            entityManager.merge(order);

            log.info("Room with id {} is attached to Order with id {}", roomId, orderId);
        }
        entityManager.getTransaction().commit();
        return rentPerDay;
    }


    public void updateRoom(Room room) {
        log.debug("Updating Room with id {}...", room.getId());
        entityManager.getTransaction().begin();
        entityManager.merge(room);
        entityManager.getTransaction().commit();
        log.info("Updated Room with id {}", room.getId());
    }

    public void deleteRoom(Long id) {
        log.debug("Deleting Room with id {}...", id);
        entityManager.getTransaction().begin();
        Room room = entityManager.find(Room.class, id);
        if (room != null) {
            entityManager.remove(room);
            log.info("Deleted Room with id {}", id);
        } else {
            log.warn("No Room found with id {}", id);
        }
        entityManager.getTransaction().commit();
    }
}
