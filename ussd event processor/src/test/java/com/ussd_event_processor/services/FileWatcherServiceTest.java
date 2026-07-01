package com.ussd_event_processor.services;

import com.ussd_event_processor.entity.CallDetailRecord;
import com.ussd_event_processor.entity.CdrLog;
import com.ussd_event_processor.mapper.CdrMapper;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import com.ussd_event_processor.repository.CdrLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileWatcherServiceTest {

    @Mock
    private CallDetailRecordRepository callDetailRepository;

    @Mock
    private CdrLogRepository cdrLogRepository;

    @Mock
    private CdrMapper cdrMapper;

    @Mock
    private org.springframework.context.ApplicationContext applicationContext;

    private FileWatcherService fileWatcherService;

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

        fileWatcherService = new FileWatcherService(callDetailRepository, cdrLogRepository, cdrMapper, applicationContext);
        lenient().when(applicationContext.getBean(FileWatcherService.class)).thenReturn(fileWatcherService);
        ReflectionTestUtils.setField(fileWatcherService, "watchFolder", watchFolder.toString());
        ReflectionTestUtils.setField(fileWatcherService, "processedFolder", processedFolder.toString());
    }

    @Test
    void testPollFolder_NoFiles() {
        fileWatcherService.pollFolder();
        verifyNoInteractions(cdrMapper, callDetailRepository, cdrLogRepository);
    }

    @Test
    void testProcessFile_Success() throws IOException {
        String fileName = "test.log";
        Path filePath = watchFolder.resolve(fileName);
        Files.writeString(filePath, "line1\nline2\n");

        CallDetailRecord record1 = new CallDetailRecord();
        CallDetailRecord record2 = new CallDetailRecord();

        when(cdrMapper.mapToEntity(anyString(), eq(fileName))).thenReturn(record1, record2);

        fileWatcherService.processFile(filePath.toFile());

        verify(callDetailRepository).saveAll(anyList());
        verify(cdrLogRepository).save(any(CdrLog.class));
        
        // Verify file moved
        assertThat(Files.exists(processedFolder.resolve(fileName))).isTrue();
        assertThat(Files.exists(filePath)).isFalse();

        ArgumentCaptor<CdrLog> logCaptor = ArgumentCaptor.forClass(CdrLog.class);
        verify(cdrLogRepository).save(logCaptor.capture());
        CdrLog capturedLog = logCaptor.getValue();
        assertThat(capturedLog.getFileName()).isEqualTo(fileName);
        assertThat(capturedLog.getRecordsLoaded()).isEqualTo(2);
        assertThat(capturedLog.getRecordsFailed()).isEqualTo(0);
    }

    @Test
    void testProcessFile_WithFailures() throws IOException {
        String fileName = "test_fail.log";
        Path filePath = watchFolder.resolve(fileName);
        Files.writeString(filePath, "good\nbad\n");

        CallDetailRecord record1 = new CallDetailRecord();

        when(cdrMapper.mapToEntity("good", fileName)).thenReturn(record1);
        when(cdrMapper.mapToEntity("bad", fileName)).thenThrow(new RuntimeException("Parsing failed"));

        fileWatcherService.processFile(filePath.toFile());

        verify(callDetailRepository).saveAll(anyList());
        verify(cdrLogRepository).save(any(CdrLog.class));

        ArgumentCaptor<CdrLog> logCaptor = ArgumentCaptor.forClass(CdrLog.class);
        verify(cdrLogRepository).save(logCaptor.capture());
        CdrLog capturedLog = logCaptor.getValue();

        assertThat(capturedLog.getRecordsLoaded()).isEqualTo(1);
        assertThat(capturedLog.getRecordsFailed()).isEqualTo(1);
    }
}