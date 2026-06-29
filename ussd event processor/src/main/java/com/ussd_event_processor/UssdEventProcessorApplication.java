package com.ussd_event_processor;

import com.ussd_event_processor.repository.CallDetailRecordRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class UssdEventProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UssdEventProcessorApplication.class, args);
    }
}