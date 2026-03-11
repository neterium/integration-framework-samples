package com.neterium.client.demo.controllers;

import com.neterium.client.demo.files.DataFile;
import com.neterium.client.sdk.files.FileService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.neterium.client.demo.batch.support.JobParamsSupport.*;

/**
 * ScreeningController
 *
 * @author Bernard Ligny
 */
@RestController
@RequestMapping("/rest/screen")
@Slf4j
public class ScreeningController {

    private final JobLauncher jobLauncher;
    private final Job importJob;
    private final Job screeningJob;
    private final FileService fileService;

    @Value("${demo.polling.directory}")
    private Path rootDirectory;

    @Value("${demo.screening.threshold:85}")
    private long threshold;


    public ScreeningController(@Qualifier("async") JobLauncher jobLauncher,
                               Job importJob,
                               Job screeningJob,
                               FileService fileService) {
        this.jobLauncher = jobLauncher;
        this.importJob = importJob;
        this.screeningJob = screeningJob;
        this.fileService = fileService;
    }


    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(rootDirectory.resolve(DataFile.UPLOAD_DIRECTORY));
    }


    @PostMapping(value = "/import-file", produces = MediaType.TEXT_PLAIN_VALUE)
    public String importFile(@RequestParam(value = "file") MultipartFile file,
                             @RequestParam(value = "collection", required = false, defaultValue = "sanctions") String collectionName,
                             @RequestParam(value = "chainWithScreening", required = false, defaultValue = "false") Boolean chainWithScreening)
            throws IOException, JobExecutionException {
        var inputFile = rootDirectory.resolve(DataFile.UPLOAD_DIRECTORY)
                .resolve(file.getOriginalFilename())
                .normalize();
        fileService.saveToFile(file.getInputStream(), inputFile);
        var jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString(PARAM_SCREENING_COLLECTION, collectionName)
                .addString(PARAM_DATA_FILE, inputFile.toAbsolutePath().toString())
                .addString(PARAM_SCREEN_AFTER_IMPORT, chainWithScreening.toString())
                .addLong(PARAM_SCREENING_THRESHOLD, threshold)
                .toJobParameters();
        return startJob(importJob, jobParameters).toString();
    }


    @GetMapping(value = "/database", produces = MediaType.TEXT_PLAIN_VALUE)
    public String screenWholeDatabase(@RequestParam(value = "collection", required = false, defaultValue = "sanctions") String collectionName,
                                      @RequestParam(value = "threshold", required = false, defaultValue = "70L") long threshold)
            throws JobExecutionException {
        var jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString(PARAM_SCREENING_COLLECTION, collectionName)
                .addLong(PARAM_SCREENING_THRESHOLD, threshold)
                .toJobParameters();
        return startJob(screeningJob, jobParameters).toString();
    }


    private JobExecution startJob(Job job, JobParameters jobParameters) throws JobExecutionException {
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        log.debug("JobExecution::: {}", jobExecution);
        if (jobExecution.getStatus().isRunning()) {
            log.info("Job {} successfully started", jobExecution.getJobInstance().getJobName());
        }
        return jobExecution;
    }

}
