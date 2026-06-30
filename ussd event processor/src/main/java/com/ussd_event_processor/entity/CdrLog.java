package com.ussd_event_processor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Entity for tracking the processing of CDR files.
 * Stores metadata about each file ingestion, including file name,
 * processing duration, and success/failure statistics.
 */
@Entity
@Table(name = "cdr_log")
@Getter
@Setter
@NoArgsConstructor
public class CdrLog {

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
    private Integer recordsLoaded;

    @Column(name = "records_failed", nullable = false)
    private Integer recordsFailed;
}