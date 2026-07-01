package com.ussd_event_processor.mapper;

import com.ussd_event_processor.entity.CallDetailRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Mapper component responsible for converting raw CDR (Call Detail Record) log lines
 * into {@link CallDetailRecord} JPA entities.
 *
 * <p>It handles pipe-separated values, data type conversions, and timestamp parsing
 * according to the predefined CDR format.</p>
 */
@Component
@Slf4j
public class CdrMapper {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

    /**
     * Maps a single pipe-delimited CDR line to a {@link CallDetailRecord} entity.
     *
     * @param line     The raw string line from the CDR file.
     * @param fileName The name of the file being processed, for tracking purposes.
     * @return A populated CallDetailRecord entity.
     * @throws IllegalArgumentException if the number of fields in the line is less than 33.
     * @throws RuntimeException if field mapping fails due to format issues.
     */
    public CallDetailRecord mapToEntity(String line, String fileName) {
        String[] fields = line.split("\\|", -1);

        if (fields.length < 33) {
            throw new IllegalArgumentException("Invalid number of fields: " + fields.length);
        }

        CallDetailRecord record = new CallDetailRecord();

        // based on the file format, the fields are in the following order:
        try {
            record.setEventTimestamp(parseTimestamp(fields[0].trim()));
            record.setLac(parseInteger(fields[1]));
            record.setCellId(parseInteger(fields[2]));
            record.setEventType(parseInteger(fields[3]));
            record.setServiceType(parseInteger(fields[4]));

            record.setOriginatingMsisdn(fields[5].trim());
            record.setOptionalField(fields[6].trim());
            record.setProtocolVersion(parseInteger(fields[7]));
            record.setStatusCode1(parseInteger(fields[8]));
            record.setStatusCode2(parseInteger(fields[9]));

            record.setDestinationMsisdn(fields[10].trim());
            record.setUssdString(fields[11].trim());
            record.setFlag1(parseInteger(fields[12]));
            record.setFlag2(parseInteger(fields[13]));

            record.setMsisdn(fields[14].trim());
            record.setFlag3(parseInteger(fields[15]));
            record.setMccMnc(parseInteger(fields[16]));
            record.setImsi(fields[17].trim());

            record.setFlag4(parseInteger(fields[18]));
            record.setFlag5(parseInteger(fields[19]));
            record.setThirdPartyMsisdn(fields[20].trim());

            record.setReserved1(fields[21].trim());
            record.setReserved2(fields[22].trim());
            record.setReserved3(fields[23].trim());
            record.setReserved4(fields[24].trim());

            record.setResult(fields[25].trim());
            record.setSessionType(fields[26].trim());

            LocalDateTime parsedRecordDate = parseTimestamp(fields[27].trim());
            record.setRecordDate(parsedRecordDate != null ? parsedRecordDate : record.getEventTimestamp());

            record.setDurationMs(parseLong(fields[28]));
            record.setBytesSent(parseLong(fields[29]));
            record.setBytesReceived(parseLong(fields[30]));

            record.setMetrics(fields[31].trim());
            record.setTransactionId(fields[32].trim());

            record.setFileName(fileName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to map fields", e);
        }

        return record;
    }

    /**
     * Parses a timestamp string using the standard CDR format (yyyy-MM-dd HH:mm:ss,SSS).
     *
     * @param timestampStr The raw timestamp string.
     * @return A LocalDateTime object, or null if the input is blank or unparseable.
     */
    public LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(timestampStr.trim(), TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Safely parses a string value to an Integer.
     *
     * @param value The string to parse.
     * @return The Integer value, or null if the string is empty or null.
     */
    public Integer parseInteger(String value) {
        return value == null || value.trim().isEmpty() ? null : Integer.parseInt(value.trim());
    }

    /**
     * Safely parses a string value to a Long.
     *
     * @param value The string to parse.
     * @return The Long value, or null if the string is empty or null.
     */
    public Long parseLong(String value) {
        return value == null || value.trim().isEmpty() ? null : Long.parseLong(value.trim());
    }
}