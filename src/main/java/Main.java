import jdbc.ConnectionPool;
import lombok.extern.slf4j.Slf4j;
import manager.HostelManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Starting application");
        try {
            log.debug("Creating connection pool");
            try (ConnectionPool connectionPool = new ConnectionPool("application.properties", 10);
                 Scanner scanner = new Scanner(System.in);
                 Connection connection = connectionPool.getConnection()
            ) {
                log.trace("Creating HostelManager");
                HostelManager manager = new HostelManager(connection, scanner);
                log.info("Running HostelManager");
                manager.run();
            } catch (SQLException e) {
                log.error("An error occurred while running the HostelManager", e);
            } finally {
                log.debug("Closing resources");
            }
        } catch (Exception e) {
            log.error("An error occurred while setting up the application", e);
        }
        log.info("Exiting application");
    }
}
