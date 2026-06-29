package com.ussd_event_processor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Audited;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Audited.Table(name = "cdr_logs")
@Getter
@Setter
@NoArgsConstructor
public class CdrLog {
    /* CDR Log Model.*/


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "upload_start_time", nullable = false)
    private LocalDateTime uploadStartTime;

    @Column(name = "upload_end_time")
    private LocalDateTime uploadEndTime;

    @Column(name = "records_loaded", nullable = false)
    private Integer recordsLoaded = 0;

    @Column(name = "records_failed", nullable = false)
    private Integer recordsFailed = 0;
}