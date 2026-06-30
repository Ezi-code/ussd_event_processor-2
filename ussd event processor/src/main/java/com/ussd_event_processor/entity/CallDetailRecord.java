package com.ussd_event_processor.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Call Detail Record (CDR) for USSD events.
 * Maps to the {@code call_detail_records} table in the {@code ussd} schema.
 */
@Entity
@Table(name = "call_detail_records", schema = "ussd")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallDetailRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "lac")
    private Integer lac;

    @Column(name = "cell_id")
    private Integer cellId;

    @Column(name = "event_type")
    private Integer eventType;

    @Column(name = "service_type")
    private Integer serviceType;

    @Column(name = "originating_msisdn", length = 20)
    private String originatingMsisdn;

    @Column(name = "optional_field", length = 50)
    private String optionalField; // Not sure of the name of this field

    @Column(name = "protocol_version")
    private Integer protocolVersion;

    @Column(name = "status_code1")
    private Integer statusCode1;

    @Column(name = "status_code2")
    private Integer statusCode2;

    @Column(name = "destination_msisdn", length = 20)
    private String destinationMsisdn;

    @Column(name = "ussd_string", length = 50)
    private String ussdString;

    @Column(name = "flag1")
    private Integer flag1;

    @Column(name = "flag2")
    private Integer flag2;

    @Column(name = "msisdn", length = 20)           // Key for Query API
    private String msisdn;

    @Column(name = "flag3")
    private Integer flag3;

    @Column(name = "mcc_mnc")
    private Integer mccMnc;

    @Column(name = "imsi", length = 20)             // Key for Query API
    private String imsi;

    @Column(name = "flag4")
    private Integer flag4;

    @Column(name = "flag5")
    private Integer flag5;

    @Column(name = "third_party_msisdn", length = 20)
    private String thirdPartyMsisdn;

    @Column(name = "reserved1", length = 255)
    private String reserved1;

    @Column(name = "reserved2", length = 255)
    private String reserved2;

    @Column(name = "reserved3", length = 255)
    private String reserved3;

    @Column(name = "reserved4", length = 255)
    private String reserved4;

    @Column(name = "result", length = 255)
    private String result;

    @Column(name = "session_type", length = 20)
    private String sessionType;

    @Column(name = "record_date", nullable = false) // Key for Query API
    private LocalDateTime recordDate;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "bytes_sent")
    private Long bytesSent;

    @Column(name = "bytes_received")
    private Long bytesReceived;

    @Column(name = "metrics", length = 255)
    private String metrics;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}