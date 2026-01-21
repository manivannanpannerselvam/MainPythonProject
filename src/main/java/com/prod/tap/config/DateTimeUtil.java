package com.prod.tap.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.getDefault());
    private static DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("M/d/yyyy H:m:s.SSS", Locale.getDefault());

    public static LocalDate tryParseDate(final String dateString) {
        try {
            return LocalDate.parse(dateString, dateFormatter);
        } catch (DateTimeParseException ex) {
            // swallow exception and return null
            return null;
        }
    }

    public static LocalDateTime tryParseDateTime(final String dateString) {
        try {
            return LocalDateTime.parse(dateString, dateTimeformatter);
        } catch (DateTimeParseException ex) {
            // swallow exception and return null
            return null;
        }
    }

    public static LocalDateTime tryParseDateTime(final String format, final String dateString) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(format, Locale.getDefault()));
        } catch (DateTimeParseException ex) {
            // swallow exception and return null
            return null;
        }
    }

    public static String toString(final String format, final LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        return formatter.format(date);
    }

    public static String toString(final LocalDate date) {
        return dateFormatter.format(date);
    }

    public static String toString(final String format, final LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        return formatter.format(time);
    }

    public static String toString(final String format, final long timestamp) {
        LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault());
        return toString(format, triggerTime);
    }

    public static String toString(final LocalDateTime time) {
        return dateTimeformatter.format(time);
    }

    public static String toString(final String format, final LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        return formatter.format(time);
    }

    public static LocalDate toLocalDate(final Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(final Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean areEqual(final LocalDateTime expected, final LocalDateTime actual, final Long delta) {
        if (delta == null) {
            // No tolerance, compare exact
            return expected.equals(actual);
        } else {
            // we expected that expectedDate < actualDate < (expectedDate + delta seconds)
            LocalDateTime toDateTime = expected.plusSeconds(delta);
            return !actual.isBefore(expected) && !actual.isAfter(toDateTime);
        }
    }

    public static long getCurrentTimeStamp() {
        return LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }

    public static long getTimeStamp(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }

    public static long getFileLastModified(File file) throws IOException {
        long lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath())).toMillis();
        LocalDateTime time = getDateTime(lastModified);
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime getDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public static TemporalAmount getTemporalAmount(String feString) {
        if (Character.isUpperCase(feString.charAt(feString.length() - 1))) {
            return Period.parse("P" + feString);
        } else {
            return Duration.parse("PT" + feString);
        }
    }
}
