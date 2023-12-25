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
import security.SecurityHolder;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static manager.ScannerUtil.scanLong;

@Slf4j
public class HostelManager {

    private final HostelUserDAO hostelUserDAO;
    private final RoomDAO roomDAO;
    private final HostelOrderDAO hostelOrderDAO;
    private final BillDAO billDAO;
    private final Scanner scanner;
    private final SecurityHolder securityHolder;

    public HostelManager(Connection connection, Scanner scanner) {
        this.hostelUserDAO = new HostelUserDAO(connection);
        this.roomDAO = new RoomDAO(connection);
        this.hostelOrderDAO = new HostelOrderDAO(connection, this.hostelUserDAO);
        this.billDAO = new BillDAO(connection, this.hostelOrderDAO);

        this.scanner = scanner;
        this.securityHolder = new SecurityHolder(hostelUserDAO);
    }

    public void run() {
        while (true) {
            System.out.println("\n\n");
            try {
                if (securityHolder.getCurrentUser() == null) {
                    showLoginMenu();
                    if (loginMenu(enterCommand())) break;
                } else {
                    showMainMenu();
                    mainMenu(enterCommand());
                }
            } catch (SQLException e) {
                log.error("An error occurred", e);
            }
        }
    }

    private int enterCommand() {
        System.out.println("Enter command: ");
        int choice = -1;
        if(scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine();
        } else {
            log.error("Cannot proceed command");
            throw new IllegalStateException("Terminal is busy...");
        }

        return choice;
    }

    private void mainMenu(int choice) throws SQLException {
        switch (choice) {
            case 1:
                seeProfilePage();
                break;
            case 2:
                payForOrder();
                break;
//            case 2:
//                approveOrder();
//                break;
//            case 3:
//                payForOrder();
//                break;
//            case 4:
//                viewUnpaidBills();
//                break;
            case 0:
                logout();
                System.out.println("Logged out...");
                break;
            default:
                System.out.println("Invalid command.");
        }
    }

    private boolean loginMenu(int choice) throws SQLException {
        switch (choice) {
            case 1:
                login();
                break;
            case 0:
                System.out.println("Exiting...");
                return true;
            default:
                System.out.println("Invalid command.");
        }

        return false;
    }

    private void showLoginMenu() {
        System.out.println(
                """
                Menu:
                1) Login
                0) Exit
                """
        );
    }

    private void showMainMenu() {
        final String adminMenu = """
                5) Approve order
                6) View unpaid bills
                7) See free rooms
                8) See all clients
                """;

        System.out.printf(
                """
                Menu:
                1) See profile page
                2) Pay for order
                3) Check my orders
                4) Create new order
                %s
                0) Logout
                """, securityHolder.isAdminSession() ? adminMenu : ""
        );
    }

    private boolean login() throws SQLException {
        System.out.println("Enter email:");
        String email = "";
        if (scanner.hasNextLine())
            email = scanner.nextLine().trim();

        System.out.println("Enter password:");
        String password = "";
        if (scanner.hasNextLine())
            password = scanner.nextLine();

        return securityHolder.login(email, password);
    }

    private void logout() {
        securityHolder.logout();
        log.info("User logged out.");
    }

    private void seeProfilePage() {
        System.out.println(securityHolder.getCurrentUser().profile());
    }

    private void createNewOrder() throws SQLException {
        System.out.println("Enter client ID:");
        long clientId = -1L;
        if (scanner.hasNextLong()) {
            clientId = scanner.nextLong();
            scanner.nextLine();
        }

        System.out.println("Enter number of beds:");
        int beds = -1;
        if (scanner.hasNextInt()) {
            clientId = scanner.nextInt();
            scanner.nextLine();
        }

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
        log.info("Enter order ID to approve:");
        Long orderId = scanner.nextLong();
        HostelOrder orderToApprove = hostelOrderDAO.getHostelOrderById(orderId);
        orderToApprove.setStatus(HostelOrder.Status.APPROVED);
        hostelOrderDAO.updateHostelOrder(orderToApprove);
        log.info("Order approved.");
    }

    private void payForOrder() throws SQLException {
        log.debug("Paying for order");

        System.out.println("Enter order ID to pay for: ");
        Long orderIdToPay = scanLong();

        HostelOrder orderToPay = hostelOrderDAO.getHostelOrderById(orderIdToPay);
        orderToPay.setStatus(HostelOrder.Status.PAYED);
        hostelOrderDAO.updateHostelOrder(orderToPay);
        System.out.println("Nice! You have payed your order:)");


        log.debug("Order with id: " + orderToPay.getId() +  " is successfully paid.");
    }

    private void closeOrder() throws SQLException {
        log.info("Enter order ID to close:");
        Long orderIdToClose = scanner.nextLong();
        HostelOrder orderToClose = hostelOrderDAO.getHostelOrderById(orderIdToClose);
        orderToClose.setStatus(HostelOrder.Status.CLOSED);
        hostelOrderDAO.updateHostelOrder(orderToClose);
        log.info("Order closed.");
    }

    private void viewUnpaidBills() throws SQLException {
        Set<Bill> unpaidBills = billDAO.getAllUnpaidBills();
        System.out.println("Unpaid bills:");
        unpaidBills.forEach(bill -> log.info(bill.toString()));
    }
}