package jdbc.dao;

import entity.HostelUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HostelUserDAO {
    private final Connection connection;

    public HostelUserDAO(Connection connection) {
        this.connection = connection;
    }

    public void createHostelUser(HostelUser hostelUser) throws SQLException {
        String sql = "INSERT INTO hostel_user (id, name, email, password, authority) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, hostelUser.getId());
            statement.setString(2, hostelUser.getName());
            statement.setString(3, hostelUser.getEmail());
            statement.setString(4, hostelUser.getPassword());
            statement.setString(5, hostelUser.getAuthority().name());
            statement.executeUpdate();
        }
    }

    public HostelUser getHostelUserById(Long id) throws SQLException {
        String sql = "SELECT * FROM hostel_user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return prepareHostelUser(resultSet);
            } else {
                return null;
            }
        }
    }

    public List<HostelUser> getAllHostelUsers() throws SQLException {
        List<HostelUser> hostelUsers = new ArrayList<>();
        String sql = "SELECT * FROM hostel_user";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                hostelUsers.add(prepareHostelUser(resultSet));
            }
        }
        return hostelUsers;
    }

    public void updateHostelUser(HostelUser hostelUser) throws SQLException {
        String sql = "UPDATE hostel_user SET name = ?, email = ?, password = ?, authority = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hostelUser.getName());
            statement.setString(2, hostelUser.getEmail());
            statement.setString(3, hostelUser.getPassword());
            statement.setString(4, hostelUser.getAuthority().name());
            statement.setLong(5, hostelUser.getId());
            statement.executeUpdate();
        }
    }

    public void deleteHostelUser(Long id) throws SQLException {
        String sql = "DELETE FROM hostel_user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public HostelUser getHostelUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM hostel_user WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return prepareHostelUser(resultSet);
            } else {
                return null;
            }
        }
    }


    private HostelUser prepareHostelUser(ResultSet resultSet) throws SQLException {
        HostelUser hostelUser = new HostelUser();
        hostelUser.setId(resultSet.getLong("id"));
        hostelUser.setName(resultSet.getString("name"));
        hostelUser.setEmail(resultSet.getString("email"));
        hostelUser.setPassword(resultSet.getString("password"));
        hostelUser.setAuthority(HostelUser.Authority.valueOf(resultSet.getString("authority")));
        return hostelUser;
    }
}
