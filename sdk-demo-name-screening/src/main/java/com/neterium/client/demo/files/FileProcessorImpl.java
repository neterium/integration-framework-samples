package com.neterium.client.demo.files;

import com.neterium.client.sdk.files.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static com.neterium.client.demo.batch.support.JobParamsSupport.*;

/**
 * FileProcessorImpl
 *
 * @author Bernard Ligny
 */
@Component
@Slf4j
public class FileProcessorImpl implements FileProcessor {

    private final JobLauncher jobLauncher;
    private final Job importJob;

    @Value("${demo.screening.collection:sanctions}")
    private String screeningCollection;

    @Value("${demo.screening.threshold:85}")
    private long threshold;


    public FileProcessorImpl(@Qualifier("async") JobLauncher jobLauncher,
                             Job importJob) {
        this.jobLauncher = jobLauncher;
        this.importJob = importJob;
    }


    @Override
    public void process(Path file) {
        log.info("Processing file {} ", file);
        try {
            var jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString(PARAM_DATA_FILE, file.toAbsolutePath().toString())
                    .addString(PARAM_SCREENING_COLLECTION, screeningCollection)
                    .addLong(PARAM_SCREENING_THRESHOLD, threshold)
                    .toJobParameters();
            jobLauncher.run(importJob, jobParameters);
        } catch (Exception e) {
            log.error("Error while launching job", e);
        }
    }

}
