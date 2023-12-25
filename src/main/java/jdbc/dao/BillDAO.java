package jdbc.dao;

import entity.Bill;
import entity.HostelOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class BillDAO {
    public final static String SELECT_BY_ID_SQL = "SELECT * FROM bill WHERE id = ?";
    private final Connection connection;

    public BillDAO(Connection connection) {
        this.connection = connection;
    }

    public Bill createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bill (bill_price, status) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement(bill, statement);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                log.error("Error while creating Bill");
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bill.setId(generatedKeys.getLong(1));
                } else {
                    log.error("Error while creating Bill");
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
        return bill;
    }

    public Bill getBillById(Long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return prepareBill(resultSet);
            } else {
                return null;
            }
        }
    }

    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bills.add(prepareBill(resultSet));
            }
        }
        return bills;
    }

    public void updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bill SET bill_price = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatement(bill, statement);
            statement.setLong(3, bill.getId());
            statement.executeUpdate();
        }
    }

    public void deleteBill(Long id) throws SQLException {
        String sql = "DELETE FROM bill WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Set<Bill> getAllUnpaidBills() throws SQLException {
        Set<Bill> unpaidBills = new HashSet<>();
        String sql = """
                 SELECT b.*, ho.id AS hostel_order_id FROM hostel_order AS ho
                 LEFT JOIN bill AS b on b.id = ho.bill_id
                 WHERE ho.status = ? AND b.status = ?;
                 """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, HostelOrder.Status.APPROVED.name());
            statement.setString(2, Bill.Status.NOT_PAYED.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                unpaidBills.add(prepareBill(resultSet));
            }
        }
        return unpaidBills;
    }

    private Bill prepareBill(ResultSet resultSet) throws SQLException {
        return new Bill(
                resultSet.getLong("id"),
                HostelOrderDAO.prepareInject(connection, resultSet.getLong("hostel_order_id")),
                resultSet.getDouble("bill_price"),
                Bill.Status.valueOf(resultSet.getString("status"))
        );
    }

    private void prepareStatement(Bill bill, PreparedStatement statement) throws SQLException {
        statement.setDouble(1, bill.getBillPrice());
        statement.setString(2, bill.getStatus().name());
    }

    protected static Bill prepareInject(Connection connection, Long billId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(BillDAO.SELECT_BY_ID_SQL)) {
            statement.setLong(1, billId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Bill(
                        resultSet.getLong("id"),
                        null,
                        resultSet.getDouble("bill_price"),
                        Bill.Status.valueOf(resultSet.getString("status"))
                );
            }
            return null;
        }
    }
}
