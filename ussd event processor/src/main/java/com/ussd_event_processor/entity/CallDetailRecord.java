package com.ussd_event_processor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "call_detail_records")
public class CallDetailRecord {
    /* Call Detail Record */


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "record_start_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime recordStartDateTime;

    @Column(name = "record_end_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime recordEndDateTime;

    @Column(name = "msisdn", length = 12, nullable = false)
    private String msisdn;

    @Column(name = "imsi", length = 10, nullable = true)
    private String imsi;

    @Column(name = "raw_line", nullable = true)
    private String rawLine;

    public CallDetailRecord(UUID id, LocalDateTime recordStartDateTime, LocalDateTime recordEndDateTime, String msisdn, String imsi ) {
        this.id = id;
        this.recordStartDateTime = recordStartDateTime;
        this.recordEndDateTime = recordEndDateTime;
        this.msisdn = msisdn;
        this.imsi = imsi;

    }

    public CallDetailRecord(UUID id, LocalDateTime recordStartDateTime, LocalDateTime recordEndDateTime, String msisdn) {
        this.id = UUID.randomUUID();
        this.recordStartDateTime = recordStartDateTime;
        this.recordEndDateTime = recordEndDateTime;
        this.msisdn = msisdn;
    }
}