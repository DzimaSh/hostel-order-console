package jdbc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class JdbcConnector {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            log.debug("Loading database properties and establishing connection...");
            try {
                Properties properties = new Properties();
                InputStream inputStream = JdbcConnector.class
                        .getClassLoader()
                        .getResourceAsStream("database.properties");
                properties.load(inputStream);

                String url = properties.getProperty("db.url");
                String username = properties.getProperty("db.username");
                String password = properties.getProperty("db.password");

                connection = DriverManager.getConnection(url, username, password);
                log.info("Database connection established successfully.");
            } catch (IOException | SQLException e) {
                log.error("Error establishing database connection", e);
            }
        }
        return connection;
    }

    private static boolean isConnectionClosed() {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            log.error("Error checking connection status", e);
            return true;
        }
    }

    public static void closeConnection() {
        if (connection != null && !isConnectionClosed()) {
            try {
                connection.close();
                log.info("Database connection closed successfully.");
            } catch (SQLException e) {
                log.error("Error closing connection", e);
            }
        }
    }
}
