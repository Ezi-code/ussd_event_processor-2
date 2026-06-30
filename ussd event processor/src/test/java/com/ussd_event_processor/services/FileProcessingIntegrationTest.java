package com.ussd_event_processor.services;

import com.ussd_event_processor.repository.CallDetailRecordRepository;
import com.ussd_event_processor.repository.CdrLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class FileProcessingIntegrationTest {

    @Autowired
    private FileWatcherService fileWatcherService;

    @Autowired
    private CallDetailRecordRepository cdrRepository;

    @Autowired
    private CdrLogRepository cdrLogRepository;

    @TempDir
    Path tempDir;

    private Path watchFolder;
    private Path processedFolder;

    @BeforeEach
    void setUp() throws IOException {
        watchFolder = tempDir.resolve("incoming");
        processedFolder = tempDir.resolve("processed");
        Files.createDirectories(watchFolder);
        Files.createDirectories(processedFolder);

        // Inject temp paths into the service
        ReflectionTestUtils.setField(fileWatcherService, "watchFolder", watchFolder.toString());
        ReflectionTestUtils.setField(fileWatcherService, "processedFolder", processedFolder.toString());

        cdrRepository.deleteAll();
        cdrLogRepository.deleteAll();
    }

    @Test
    void testEndToEndFileProcessing() throws IOException {
        String fileName = "test_e2e.log";
        String content = "2023-10-27 10:00:00,000|1|2|3|4|MSISDN1|OPT|7|8|9|DEST|STR|12|13|MSISDN2|15|16|IMSI|18|19|3RD|R1|R2|R3|R4|RES|TYPE|2023-10-27 10:00:00,000|100|1000|2000|METRIC|TXID\n" +
                         "2023-10-27 10:01:00,000|1|2|3|4|MSISDN1|OPT|7|8|9|DEST|STR|12|13|MSISDN2|15|16|IMSI|18|19|3RD|R1|R2|R3|R4|RES|TYPE|2023-10-27 10:01:00,000|100|1000|2000|METRIC|TXID\n";
        
        Files.writeString(watchFolder.resolve(fileName), content);

        fileWatcherService.pollFolder();

        assertThat(cdrLogRepository.count()).isEqualTo(1);
        assertThat(cdrRepository.count()).isEqualTo(2);
        assertThat(Files.exists(processedFolder.resolve(fileName))).isTrue();
        assertThat(Files.exists(watchFolder.resolve(fileName))).isFalse();
    }
}