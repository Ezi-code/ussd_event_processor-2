package com.ussd_event_processor.mapper;

import com.ussd_event_processor.entity.CallDetailRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@Slf4j
public class CdrMapper {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

    public CallDetailRecord mapToEntity(String line) {
        String[] fields = line.split("\\|", -1);

        if (fields.length < 33) {
            throw new IllegalArgumentException("Invalid number of fields: " + fields.length);
        }

        CallDetailRecord record = new CallDetailRecord();

        try {
            record.setTstamp(parseTimestamp(fields[0].trim()));
            if (record.getTstamp() == null) {
                throw new IllegalArgumentException("Invalid tstamp: " + fields[0].trim());
            }

            record.setServiceCode(defaultString(fields[4]));
            record.setOrDigits(defaultString(fields[5]));
            record.setDeDigits(defaultString(fields[10]));
            record.setUssdString(defaultString(fields[11]));
            record.setMsisdn(defaultString(fields[14]));
            record.setImsi(defaultString(fields[17]));
            record.setStatus(defaultString(fields[25]));
            record.setType(defaultString(fields[26]));

            LocalDateTime parsedRecordDate = parseTimestamp(fields[27].trim());
            record.setRecordDate(parsedRecordDate != null ? parsedRecordDate : record.getTstamp());

            record.setDialogDuration(defaultLong(fields[28]));
            record.setTransactionId(defaultString(fields[32]));

        } catch (Exception e) {
            throw new RuntimeException("Failed to map fields", e);
        }

        return record;
    }

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

    public Integer parseInteger(String value) {
        return value == null || value.trim().isEmpty() ? null : Integer.parseInt(value.trim());
    }

    public Long parseLong(String value) {
        return value == null || value.trim().isEmpty() ? null : Long.parseLong(value.trim());
    }

    private Integer defaultInteger(String value) {
        Integer parsed = parseInteger(value);
        return parsed != null ? parsed : 0;
    }

    private Long defaultLong(String value) {
        Long parsed = parseLong(value);
        return parsed != null ? parsed : 0L;
    }

    private String defaultString(String value) {
        return value == null || value.trim().isEmpty() ? "" : value.trim();
    }
}