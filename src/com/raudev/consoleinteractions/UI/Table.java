package com.raudev.consoleinteractions.UI;

import com.raudev.consoleinteractions.Main;
import com.raudev.consoleinteractions.utils.TextFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for rendering formatted tables in the console.
 *
 * <p>The table uses the line length defined by {@link Main#LINE_LENGTH}
 * and dynamically adjusts the height of each row when cell content
 * spans multiple lines.</p>
 *
 * <p>The first and last lines of the table use column-aware borders.
 * The header is separated from the table body using a column-aware
 * separator, while common rows use a continuous horizontal separator.</p>
 */
public final class Table {

    private Table() {
        // Prevent instantiation.
    }

    /**
     * Prints a formatted table to the console.
     *
     * <p>The number of columns is determined by the row containing the
     * largest number of elements. Missing cells are rendered as empty
     * cells.</p>
     *
     * <p>The table begins and ends with a column-aware horizontal border.
     * The first row is treated as the header and is separated from the
     * table body using the same column structure. Common rows are
     * separated using a continuous horizontal line.</p>
     *
     * @param matrix table data to render
     */
    public static void printTable(List<List<?>> matrix) {

        if (!isValidMatrix(matrix)) {
            return;
        }

        int columnCount = getColumnCount(matrix);

        if (columnCount == 0) {
            return;
        }

        if (columnCount > 10) {
            System.out.println("The maximum number of columns is 10.");
            return;
        }

        int[] columnWidths = calculateColumnWidths(columnCount);

        String[] columnFormats = createColumnFormats(columnWidths);

        printOuterSeparator(columnWidths);

        for (int rowIndex = 0; rowIndex < matrix.size(); rowIndex++) {

            List<List<String>> processedCells =
                    processRow(
                            matrix.get(rowIndex),
                            columnCount,
                            columnWidths
                    );

            printRow(
                    processedCells,
                    columnCount,
                    columnFormats
            );

            if (rowIndex == 0) {
                printColumnSeparator(columnWidths);
            } else if (rowIndex < matrix.size() - 1) {
                printRowSeparator();
            }
        }

        printOuterSeparator(columnWidths);
    }

    /**
     * Validates the table data before processing.
     *
     * @param matrix table data to validate
     * @return {@code true} if the matrix can be processed
     */
    private static boolean isValidMatrix(List<List<?>> matrix) {

        if (matrix == null || matrix.isEmpty()) {
            System.out.println("The table is empty.");
            return false;
        }

        return true;
    }

    /**
     * Determines the maximum number of columns present in the table.
     *
     * @param matrix table data
     * @return maximum number of columns
     */
    private static int getColumnCount(List<List<?>> matrix) {

        int columnCount = 0;

        for (List<?> row : matrix) {

            if (row != null) {
                columnCount = Math.max(columnCount, row.size());
            }
        }

        return columnCount;
    }

    /**
     * Calculates the width of every column while ensuring that the
     * complete rendered line uses exactly {@link Main#LINE_LENGTH}
     * characters.
     *
     * <p>The calculation reserves one character for every internal
     * separator and two characters for the outer borders. Any remaining
     * characters are distributed among the columns so that the total
     * line length is preserved.</p>
     *
     * @param columnCount number of columns
     * @return array containing the width of every column
     */
    private static int[] calculateColumnWidths(int columnCount) {

        int separatorCount = columnCount - 1;
        int borderCount = 2;

        int availableWidth =
                Main.LINE_LENGTH
                        - separatorCount
                        - borderCount;

        int baseWidth = availableWidth / columnCount;
        int remainingWidth = availableWidth % columnCount;

        int[] columnWidths = new int[columnCount];

        for (int columnIndex = 0;
             columnIndex < columnCount;
             columnIndex++) {

            columnWidths[columnIndex] =
                    baseWidth
                            + (columnIndex < remainingWidth ? 1 : 0);
        }

        return columnWidths;
    }

    /**
     * Creates the formatting pattern used to left-align each column.
     *
     * @param columnWidths width of every column
     * @return formatting patterns for every column
     */
    private static String[] createColumnFormats(int[] columnWidths) {

        String[] columnFormats = new String[columnWidths.length];

        for (int columnIndex = 0;
             columnIndex < columnWidths.length;
             columnIndex++) {

            columnFormats[columnIndex] =
                    "%-" + columnWidths[columnIndex] + "s";
        }

        return columnFormats;
    }

    /**
     * Processes all cells belonging to a row.
     *
     * @param row row to process
     * @param columnCount total number of columns
     * @param columnWidths width of every column
     * @return processed cells
     */
    private static List<List<String>> processRow(
            List<?> row,
            int columnCount,
            int[] columnWidths
    ) {

        List<List<String>> processedCells = new ArrayList<>();

        for (int columnIndex = 0;
             columnIndex < columnCount;
             columnIndex++) {

            Object cellValue =
                    row != null && columnIndex < row.size()
                            ? row.get(columnIndex)
                            : null;

            String cellText =
                    cellValue != null
                            ? cellValue.toString()
                            : "";

            String formattedText =
                    TextFormatter.wordWrap(
                            columnWidths[columnIndex],
                            cellText
                    );

            processedCells.add(
                    List.of(
                            formattedText.split("\\R", -1)
                    )
            );
        }

        return processedCells;
    }

    /**
     * Prints a processed row.
     *
     * @param processedCells processed cells belonging to the row
     * @param columnCount number of columns
     * @param columnFormats formatting patterns for every column
     */
    private static void printRow(
            List<List<String>> processedCells,
            int columnCount,
            String[] columnFormats
    ) {

        int rowHeight = getRowHeight(processedCells);

        for (int lineIndex = 0;
             lineIndex < rowHeight;
             lineIndex++) {

            StringBuilder line = new StringBuilder();

            line.append("|");

            for (int columnIndex = 0;
                 columnIndex < columnCount;
                 columnIndex++) {

                List<String> cellLines =
                        processedCells.get(columnIndex);

                String cellText =
                        lineIndex < cellLines.size()
                                ? cellLines.get(lineIndex)
                                : "";

                line.append(
                        String.format(
                                columnFormats[columnIndex],
                                cellText
                        )
                );

                line.append("|");
            }

            System.out.println(line);
        }
    }

    /**
     * Determines the height required by a row.
     *
     * @param processedCells processed cells belonging to the row
     * @return number of console lines required by the row
     */
    private static int getRowHeight(
            List<List<String>> processedCells
    ) {

        int rowHeight = 1;

        for (List<String> cellLines : processedCells) {
            rowHeight = Math.max(
                    rowHeight,
                    cellLines.size()
            );
        }

        return rowHeight;
    }

    /**
     * Prints the top or bottom border of the table.
     *
     * <p>The border preserves the exact column widths and therefore
     * matches {@link Main#LINE_LENGTH}.</p>
     *
     * @param columnWidths width of every column
     */
    private static void printOuterSeparator(int[] columnWidths) {

        StringBuilder separator = new StringBuilder();

        separator.append("+");

        for (int columnWidth : columnWidths) {

            separator.append("-".repeat(columnWidth));
            separator.append("+");
        }

        System.out.println(separator);
    }

    /**
     * Prints the separator between the header and the table body.
     *
     * <p>This separator preserves the individual column boundaries
     * using {@code +} characters.</p>
     *
     * @param columnWidths width of every column
     */
    private static void printColumnSeparator(int[] columnWidths) {
        printOuterSeparator(columnWidths);
    }

    /**
     * Prints a continuous separator between common rows.
     *
     * <p>This separator does not expose individual column intersections,
     * visually distinguishing common row boundaries from the header.</p>
     */
    private static void printRowSeparator() {

        System.out.println(
                "+" + "-".repeat(Main.LINE_LENGTH - 2) + "+"
        );
    }
}
