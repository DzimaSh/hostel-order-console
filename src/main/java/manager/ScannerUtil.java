package manager;

import entity.Room;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class ScannerUtil {

    private static final Scanner scanner = new Scanner(System.in);

    public static String scanLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        } else {
            throw error("Expected a line.");
        }
    }

    public static Integer scanInteger() {
        if (scanner.hasNextInt()) {
            int value = scanner.nextInt();
            scanner.nextLine();
            return value;
        } else {
            throw error("Expected an integer.");
        }
    }

    public static Long scanLong() {
        if (scanner.hasNextLong()) {
            long value = scanner.nextLong();
            scanner.nextLine();
            return value;
        } else {
            throw error("Expected a long.");
        }
    }

    public static Double scanDouble() {
        if (scanner.hasNextDouble()) {
            double value = scanner.nextDouble();
            scanner.nextLine();
            return value;
        } else {
            throw error("Expected a double.");
        }
    }

    private static InputMismatchException error(String message) {
        log.error("Cannot read value...");
        return new InputMismatchException(message);
    }
}
