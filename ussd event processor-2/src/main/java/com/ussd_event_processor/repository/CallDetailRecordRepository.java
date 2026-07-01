package com.ussd_event_processor.repository;

import com.ussd_event_processor.entity.CallDetailRecord;
import com.ussd_event_processor.dto.CdrQueryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link CallDetailRecord} entities.
 * Provides standard CRUD operations and custom query methods.
 */
public interface CallDetailRecordRepository extends JpaRepository<CallDetailRecord, UUID> {

    @Query("""
            SELECT new com.ussd_event_processor.dto.CdrQueryResponse(
                c.recordDate, c.msisdn, c.imsi
            )
            FROM CallDetailRecord c
            WHERE c.recordDate >= :recordDateStart
              AND c.recordDate <= :recordDateEnd
              AND (:msisdn IS NULL OR c.msisdn = :msisdn)
              AND (:imsi IS NULL OR c.imsi = :imsi)
            ORDER BY c.recordDate
            """)
    List<CdrQueryResponse> findRecords(
            @Param("recordDateStart") LocalDateTime recordDateStart,
            @Param("recordDateEnd") LocalDateTime recordDateEnd,
            @Param("msisdn") String msisdn,
            @Param("imsi") String imsi
    );
}