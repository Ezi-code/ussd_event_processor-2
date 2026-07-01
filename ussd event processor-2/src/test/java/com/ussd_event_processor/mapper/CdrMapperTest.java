package com.ussd_event_processor.mapper;

import com.ussd_event_processor.entity.CallDetailRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CdrMapperTest {

    private CdrMapper cdrMapper;

    @BeforeEach
    void setUp() {
        cdrMapper = new CdrMapper();
    }

    @Test
    void testMapToEntity_Success() {
        String line = "2023-10-27 10:00:00,000|1|2|3|SVCCode|MSISDN1|OPT|7|8|9|DEST|STR|12|13|MSISDN2|15|16|IMSI|18|19|3RD|R1|R2|R3|R4|RES|TYPE|2023-10-27 10:05:00,000|100|1000|2000|METRIC|TXID";
        String fileName = "test.log";

        CallDetailRecord record = cdrMapper.mapToEntity(line, fileName);

        assertThat(record).isNotNull();
        assertThat(record.getTstamp()).isEqualTo(LocalDateTime.of(2023, 10, 27, 10, 0, 0));
        assertThat(record.getServiceCode()).isEqualTo("SVCCode");
        assertThat(record.getOrDigits()).isEqualTo("MSISDN1");
        assertThat(record.getDeDigits()).isEqualTo("DEST");
        assertThat(record.getUssdString()).isEqualTo("STR");
        assertThat(record.getMsisdn()).isEqualTo("MSISDN2");
        assertThat(record.getImsi()).isEqualTo("IMSI");
        assertThat(record.getStatus()).isEqualTo("RES");
        assertThat(record.getType()).isEqualTo("TYPE");
        assertThat(record.getRecordDate()).isEqualTo(LocalDateTime.of(2023, 10, 27, 10, 5, 0));
        assertThat(record.getDialogDuration()).isEqualTo(100L);
        assertThat(record.getTransactionId()).isEqualTo("TXID");
        assertThat(record.getFileName()).isEqualTo(fileName);
    }

    @Test
    void testMapToEntity_InvalidFieldCount() {
        String line = "too|few|fields";
        assertThatThrownBy(() -> cdrMapper.mapToEntity(line, "test.log"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid number of fields");
    }

    @Test
    void testMapToEntity_InvalidTimestamp() {
        String line = "not_a_timestamp|1|2|3|4|MSISDN1|OPT|7|8|9|DEST|STR|12|13|MSISDN2|15|16|IMSI|18|19|3RD|R1|R2|R3|R4|RES|TYPE|2023-10-27 10:05:00,000|100|1000|2000|METRIC|TXID";
        assertThatThrownBy(() -> cdrMapper.mapToEntity(line, "test.log"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to map fields");
    }

    @Test
    void testParseTimestamp_Valid() {
        LocalDateTime dt = cdrMapper.parseTimestamp("2023-10-27 10:00:00,123");
        assertThat(dt).isEqualTo(LocalDateTime.of(2023, 10, 27, 10, 0, 0, 123_000_000));
    }

    @Test
    void testParseTimestamp_Invalid() {
        assertThat(cdrMapper.parseTimestamp("invalid")).isNull();
        assertThat(cdrMapper.parseTimestamp(null)).isNull();
        assertThat(cdrMapper.parseTimestamp("  ")).isNull();
    }

    @Test
    void testParseInteger_Valid() {
        assertThat(cdrMapper.parseInteger(" 123 ")).isEqualTo(123);
        assertThat(cdrMapper.parseInteger(null)).isNull();
        assertThat(cdrMapper.parseInteger("")).isNull();
    }

    @Test
    void testParseLong_Valid() {
        assertThat(cdrMapper.parseLong(" 123456789 ")).isEqualTo(123456789L);
        assertThat(cdrMapper.parseLong(null)).isNull();
        assertThat(cdrMapper.parseLong("")).isNull();
    }
}
