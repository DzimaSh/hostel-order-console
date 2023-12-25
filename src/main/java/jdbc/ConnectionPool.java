package jdbc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ConnectionPool implements AutoCloseable {
    private final Properties properties;
    private final List<Connection> connections;

    public ConnectionPool(String propertiesFile, int initialPoolSize) throws SQLException {
        this.properties = loadProperties(propertiesFile);
        this.connections = new CopyOnWriteArrayList<>();
        initializePool(initialPoolSize);
    }

    private Properties loadProperties(String propertiesFile) throws SQLException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading database properties", e);
            throw new SQLException("Error while loading the db", e);
        }
        return properties;
    }

    private void initializePool(int initialSize) throws SQLException {
        log.debug("Initialize connection pool. Open " + initialSize + " connections...");
        for (int i = 0; i < initialSize; i++) {
            Connection connection = createConnection();
            log.trace("Connection " + i + " was opened...");
            connections.add(connection);
        }
    }

    private Connection createConnection() throws SQLException {
        try {
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            log.error("Error creating database connection", e);
            throw e;
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connections.isEmpty()) {

            Connection newConnection = createConnection();
            connections.add(newConnection);
            return newConnection;
        }
        return connections.remove(connections.size() - 1);
    }

    public synchronized void close() throws SQLException {
        for (Connection connection : connections) {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    log.error("Error closing connection", e);
                    throw e;
                }
            }
        }

        connections.clear();
    }
}
