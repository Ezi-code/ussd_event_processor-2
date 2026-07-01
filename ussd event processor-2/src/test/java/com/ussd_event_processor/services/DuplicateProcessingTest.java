package com.ussd_event_processor.services;

import com.ussd_event_processor.repository.CallDetailRecordRepository;
import com.ussd_event_processor.repository.CdrLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DuplicateProcessingTest {

    @Autowired
    private FileWatcherService fileWatcherService;

    @Autowired
    private CallDetailRecordRepository cdrRepository;

    @Autowired
    private CdrLogRepository cdrLogRepository;

    @Value("${cdr.watch-folder}")
    private String watchFolder;

    @Value("${cdr.processed-folder}")
    private String processedFolder;

    private String testFileName = "test_duplicate.log";
    private String cdrLine = "2023-10-27 10:00:00,000|1|2|3|4|MSISDN1|OPT|1|0|0|DEST1|USSD|0|0|MSISDN2|0|123|IMSI1|0|0|3RD1|R1|R2|R3|R4|SUCCESS|TYPE|2023-10-27 10:00:00,000|100|50|50|METRIC|TX1";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(watchFolder));
        Files.createDirectories(Paths.get(processedFolder));
        cdrRepository.deleteAll();
        cdrLogRepository.deleteAll();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(watchFolder, testFileName));
        Files.deleteIfExists(Paths.get(processedFolder, testFileName));
    }

    @Test
    void testDuplicateFileProcessing() throws IOException {
        // Use a unique name for each test run to avoid Flyway migration issues with existing data
        String uniqueFileName = "test_duplicate_" + System.currentTimeMillis() + ".log";

        // 1. Create a file in watch folder
        File file = new File(watchFolder, uniqueFileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(cdrLine);
        }

        // 2. Process it once
        fileWatcherService.processFile(file);

        assertEquals(1, cdrRepository.count(), "Should have 1 record after first processing");
        assertEquals(1, cdrLogRepository.count(), "Should have 1 log after first processing");

        // 3. Put it back in watch folder
        File file2 = new File(watchFolder, uniqueFileName);
        if (!file2.exists()) {
            try (FileWriter writer = new FileWriter(file2)) {
                writer.write(cdrLine);
            }
        }

        // 4. Process it again
        fileWatcherService.processFile(file2);

        assertEquals(1, cdrRepository.count(), "Should STILL have only 1 record after duplicate processing");
        assertEquals(1, cdrLogRepository.count(), "Should STILL have only 1 log after duplicate processing");

        Files.deleteIfExists(file2.toPath());
        Files.deleteIfExists(Paths.get(processedFolder, uniqueFileName));
    }
}