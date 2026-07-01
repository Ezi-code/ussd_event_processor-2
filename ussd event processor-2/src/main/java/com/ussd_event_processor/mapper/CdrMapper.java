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

    public CallDetailRecord mapToEntity(String line, String fileName) {
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

            record.setServiceCode(fields[4].trim());
            record.setOrDigits(fields[5].trim());
            record.setDeDigits(fields[10].trim());
            record.setUssdString(fields[11].trim());
            record.setMsisdn(fields[14].trim());
            record.setImsi(fields[17].trim());
            record.setStatus(fields[25].trim());
            record.setType(fields[26].trim());

            LocalDateTime parsedRecordDate = parseTimestamp(fields[27].trim());
            record.setRecordDate(parsedRecordDate != null ? parsedRecordDate : record.getTstamp());

            record.setDialogDuration(parseLong(fields[28]));
            record.setTransactionId(fields[32].trim());
            record.setFileName(fileName);

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
}
