package com.raudev.consoleinteractions.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for formatting text for console output.
 */
public final class TextFormatter {

    private TextFormatter() {
        // Prevent instantiation.
    }

    /**
     * Wraps text according to the available space.
     *
     * <p>Words are kept intact whenever possible. When a word does not fit
     * in the current line, it is moved entirely to the next line.</p>
     *
     * <p>If a single word is longer than the available space, the word is
     * split so that no generated line exceeds the specified length.</p>
     *
     * @param availableSpace maximum number of characters allowed per line
     * @param text the text to format
     * @return the formatted text containing line breaks
     * @throws IllegalArgumentException if {@code availableSpace} is less
     *                                  than or equal to zero
     */
    public static String wordWrap(int availableSpace, String text) {
        if (availableSpace <= 0) {
            throw new IllegalArgumentException(
                    "Available space must be greater than zero."
            );
        }

        if (text == null || text.isBlank()) {
            return "";
        }

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        String[] words = text.trim().split("\\s+");

        for (String word : words) {

            if (word.length() > availableSpace) {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                }

                addLongWord(lines, word, availableSpace);
                continue;
            }

            if (currentLine.isEmpty()) {
                currentLine.append(word);
                continue;
            }

            int requiredLength =
                    currentLine.length() + 1 + word.length();

            if (requiredLength <= availableSpace) {
                currentLine.append(" ").append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return String.join(System.lineSeparator(), lines);
    }

    /**
     * Splits a word that exceeds the available space.
     *
     * @param lines list where the resulting lines are stored
     * @param word the word to split
     * @param availableSpace maximum number of characters per line
     */
    private static void addLongWord(
            List<String> lines,
            String word,
            int availableSpace
    ) {
        int start = 0;

        while (start < word.length()) {
            int end = Math.min(start + availableSpace, word.length());
            lines.add(word.substring(start, end));
            start = end;
        }
    }
}

