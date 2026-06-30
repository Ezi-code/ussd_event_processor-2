package com.ussd_event_processor.services;

import com.ussd_event_processor.entity.CallDetailRecord;
import com.ussd_event_processor.entity.CdrLog;
import com.ussd_event_processor.mapper.CdrMapper;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import com.ussd_event_processor.repository.CdrLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for monitoring a specific folder for new CDR files and
 * orchestrating their ingestion into the database.
 *
 * <p>It utilizes Spring Scheduling to poll the watch folder at a regular interval.
 * Files are processed in batches and moved to a 'processed' directory upon completion.</p>
 */
@Service
@Slf4j
public class FileWatcherService {

    @Value("${cdr.watch-folder}")
    private String watchFolder;

    @Value("${cdr.processed-folder}")
    private String processedFolder;

    private final CallDetailRecordRepository callDetailRepository;
    private final CdrLogRepository cdrLogRepository;
    private final CdrMapper cdrMapper;

    public FileWatcherService(CallDetailRecordRepository callDetailRepository,
                              CdrLogRepository cdrLogRepository,
                              CdrMapper cdrMapper) {
        this.callDetailRepository = callDetailRepository;
        this.cdrLogRepository = cdrLogRepository;
        this.cdrMapper = cdrMapper;
    }

    /**
     * Scheduled task that polls the watch folder for new files.
     * The polling rate is configured via {@code cdr.poll-rate-ms}.
     */
    @Scheduled(fixedRateString = "${cdr.poll-rate-ms:60000}")
    @Transactional
    public void pollFolder() {
        /* Poll the watch folder for new files.*/

        File folder = new File(watchFolder);
        File[] files = folder.listFiles(File::isFile);

        if (files == null || files.length == 0) {
            log.debug("No files found in watch folder: {}", watchFolder);
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                if (cdrLogRepository.existsByFileName(file.getName())) {
                    log.info("File {} already processed, moving to processed folder", file.getName());
                    try {
                        moveToProcessed(file);
                    } catch (IOException e) {
                        log.error("Failed to move already processed file: {}", file.getName(), e);
                    }
                    continue;
                }
                processFile(file);
            }
        }
    }

    /**
     * Processes a single CDR file.
     * Reads the file line by line, maps each line to an entity, and saves them in batches.
     *
     * @param file The CDR file to process.
     */
    @Transactional
    public void processFile(File file) {

        // Check if the file already exists in the cdr_log table
        if (cdrLogRepository.existsByFileName(file.getName())) {
            log.warn("Skipping file {}: already exists in cdr_log", file.getName());
            return;
        }

        LocalDateTime startTime = LocalDateTime.now();
        int successCount = 0;
        int failedCount = 0;
        List<CallDetailRecord> batch = new ArrayList<>();

        log.info("Starting processing of file: {}", file.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                try {
                    CallDetailRecord record = cdrMapper.mapToEntity(line, file.getName());
                    batch.add(record);
                    successCount++;

                    if (batch.size() >= 1000) {
                        callDetailRepository.saveAll(batch);
                        batch.clear();
                    }
                } catch (Exception e) {
                    failedCount++;
                    log.warn("Failed to parse line {} in {}: {}", lineNumber, file.getName(), e.getMessage());
                }
            }

            if (!batch.isEmpty()) {
                callDetailRepository.saveAll(batch);
            }

            recordProcessingLog(file.getName(), startTime, successCount, failedCount);
            moveToProcessed(file);
            log.info("Successfully processed {}: {} records loaded, {} failed",
                    file.getName(), successCount, failedCount);

        } catch (IOException e) {
            log.error("Error reading file: {}", file.getName(), e);
        }
    }

    /**
     * Moves the processed file to the configured processed folder.
     *
     * @param file The file to move.
     * @throws IOException If moving the file fails.
     */
    private void moveToProcessed(File file) throws IOException {
        Path target = Paths.get(processedFolder, file.getName());
        Files.move(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        log.debug("Moved file to processed folder: {}", file.getName());
    }

    /**
     * Records the processing results (success/failure counts, timestamps) in the {@code cdr_log} table.
     *
     * @param fileName  The name of the processed file.
     * @param startTime The time processing started.
     * @param success   Number of successfully loaded records.
     * @param failed    Number of failed records.
     */
    private void recordProcessingLog(
            String fileName, LocalDateTime startTime, int success, int failed)
    {
        CdrLog logEntry = new CdrLog();
        logEntry.setFileName(fileName);
        logEntry.setUploadStartTime(startTime);
        logEntry.setUploadEndTime(LocalDateTime.now());
        logEntry.setRecordsLoaded(success);
        logEntry.setRecordsFailed(failed);

        cdrLogRepository.save(logEntry);
    }

    /**
     * Deletes all records from the {@code call_detail_records} table.
     * Used for system maintenance or data reset.
     */
    @Transactional
    public void deleteAllRecords() {
        callDetailRepository.deleteAll();
        log.info("All records deleted from call_detail_records");
    }
}