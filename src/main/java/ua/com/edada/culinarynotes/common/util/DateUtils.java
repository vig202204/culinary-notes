package ua.com.edada.culinarynotes.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 */
public final class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Formats a LocalDateTime using the default formatter (yyyy-MM-dd HH:mm:ss).
     *
     * @param dateTime the date time to format
     * @return the formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    /**
     * Formats a LocalDateTime using a custom formatter.
     *
     * @param dateTime the date time to format
     * @param pattern the pattern to use for formatting
     * @return the formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
    
    /**
     * Parses a date time string using the default formatter (yyyy-MM-dd HH:mm:ss).
     *
     * @param dateTimeStr the date time string to parse
     * @return the parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }
    
    /**
     * Parses a date time string using a custom formatter.
     *
     * @param dateTimeStr the date time string to parse
     * @param pattern the pattern to use for parsing
     * @return the parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}