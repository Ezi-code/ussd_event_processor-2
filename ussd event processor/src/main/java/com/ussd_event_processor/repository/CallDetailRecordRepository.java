package com.ussd_event_processor.repository;

import com.ussd_event_processor.entity.CallDetailRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for {@link CallDetailRecord} entities.
 * Provides standard CRUD operations and custom query methods.
 */
public interface CallDetailRecordRepository extends JpaRepository<CallDetailRecord, UUID> {

}