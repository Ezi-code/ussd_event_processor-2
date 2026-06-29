package com.ussd_event_processor.services;

import com.ussd_event_processor.entity.CallDetailRecord;
import com.ussd_event_processor.entity.CdrLog;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import com.ussd_event_processor.repository.CdrLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FileWatcherService {

    @Value("${cdr.watch-folder}")
    private String watchFolder;

    @Value("${cdr.processed-folder}")
    private String processedFolder;

    private final CallDetailRecordRepository callDetailRepository;
    private final CdrLogRepository cdrLogRepository;

    public FileWatcherService(CallDetailRecordRepository callDetailRepository, CdrLogRepository cdrLogRepository) {
        this.callDetailRepository = callDetailRepository;
        this.cdrLogRepository = cdrLogRepository;
    }

    @Scheduled(fixedRateString = "${cdr.poll-rate-ms}")
    public void pollFolder(){
        File folder = new File(watchFolder);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log.info("No files found in the folder");
            return;
        }

        for (File file : files) {
            processFile(file);
        }
    }

    private void processFile(File file){
        LocalDateTime start = LocalDateTime.now();
        int success = 0;
        int failed = 0;
        List<CallDetailRecord> batch = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    batch.add(parseLine(line));
                    success++;
                } catch (Exception e) {
                    log.warn("Skipping malformed line in {}: {}",
                            file.getName(), e.getMessage());
                    failed++;
                }
            }
            callDetailRepository.saveAll(batch);
            moveToProcessed(file);
        } catch (IOException e) {
            log.error("Could not read file {}", file.getName(), e);
        }
        recordOutcome(file.getName(), start, success, failed);
    }

    private CallDetailRecord parseLine(String line) {
        String[] f = line.split("\\|", -1);
        CallDetailRecord record = new CallDetailRecord();

        record.setRecordStartDateTime(
                LocalDateTime.parse(f[0].trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        record.setMsisdn(f[1].trim());
        record.setImsi(f[2].trim());
        record.setRawLine(line);
        return record;
    }
    private void moveToProcessed(File file) throws IOException {
        Path target = Paths.get(processedFolder, file.getName());
        Files.move(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    }
    private void recordOutcome(
            String fileName, LocalDateTime start,
            int success, int failed) {
        CdrLog entry = new CdrLog();
        entry.setFileName(fileName);
        entry.setUploadStartTime(start);
        entry.setUploadEndTime(LocalDateTime.now());
        entry.setRecordsLoaded(success);
        entry.setRecordsFailed(failed);
        cdrLogRepository.save(entry);
        log.info("Processed {}: {} loaded, {} failed", fileName, success, failed);
    }
}