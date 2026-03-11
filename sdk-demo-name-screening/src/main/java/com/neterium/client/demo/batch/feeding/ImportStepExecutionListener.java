package com.neterium.client.demo.batch.feeding;

import com.neterium.client.demo.batch.support.JobParamsSupport;
import com.neterium.client.demo.files.DataFile;
import com.neterium.client.sdk.files.FileService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ImportStepExecutionListener
 *
 * @author Bernard Ligny
 */
@Component
@Slf4j
public class ImportStepExecutionListener implements StepExecutionListener {

    private final FileService fileService;

    @Value("${demo.polling.directory}")
    private Path rootDirectory;


    public ImportStepExecutionListener(FileService fileService) {
        this.fileService = fileService;
    }


    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(rootDirectory.resolve(DataFile.RUNNING_DIRECTORY));
        Files.createDirectories(rootDirectory.resolve(DataFile.ERROR_DIRECTORY));
        Files.createDirectories(rootDirectory.resolve(DataFile.DONE_DIRECTORY));
    }


    /**
     * Move file to running dir when import is started
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        var dataFile = this.getDataFile(stepExecution);
        log.info("Starting {} for file {}", stepExecution.getStepName(), dataFile.getInputFile());
        fileService.moveFile(dataFile.getInputFile(), dataFile.getRunningFile());
    }


    /**
     * Move file to appropriate dir depending on step execution status
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        var dataFile = this.getDataFile(stepExecution);
        if (stepExecution.getStatus().isUnsuccessful()) {
            var firstException = stepExecution.getFailureExceptions()
                    .stream()
                    .findFirst()
                    .orElse(null);
            log.error("Failed execution of '{}' for file '{}'", stepExecution.getStepName(),
                    dataFile.getInputFile().getFileName(),
                    firstException);
            fileService.moveFile(dataFile.getRunningFile(), dataFile.getParkedFile());
        } else {
            log.info("Successful execution of '{}' for file '{}' - Read={} | Write={} | Skip={}",
                    stepExecution.getStepName(),
                    dataFile.getInputFile().getFileName(),
                    stepExecution.getReadCount(),
                    stepExecution.getWriteCount(),
                    stepExecution.getSkipCount());
            fileService.moveFile(dataFile.getRunningFile(), dataFile.getArchivedFile());
        }
        return null;
    }


    private DataFile getDataFile(StepExecution stepExecution) {
        var jobParams = new JobParamsSupport(stepExecution.getJobExecution().getJobParameters());
        return jobParams.getDataFile();
    }

}
