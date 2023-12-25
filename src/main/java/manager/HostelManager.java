package manager;

import entity.Bill;
import entity.HostelOrder;
import entity.HostelUser;
import entity.Room;
import jdbc.dao.BillDAO;
import jdbc.dao.HostelOrderDAO;
import jdbc.dao.HostelUserDAO;
import jdbc.dao.RoomDAO;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class HostelManager {

    private final HostelUserDAO hostelUserDAO;
    private final RoomDAO roomDAO;
    private final HostelOrderDAO hostelOrderDAO;
    private final BillDAO billDAO;
    private final Scanner scanner;

    public HostelManager(Connection connection, Scanner scanner) {
        this.hostelUserDAO = new HostelUserDAO(connection);
        this.roomDAO = new RoomDAO(connection);
        this.hostelOrderDAO = new HostelOrderDAO(connection, this.hostelUserDAO);
        this.billDAO = new BillDAO(connection, this.hostelOrderDAO);

         this.scanner = scanner;
    }

    public void run() {
        while (true) {
            showMenu();
            System.out.println("Enter command: ");
            int choice = -1;
            if(scanner.hasNextInt())
                choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1:
                        createNewOrder();
                        break;
                    case 2:
                        approveOrder();
                        break;
                    case 3:
                        payForOrder();
                        break;
                    case 4:
                        closeOrder();
                        break;
                    case 5:
                        viewUnpaidBills();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid command.");
                }
            } catch (SQLException e) {
                log.error("An error occurred", e);
            }
        }
    }

    private void showMenu() {
        System.out.println(
                """
                Menu:
                1) Create new order
                2) Approve order
                3) Pay for order
                4) Close order
                5) View unpaid bills
                0) Exit
                """
        );
    }

    private void createNewOrder() throws SQLException {
        System.out.println("Enter client ID:");
        long clientId = -1L;
        if (scanner.hasNextLong())
            clientId = scanner.nextLong();

        System.out.println("Enter number of beds:");
        int beds = -1;
        if (scanner.hasNextInt())
            clientId = scanner.nextInt();

        System.out.println("Enter room type (BASIC or PREMIUM):");
        Room.Type type = null;
        if (scanner.hasNextLine())
            type = Room.Type.valueOf(scanner.nextLine().toUpperCase());

        System.out.println("Enter start date (yyyy-mm-dd):");
        LocalDate startDate = null;
        if (scanner.hasNextLine())
            startDate = LocalDate.parse(scanner.nextLine());

        log.info("Enter end date (yyyy-mm-dd):");
        LocalDate endDate = null;
        if (scanner.hasNextLine())
            endDate = LocalDate.parse(scanner.nextLine());

        HostelUser client = hostelUserDAO.getHostelUserById(clientId);

        HostelOrder order = new HostelOrder(
                null, startDate, endDate,
                client, new HashSet<>(), null,
                type, beds,  HostelOrder.Status.OPEN
        );

        hostelOrderDAO.createHostelOrder(order);
        log.info("Order created.");
    }

    private void approveOrder() throws SQLException {
//        log.info("Enter order ID to approve:");
//        Long orderId = scanner.nextLong();
//        HostelOrder orderToApprove = hostelOrderDAO.getHostelOrderById(orderId);
//        orderToApprove.setStatus(HostelOrder.Status.APPROVED);
//        hostelOrderDAO.updateHostelOrder(orderToApprove);
//        log.info("Order approved.");
    }

    private void payForOrder() throws SQLException {
//        log.info("Enter order ID to pay for:");
//        Long orderIdToPay = scanner.nextLong();
//        HostelOrder orderToPay = hostelOrderDAO.getHostelOrderById(orderIdToPay);
//        orderToPay.setStatus(HostelOrder.Status.PAYED);
//        hostelOrderDAO.updateHostelOrder(orderToPay);
//        log.info("Order paid.");
    }

    private void closeOrder() throws SQLException {
//        log.info("Enter order ID to close:");
//        Long orderIdToClose = scanner.nextLong();
//        HostelOrder orderToClose = hostelOrderDAO.getHostelOrderById(orderIdToClose);
//        orderToClose.setStatus(HostelOrder.Status.CLOSED);
//        hostelOrderDAO.updateHostelOrder(orderToClose);
//        log.info("Order closed.");
    }

    private void viewUnpaidBills() throws SQLException {
        Set<Bill> unpaidBills = billDAO.getAllUnpaidBills();
        System.out.println("Unpaid bills:");
        unpaidBills.forEach(bill -> log.info(bill.toString()));
    }
}