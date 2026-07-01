package com.ussd_event_processor.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeParserTest {

    @Test
    void parseSpaceSeparatedWithCommaMillis() {
        assertEquals(
                LocalDateTime.of(2023, 10, 27, 10, 0, 0),
                DateTimeParser.parse("2023-10-27 10:00:00,000")
        );
    }

    @Test
    void parseSpaceSeparatedWithoutMillis() {
        assertEquals(
                LocalDateTime.of(2023, 8, 18, 10, 30, 0),
                DateTimeParser.parse("2023-08-18 10:30:00")
        );
    }

    @Test
    void parseIsoWithMillisAndZ() {
        assertEquals(
                LocalDateTime.of(2026, 7, 1, 11, 36, 44, 284_000_000),
                DateTimeParser.parse("2026-07-01T11:36:44.284Z")
        );
    }

    @Test
    void parseIsoWithMillis() {
        assertEquals(
                LocalDateTime.of(2026, 7, 1, 11, 36, 44, 284_000_000),
                DateTimeParser.parse("2026-07-01T11:36:44.284")
        );
    }

    @Test
    void parseIsoWithZ() {
        assertEquals(
                LocalDateTime.of(2026, 7, 1, 11, 36, 44),
                DateTimeParser.parse("2026-07-01T11:36:44Z")
        );
    }

    @Test
    void parseIsoWithoutMillisOrZ() {
        assertEquals(
                LocalDateTime.of(2026, 7, 1, 11, 36, 44),
                DateTimeParser.parse("2026-07-01T11:36:44")
        );
    }

    @Test
    void parseDateOnly() {
        assertEquals(
                LocalDateTime.of(2026, 7, 1, 0, 0),
                DateTimeParser.parse("2026-07-01")
        );
    }

    @Test
    void parseNullReturnsNull() {
        assertNull(DateTimeParser.parse(null));
    }

    @Test
    void parseBlankReturnsNull() {
        assertNull(DateTimeParser.parse("  "));
    }

    @Test
    void parseInvalidReturnsNull() {
        assertNull(DateTimeParser.parse("not-a-date"));
    }
}
