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
        try {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                throw error("Expected an integer.");
            }
        } finally {
            scanner.nextLine();
        }
    }

    public static Long scanLong() {
        try {
            if (scanner.hasNextLong()) {
                return scanner.nextLong();
            } else {
                throw error("Expected a long.");
            }
        } finally {
            scanner.nextLine();
        }
    }

    public static Double scanDouble() {
        try {
            if (scanner.hasNextDouble()) {
                return scanner.nextDouble();
            } else {
                throw error("Expected a double.");
            }
        } finally {
            scanner.nextLine();
        }
    }

    private static InputMismatchException error(String message) {
        log.error("Cannot read value...");
        return new InputMismatchException(message);
    }
}
