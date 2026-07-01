package com.ussd_event_processor.util;

import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public class DateTimeParser {

    private static final String[] PATTERNS = {
            "yyyy-MM-dd HH:mm:ss,SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    };

    private static final DateTimeFormatter[] FORMATTERS;

    static {
        FORMATTERS = new DateTimeFormatter[PATTERNS.length];
        for (int i = 0; i < PATTERNS.length; i++) {
            FORMATTERS[i] = DateTimeFormatter.ofPattern(PATTERNS[i]);
        }
    }

    /**
     * Parses a datetime string into a LocalDateTime, trying multiple formats.
     * Returns null if the input is blank or cannot be parsed by any known format.
     */
    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                ParsePosition pos = new ParsePosition(0);
                TemporalAccessor parsed = formatter.parse(trimmed, pos);
                if (pos.getErrorIndex() == -1 && pos.getIndex() == trimmed.length()) {
                    if (parsed.isSupported(java.time.temporal.ChronoField.HOUR_OF_DAY)) {
                        return LocalDateTime.from(parsed);
                    }
                    return LocalDate.from(parsed).atStartOfDay();
                }
            } catch (DateTimeParseException e) {
                // try next pattern
            }
        }
        return null;
    }
}
