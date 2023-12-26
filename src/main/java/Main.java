import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import manager.HostelManager;

import java.util.Scanner;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Starting application");
        try {
            log.debug("Creating EntityManagerFactory");
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("hostel-order-persistence-unit");
            EntityManager entityManager = emf.createEntityManager();

            log.debug("Creating Scanner");
            Scanner scanner = new Scanner(System.in);

            log.trace("Creating HostelManager");
            HostelManager manager = new HostelManager(entityManager, scanner);
            log.info("Running HostelManager");
            manager.run();

            log.debug("Closing resources");
            scanner.close();
            entityManager.close();
            emf.close();
        } catch (Exception e) {
            log.error("An error occurred while setting up the application", e);
        }
        log.info("Exiting application");
    }
}