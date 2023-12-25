package jdbc.dao;

import entity.Bill;
import entity.HostelOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillDAO {
    public final static String SELECT_BY_ID_SQL = "SELECT * FROM bill WHERE id = ?";
    private final Connection connection;
    private final HostelOrderDAO hostelOrderDAO;

    public BillDAO(Connection connection, HostelOrderDAO hostelOrderDAO) {
        this.connection = connection;
        this.hostelOrderDAO = hostelOrderDAO;
    }

    public void createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bill (bill_price, status) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatement(bill, statement);
            statement.executeUpdate();
        }
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
        String sql = "SELECT * FROM bill WHERE status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Bill.Status.NOT_PAYED.name());
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
                hostelOrderDAO.getHostelOrderById(resultSet.getLong("hostel_order_id")),
                resultSet.getDouble("bill_price"),
                Bill.Status.valueOf(resultSet.getString("status"))
        );
    }

    private void prepareStatement(Bill bill, PreparedStatement statement) throws SQLException {
        statement.setDouble(1, bill.getBillPrice());
        statement.setString(2, bill.getStatus().name());
    }

    protected static Bill prepareBillInject(Connection connection, Long billId) throws SQLException {
        Bill bill = null;
        PreparedStatement statement = connection.prepareStatement(BillDAO.SELECT_BY_ID_SQL);
        statement.setLong(1, billId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            bill = new Bill();
            bill.setId(resultSet.getLong("id"));
            bill.setBillPrice(resultSet.getDouble("bill_price"));
            bill.setStatus(Bill.Status.valueOf(resultSet.getString("status")));
        }

        return bill;
    }
}
