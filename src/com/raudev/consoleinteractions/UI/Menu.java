package com.raudev.consoleinteractions.UI;

import com.raudev.consoleinteractions.Main;
import com.raudev.consoleinteractions.utils.ScannerUtils;
import com.raudev.consoleinteractions.utils.TextFormatter;

import java.util.List;

/**
 * Represents a reusable numbered console menu.
 *
 * <p>A menu displays a title, a list of numbered options, and a special
 * option represented by number {@code 0}. The label of the special option
 * is provided by the caller, allowing it to represent actions such as
 * "Back" or "Exit".</p>
 *
 * <p>The menu uses {@link TextFormatter} to ensure that the title and
 * options respect {@link Main#LINE_LENGTH}. Long text is wrapped without
 * breaking words whenever possible, and continuation lines are properly
 * indented.</p>
 *
 * <p>This class is only responsible for displaying the menu and obtaining
 * a valid selection from the user. It does not perform any action based
 * on the selected option and does not handle navigation between menus.</p>
 */
public class Menu {

    /**
     * Number reserved for the special menu option.
     */
    private static final int SPECIAL_OPTION = 0;

    private final String title;
    private final List<String> options;
    private final String specialOptionLabel;

    /**
     * Creates a new menu.
     *
     * @param title the title or question displayed above the options
     * @param options the options displayed to the user
     * @param specialOptionLabel the label displayed for option {@code 0},
     *                            such as "Back" or "Exit"
     * @throws IllegalArgumentException if the title or special option label
     *                                  is null or blank, or if the options
     *                                  list is null or empty
     */
    public Menu(
            String title,
            List<String> options,
            String specialOptionLabel
    ) {
        validateTitle(title);
        validateOptions(options);
        validateSpecialOptionLabel(specialOptionLabel);

        this.title = title;
        this.options = List.copyOf(options);
        this.specialOptionLabel = specialOptionLabel;
    }

    /**
     * Displays the menu and waits for a valid user selection.
     *
     * <p>The returned value represents the number selected by the user.
     * Option {@code 0} always represents the special option defined by
     * {@code specialOptionLabel}.</p>
     *
     * @param selectionText the prompt message displayed to ask the user for selection
     * @param invalidText the error message displayed when an invalid selection is made
     * @return the number selected by the user
     */
    public int show(String selectionText, String invalidText) {
        printMenu();

        int selection;

        do {
            selection = ScannerUtils.captureInt(selectionText);

            if (!isValidSelection(selection)) {
                System.out.println(invalidText);
            }

        } while (!isValidSelection(selection));

        return selection;
    }

    /**
     * Prints the complete menu.
     */
    private void printMenu() {
        System.out.println();

        printTitle();

        System.out.println();

        printOptions();

        printNumberedOption(
                SPECIAL_OPTION,
                specialOptionLabel
        );

        System.out.println();
    }

    /**
     * Prints the menu title using the configured line length.
     */
    private void printTitle() {
        String formattedTitle = TextFormatter.wordWrap(
                Main.LINE_LENGTH,
                title
        );

        System.out.println(formattedTitle);
    }

    /**
     * Prints all regular menu options.
     */
    private void printOptions() {
        for (int index = 0; index < options.size(); index++) {
            int optionNumber = index + 1;

            printNumberedOption(
                    optionNumber,
                    options.get(index)
            );
        }
    }

    /**
     * Prints a numbered option and wraps its text when necessary.
     *
     * <p>The available space for the option text is calculated by subtracting
     * the length of the numerical prefix from {@link Main#LINE_LENGTH}.</p>
     *
     * <p>For example:</p>
     *
     * <pre>
     * 1. Juan Pérez es el delantero
     *    titular del equipo
     * </pre>
     *
     * @param number the number assigned to the option
     * @param text the option text
     */
    private void printNumberedOption(int number, String text) {
        String prefix = number + ". ";

        int availableSpace =
                Main.LINE_LENGTH - prefix.length();

        String formattedText = TextFormatter.wordWrap(
                availableSpace,
                text
        );

        String indentation = " ".repeat(prefix.length());

        String[] lines = formattedText.split(
                System.lineSeparator()
        );

        System.out.println(prefix + lines[0]);

        for (int index = 1; index < lines.length; index++) {
            System.out.println(
                    indentation + lines[index]
            );
        }
    }

    /**
     * Determines whether a selected number corresponds to a valid option.
     *
     * @param selection the number entered by the user
     * @return {@code true} if the selection is valid; otherwise {@code false}
     */
    private boolean isValidSelection(int selection) {
        return selection >= SPECIAL_OPTION
                && selection <= options.size();
    }

    /**
     * Validates the menu title.
     *
     * @param title the title to validate
     * @throws IllegalArgumentException if the title is null or blank
     */
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Menu title cannot be null or blank."
            );
        }
    }

    /**
     * Validates the menu options.
     *
     * @param options the options to validate
     * @throws IllegalArgumentException if the list is null or empty
     */
    private void validateOptions(List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException(
                    "Menu options cannot be null or empty."
            );
        }
    }

    /**
     * Validates the special option label.
     *
     * @param specialOptionLabel the label to validate
     * @throws IllegalArgumentException if the label is null or blank
     */
    private void validateSpecialOptionLabel(String specialOptionLabel) {
        if (specialOptionLabel == null || specialOptionLabel.isBlank()) {
            throw new IllegalArgumentException(
                    "Special option label cannot be null or blank."
            );
        }
    }
}