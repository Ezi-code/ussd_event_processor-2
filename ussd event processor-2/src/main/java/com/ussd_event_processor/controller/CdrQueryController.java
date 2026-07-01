package com.ussd_event_processor.controller;

import com.ussd_event_processor.dto.CdrQueryRequest;
import com.ussd_event_processor.dto.CdrQueryResponse;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import com.ussd_event_processor.util.DateTimeParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
        LocalDateTime start = DateTimeParser.parse(request.recordDateStart());
        LocalDateTime end = DateTimeParser.parse(request.recordDateEnd());
        if (start == null || end == null) {
            return ResponseEntity.badRequest().build();
        }
        List<CdrQueryResponse> results = repository.findRecords(
                start, end,
                request.msisdn(),
                request.imsi()
        );
        return ResponseEntity.ok(results);
    }
}
