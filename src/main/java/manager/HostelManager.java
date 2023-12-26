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
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static manager.ScannerUtil.*;

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
        this.hostelOrderDAO = new HostelOrderDAO(connection);
        this.billDAO = new BillDAO(connection);

        this.scanner = scanner;
        this.securityHolder = new SecurityHolder(hostelUserDAO);
    }

    public void run() {
        while (true) {
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
        try {
            switch (choice) {
                case 1:
                    seeProfilePage();
                    break;
                case 2:
                    payForOrder();
                    break;
                case 3:
                    checkOrders();
                    break;
                case 4:
                    createNewOrder();
                    break;
                case 5:
                    approveOrder();
                    break;
                case 6:
                    viewUnpaidBills();
                    break;
                case 7:
                    seeFreeRooms();
                    break;
                case 8:
                    seeOpenOrders();
                    break;
                case 0:
                    logout();
                    System.out.println("Logged out...");
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (DateTimeParseException | InputMismatchException | IllegalArgumentException e) {
            System.out.println("Invalid input!");
        }
        System.out.println("Press Enter to proceed...");
        scanLine();
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
        System.out.println("Press Enter to proceed...");
        scanner.nextLine();

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
                8) See open orders
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
        log.debug("Creating new order for client " + securityHolder.getCurrentUser().getEmail());

        long clientId = securityHolder.getCurrentUser().getId();

        System.out.println("Enter number of beds:");
        int beds = scanInteger();

        System.out.println("Enter room type (BASIC or PREMIUM):");
        Room.Type type = Room.Type.valueOf(scanLine().toUpperCase());

        System.out.println("Enter start date (yyyy-mm-dd):");
        LocalDate startDate = LocalDate.parse(scanLine());

        System.out.println("Enter end date (yyyy-mm-dd):");
        LocalDate endDate = LocalDate.parse(scanLine());

        HostelUser client = hostelUserDAO.getHostelUserById(clientId);

        HostelOrder order = new HostelOrder(
                null, startDate, endDate,
                client, new HashSet<>(), null,
                type, beds, HostelOrder.Status.OPEN
        );

        hostelOrderDAO.createHostelOrder(order);
        System.out.println("Order is created! Please wait till approval, then pay the bill!");

        log.info("Order created.");
    }
    private void checkOrders() throws SQLException {
        log.debug("Fetching all orders for user " + securityHolder.getCurrentUser().getEmail());

        List<HostelOrder> orders = hostelOrderDAO
                .getAllHostelOrdersByUser(securityHolder.getCurrentUser().getId());
        System.out.println("You have " + orders.size() + " orders!");

        if (!orders.isEmpty()) {
            String ordersDetails = orders.stream()
                    .map(order -> "\t" + order.details().replace("\n", "\n\t"))
                    .collect(Collectors.joining("\n"));
            System.out.println(ordersDetails);
        }

        log.debug("Fetched " + orders.size() + " orders");
    }

    private void payForOrder() throws SQLException {
        log.debug("Paying for order");

        System.out.println("Enter order ID to pay for: ");
        Long orderIdToPay = scanLong();

        HostelOrder orderToPay = hostelOrderDAO.getHostelOrderById(orderIdToPay);
        if (orderToPay.getStatus().equals(HostelOrder.Status.OPEN)) {
           log.debug("Trying to pay unapproved order bill...");
           throw new IllegalStateException("Order is not approved. Cannot proceed payment...");
        }

        if (Objects.isNull(orderToPay.getBill())) {
            log.error("Cannot proceed payment! Bill is null");
            throw new NullPointerException("Bill is null");
        }

        System.out.println(orderToPay.details());

        if (orderToPay.getBill().getStatus().equals(Bill.Status.PAYED)) {
            log.debug("Trying to pay already payed bill...");
            throw new IllegalStateException("Bill is already payed");
        }

        System.out.println("Bill Amount: " + orderToPay.getBill().getBillPrice());

        System.out.println("Press Enter to proceed...");
        scanner.nextLine();

        System.out.println("Are you sure (Y/N)?");
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Payment cancelled.");
            return;
        }

        orderToPay.setStatus(HostelOrder.Status.PAYED);
        hostelOrderDAO.updateHostelOrder(orderToPay);
        Bill payedBill = orderToPay.getBill();
        payedBill.setStatus(Bill.Status.PAYED);
        billDAO.updateBill(payedBill);
        System.out.println("Nice! You have payed your order:)");


        log.debug("Order with id: " + orderToPay.getId() +  " is successfully paid.");
    }

    private void seeFreeRooms() throws SQLException {
        log.debug("Fetching for all free rooms...");

        adminCheck();

        Set<Room> rooms = roomDAO.getAllRoomsByStatus(Room.Status.FREE);
        if (rooms.isEmpty()) {
            System.out.println("Unfortunately, there is no any available room to rent...");
            return;
        }

        System.out.println("There are " + rooms.size() + " available rooms:");

        String roomsDetails = rooms.stream()
                .map(room -> "\t" + room.details().replace("\n", "\n\t"))
                .collect(Collectors.joining("\n"));

        System.out.println(roomsDetails);
        System.out.println("You can assign any of these rooms to the incoming orders!");

        log.debug(rooms.size() + " available rooms fetched");
    }

    private void viewUnpaidBills() throws SQLException {
        log.debug("Fetching for unpaid bills...");

        Set<Bill> unpaidBills = billDAO.getAllUnpaidBills();
        if (unpaidBills.isEmpty()) {
            System.out.println("Nice! There are no unpaid bills!");
            return;
        }

        System.out.println("Unpaid bills:");
        String billsDetails = unpaidBills.stream()
                .map(bill -> "\t" + bill.details().replace("\n", "\n\t"))
                .collect(Collectors.joining("\n"));
        System.out.println(billsDetails);

        log.debug(unpaidBills.size() + " unpaid bills fetched");
    }

    private void approveOrder() throws SQLException {
        log.info("Approving order...");

        adminCheck();

        System.out.println("Enter order ID to approve:");
        Long orderId = scanner.nextLong();

        HostelOrder orderToApprove = hostelOrderDAO.getHostelOrderById(orderId);
        if (Objects.isNull(orderToApprove)) {
            throw new IllegalStateException("Order with id " + orderId + " not found!");
        }
        if (orderToApprove.getStatus().equals(HostelOrder.Status.APPROVED)) {
            log.debug("Attempt to approve approved order...");
            throw new IllegalStateException("Order is already approved!");
        }

        System.out.println("Input room ids to attach. Input 'done' when finished.");
        Set<Long> roomIds = new HashSet<>();
        while (true) {
            String input = scanner.next();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            try {
                Long roomId = Long.parseLong(input);
                roomIds.add(roomId);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid room id or 'done' to finish.");
            }
        }
        double rentPerDay = roomDAO.applyRoomsForOrder(roomIds, orderId);
        Bill bill = orderToApprove.getBill();
        bill.setBillPrice(rentPerDay * orderToApprove.countPeriodOfOrder());
        billDAO.updateBill(bill);

        orderToApprove.setStatus(HostelOrder.Status.APPROVED);
        hostelOrderDAO.updateHostelOrder(orderToApprove);
        System.out.println("Order " + orderId + " approved.");

        log.info("Order " + orderId + " approved.");
    }

    private void seeOpenOrders() throws SQLException {
        log.debug("Request to see open orders...");

        List<HostelOrder> openOrders = hostelOrderDAO.getAllOpenedHostelOrders();
        if (openOrders.isEmpty()) {
            System.out.println("All orders are approved!");
        } else {
            String ordersDetails = openOrders.stream()
                    .map(order -> "\t" + order.details().replace("\n", "\n\t"))
                    .collect(Collectors.joining("\n"));
            System.out.println("You have " + openOrders.size() + " orders to be approved: ");
            System.out.println(ordersDetails);
        }

        log.debug("Retrieved " + openOrders.size() + " open orders!");
    }

    private void adminCheck() {
        if (!securityHolder.isAdminSession()) {
            log.warn("Regular user's attempt to see all rooms!");
            throw new IllegalStateException("Access denied");
        }
    }

}