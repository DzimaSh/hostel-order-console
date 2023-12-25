package jdbc.dao;

import entity.Bill;
import entity.HostelOrder;
import entity.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HostelOrderDAO {
    public final static String SELECT_BY_ID_SQL = "SELECT * FROM hostel_order WHERE id = ?";
    private final Connection connection;
    private final HostelUserDAO hostelUserDAO;

    public HostelOrderDAO(Connection connection,
                          HostelUserDAO hostelUserDAO) {
        this.connection = connection;
        this.hostelUserDAO = hostelUserDAO;
    }

    public void createHostelOrder(HostelOrder order) throws SQLException {
        String sql = "INSERT INTO hostel_order (start_date, end_date, client_id, desired_room_type, desired_beds, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatement(order, statement);
            statement.executeUpdate();
        }
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

    public List<HostelOrder> getAllHostelOrders() throws SQLException {
        List<HostelOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM hostel_order";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
        return new HostelOrder(
                resultSet.getLong("id"),
                resultSet.getDate("start_date").toLocalDate(),
                resultSet.getDate("end_date").toLocalDate(),
                hostelUserDAO.getHostelUserById(resultSet.getLong("client_id")),
                new HashSet<>(), // You might want to fetch the actual rooms here
                BillDAO.prepareBillInject(connection, resultSet.getLong("bill_id")),
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
}
