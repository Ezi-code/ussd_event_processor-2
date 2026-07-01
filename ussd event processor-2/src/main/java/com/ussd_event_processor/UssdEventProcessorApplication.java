package com.ussd_event_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Main entry point for the USSD Event Processor application.
 *
 * <p>This Spring Boot application is responsible for processing USSD Call Detail Records (CDRs).
 * It enables scheduling to support folder-based file monitoring.</p>
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class UssdEventProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UssdEventProcessorApplication.class, args);
    }


    @Bean(name = "cdrTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("CDR-Processor-");
        executor.initialize();
        return executor;
    }
}