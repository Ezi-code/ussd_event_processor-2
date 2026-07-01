package com.ussd_event_processor.controller;

import com.ussd_event_processor.dto.CdrQueryRequest;
import com.ussd_event_processor.dto.CdrQueryResponse;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cdrs")
public class CdrQueryController {

    private final CallDetailRecordRepository repository;

    public CdrQueryController(CallDetailRecordRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/query")
    public ResponseEntity<List<CdrQueryResponse>> queryRecords(@RequestBody CdrQueryRequest request) {
        if (request.recordDateStart() == null || request.recordDateEnd() == null) {
            return ResponseEntity.badRequest().build();
        }
        List<CdrQueryResponse> results = repository.findRecords(
                request.recordDateStart(),
                request.recordDateEnd(),
                request.msisdn(),
                request.imsi()
        );
        return ResponseEntity.ok(results);
    }
}
