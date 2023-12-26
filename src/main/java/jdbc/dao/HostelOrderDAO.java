package jdbc.dao;

import entity.Bill;
import entity.HostelOrder;
import entity.Room;
import entity.comparator.HostelOrderComparator;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class HostelOrderDAO {
    public final static String SELECT_BY_ID_SQL = "SELECT * FROM hostel_order WHERE id = ?";
    private final Connection connection;
    private final BillDAO billDAO;

    public HostelOrderDAO(Connection connection) {
        this.connection = connection;
        this.billDAO = new BillDAO(connection);
    }

    public HostelOrder createHostelOrder(HostelOrder order) throws SQLException {
        String sql = "INSERT INTO hostel_order (start_date, end_date, client_id, desired_room_type, desired_beds, status, bill_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (Objects.isNull(order.getBill()) || Objects.isNull(order.getBill().getId())) {
                order.setBill(billDAO.createBill(new Bill()));
            }

            prepareStatement(order, statement);
            statement.setLong(7, order.getBill().getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                log.error("Error while creating HostelOrder");
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prepareHostelOrder(generatedKeys);
                } else {
                    log.error("Error while creating HostelOrder");
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
        return order;
    }

    public HostelOrder getHostelOrderById(Long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return prepareHostelOrder(resultSet);
            } else {
                return null;
            }
        }
    }

    public List<HostelOrder> getAllOpenedHostelOrders() throws SQLException {
        List<HostelOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM hostel_order WHERE status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, HostelOrder.Status.OPEN.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(prepareHostelOrder(resultSet));
            }
        }
        return orders.stream()
                .sorted(new HostelOrderComparator())
                .toList();
    }

    public List<HostelOrder> getAllHostelOrdersByUser(Long userId) throws SQLException {
        List<HostelOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM hostel_order WHERE client_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(prepareHostelOrder(resultSet));
            }
        }
        return orders;
    }

    public void updateHostelOrder(HostelOrder order) throws SQLException {
        String sql = "UPDATE hostel_order SET start_date = ?, end_date = ?, client_id = ?, desired_room_type = ?, desired_beds = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatement(order, statement);
            statement.setLong(7, order.getId());
            statement.executeUpdate();
        }
    }

    public void deleteHostelOrder(Long id) throws SQLException {
        String sql = "DELETE FROM hostel_order WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private HostelOrder prepareHostelOrder(ResultSet resultSet) throws SQLException {
        Long orderId = resultSet.getLong("id");
        return new HostelOrder(
                orderId,
                resultSet.getDate("start_date").toLocalDate(),
                resultSet.getDate("end_date").toLocalDate(),
                HostelUserDAO.prepareInject(connection, resultSet.getLong("client_id")),
                fetchConnectedRooms(connection, orderId),
                BillDAO.prepareInject(connection, resultSet.getLong("bill_id")),
                Room.Type.valueOf(resultSet.getString("desired_room_type")),
                resultSet.getInt("desired_beds"),
                HostelOrder.Status.valueOf(resultSet.getString("status"))
        );
    }

    private void prepareStatement(HostelOrder order, PreparedStatement statement) throws SQLException {
        statement.setDate(1, java.sql.Date.valueOf(order.getStartDate()));
        statement.setDate(2, java.sql.Date.valueOf(order.getEndDate()));
        statement.setLong(3, order.getClient().getId());
        statement.setString(4, order.getDesiredRoomType().name());
        statement.setInt(5, order.getDesiredBeds());
        statement.setString(6, order.getStatus().name());
    }

    private static Set<Room> fetchConnectedRooms(Connection connection, Long id) throws SQLException {
        Set<Room> rooms = new HashSet<>();
        String sql = "SELECT * FROM order_room WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Room room = RoomDAO.prepareInject(connection, resultSet.getLong("room_id"));
                rooms.add(room);
            }
        }
        return rooms;
    }

    protected static HostelOrder prepareInject(Connection connection, Long orderId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new HostelOrder(
                        resultSet.getLong("id"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        null,
                        new HashSet<>(),
                        null,
                        Room.Type.valueOf(resultSet.getString("desired_room_type")),
                        resultSet.getInt("desired_beds"),
                        HostelOrder.Status.valueOf(resultSet.getString("status"))
                );
            }
            return null;
        }
    }
}
