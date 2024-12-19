package states;
import java.util.Scanner;

public class Util {
    public static void log(Object message) {
        System.out.println(message);
    }
    
    public static void log(String message, boolean newline) {
        System.out.print(newline ? message + '\n' : message);
    }

    public static String getString(Scanner in, String message) {
        while (true) {
            try {
                log(message, false);
                return in.nextLine();
            } catch (Exception e) {
                log("Invalid input. Please try again.");
            }
        }
    }
    
    public static int getInt(Scanner in, String message) {
        while (true) {
            try {
                log(message, false);
                return Integer.parseInt(in.nextLine());
            } catch (Exception e) {
                log("Invalid input. Please try again.");
            }
        }
    }
}
