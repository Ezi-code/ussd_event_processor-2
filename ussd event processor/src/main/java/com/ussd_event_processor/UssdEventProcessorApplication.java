package com.ussd_event_processor;

import com.ussd_event_processor.repository.CallDetailRecordRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry point for the USSD Event Processor application.
 *
 * <p>This Spring Boot application is responsible for processing USSD Call Detail Records (CDRs).
 * It enables scheduling to support folder-based file monitoring.</p>
 */
@SpringBootApplication
@EnableScheduling
public class UssdEventProcessorApplication {

    static void main(String[] args) {
        SpringApplication.run(UssdEventProcessorApplication.class, args);
    }
}