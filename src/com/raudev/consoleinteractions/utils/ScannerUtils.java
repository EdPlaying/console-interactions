package com.raudev.consoleinteractions.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class for capturing and parsing user input from the console.
 *
 * <p>This class provides methods for capturing common data types from
 * standard input, including text, integers, and decimal numbers.</p>
 *
 * <p>It also provides a method for converting delimited text into a
 * two-dimensional list structure that can be used to represent tabular
 * data.</p>
 *
 * <p>The class uses a shared {@link Scanner} instance connected to
 * {@link System#in}.</p>
 */
public final class ScannerUtils {

    /**
     * Shared scanner used to read input from the standard input stream.
     */
    public static final Scanner scanner = new Scanner(System.in);

    private ScannerUtils() {
        // Prevent instantiation.
    }

    /**
     * Captures a text value from the console.
     *
     * <p>The specified message is displayed before waiting for the user's
     * input. The method reads the complete line entered by the user.</p>
     *
     * @param message message displayed before requesting the input
     * @return text entered by the user
     */
    public static String captureText(String message) {
        System.out.println(message + ": ");
        return scanner.nextLine();
    }

    /**
     * Captures an integer value from the console.
     *
     * <p>The specified message is displayed before waiting for the user's
     * input. After reading the integer, the remaining line separator is
     * consumed so that subsequent calls to {@link #captureText(String)}
     * work correctly.</p>
     *
     * @param message message displayed before requesting the input
     * @return integer entered by the user
     */
    public static int captureInt(String message) {
        System.out.println(message + ": ");
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    /**
     * Captures a decimal number from the console.
     *
     * <p>The specified message is displayed before waiting for the user's
     * input. After reading the decimal number, the remaining line separator
     * is consumed so that subsequent calls to {@link #captureText(String)}
     * work correctly.</p>
     *
     * @param message message displayed before requesting the input
     * @return decimal number entered by the user
     */
    public static double captureDouble(String message) {
        System.out.println(message + ": ");
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    /**
     * Parses delimited text into a two-dimensional list structure.
     *
     * <p>Each line in the input represents a row, while each value separated
     * by the {@code |} character represents a column.</p>
     *
     * <p>Empty values are preserved, including empty values at the end of
     * rows. Empty lines are also preserved to avoid losing information from
     * the original text.</p>
     *
     * <p>For example, the following text:</p>
     *
     * <pre>
     * name|email|password
     * carlos|carlos@example.com|12345
     * </pre>
     *
     * <p>is converted into a structure equivalent to:</p>
     *
     * <pre>
     * [
     *     ["name", "email", "password"],
     *     ["carlos", "carlos@example.com", "12345"]
     * ]
     * </pre>
     *
     * @param text text containing rows separated by line breaks and columns
     *             separated by {@code |}
     * @return a two-dimensional list containing the parsed rows and columns;
     *         an empty list if {@code text} is {@code null} or empty
     */
    public static List<List<?>> parseTable(String text) {

        List<List<?>> table = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return table;
        }

        String[] rows = text.split("\\R", -1);

        for (String row : rows) {

            String[] columns = row.split("\\|", -1);

            List<String> parsedRow = new ArrayList<>();

            for (String column : columns) {
                parsedRow.add(column);
            }

            table.add(parsedRow);
        }

        return table;
    }
}
