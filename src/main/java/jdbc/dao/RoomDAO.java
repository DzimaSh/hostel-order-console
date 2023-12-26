package jdbc.dao;

import entity.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RoomDAO {
    public final static String SELECT_BY_ID_SQL = "SELECT * FROM room WHERE id = ?";
    private final Connection connection;

    public RoomDAO(Connection connection) {
        this.connection = connection;
    }

    public void createRoom(Room room) throws SQLException {
        String sql = "INSERT INTO room (id, room_number, possible_livers, rent_price_per_day, status, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, room.getId());
            statement.setInt(2, room.getRoomNumber());
            statement.setLong(3, room.getPossibleLivers());
            statement.setDouble(4, room.getRentPricePerDay());
            statement.setString(5, room.getStatus().name());
            statement.setString(6, room.getType().name());
            statement.executeUpdate();
        }
    }

    public Room getRoomById(Long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return prepareRoom(resultSet);
            } else {
                return null;
            }
        }
    }

    public Set<Room> getAllRoomsByStatus(Room.Status status) throws SQLException {
        Set<Room> rooms = new HashSet<>();
        String sql = "SELECT * FROM room WHERE status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rooms.add(prepareRoom(resultSet));
            }
        }
        return rooms;
    }

    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE room SET room_number = ?, possible_livers = ?, rent_price_per_day = ?, status = ?, type = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, room.getRoomNumber());
            statement.setLong(2, room.getPossibleLivers());
            statement.setDouble(3, room.getRentPricePerDay());
            statement.setString(4, room.getStatus().name());
            statement.setString(5, room.getType().name());
            statement.setLong(6, room.getId());
            statement.executeUpdate();
        }
    }

    public double applyRoomsForOrder(Set<Long> roomIds, Long orderId) throws SQLException {
        String sql = "INSERT INTO order_room (order_id, room_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            double rentPerDay = 0d;
            for (Long roomId : roomIds) {
                Room roomToConnect = this.getRoomById(roomId);
                if (!roomToConnect.getStatus().equals(Room.Status.FREE)) {
                    System.out.println("Room " + roomId + " is not free. Abort!");
                    continue;
                }
                rentPerDay += roomToConnect.getRentPricePerDay();
                roomToConnect.setStatus(Room.Status.RESERVED);
                updateRoom(roomToConnect);

                statement.setLong(1, orderId);
                statement.setLong(2, roomId);
                statement.executeUpdate();
                System.out.println("Room" + roomId + " is attached to Order " + orderId);
            }
            return rentPerDay;
        }
    }

    public void deleteRoom(Long id) throws SQLException {
        String sql = "DELETE FROM room WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private static Room prepareRoom(ResultSet resultSet) throws SQLException {
        Room room = new Room();
        room.setId(resultSet.getLong("id"));
        room.setRoomNumber(resultSet.getInt("room_number"));
        room.setPossibleLivers(resultSet.getLong("possible_livers"));
        room.setRentPricePerDay(resultSet.getDouble("rent_price_per_day"));
        room.setStatus(Room.Status.valueOf(resultSet.getString("status")));
        room.setType(Room.Type.valueOf(resultSet.getString("type")));
        return room;
    }

    protected static Room prepareInject(Connection connection, Long roomId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, roomId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return prepareRoom(resultSet);
            }
            statement.close();
            return null;
        }
    }
}

