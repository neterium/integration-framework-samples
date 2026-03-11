package com.neterium.client.demo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.neterium.client.demo.batch.ScreeningParameters.*;

/**
 * ScreeningController
 *
 * @author Bernard Ligny
 */
@RestController
@RequestMapping("/screen")
@Slf4j
public class ScreeningController {

    private final JobLauncher jobLauncher;
    private final Job screeningJob;


    public ScreeningController(@Qualifier("async") JobLauncher jobLauncher,
                               Job screeningJob) {
        this.jobLauncher = jobLauncher;
        this.screeningJob = screeningJob;
    }


    @GetMapping(value = "/start", produces = MediaType.TEXT_PLAIN_VALUE)
    public String doScreen(@RequestParam(value = "nbRecords") Long nbRecords,
                           @RequestParam(value = "hitRatio", required = false, defaultValue = "0.5") Double hitRatio,
                           @RequestParam(value = "collection", required = false, defaultValue = "sanctions") String collectionName,
                           @RequestParam(value = "threshold", required = false, defaultValue = "85") long threshold,
                           @RequestParam(value = "batchSize", required = false, defaultValue = "100") long batchSize)
            throws JobExecutionException {
        var jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString(PARAM_SCREENING_COLLECTION, collectionName)
                .addLong(PARAM_SCREENING_THRESHOLD, threshold)
                .addLong(PARAM_GENERATE_RECORD_COUNT, nbRecords)
                .addDouble(PARAM_GENERATE_HIT_RATIO, hitRatio)
                .addLong(PARAM_BATCH_SIZE, batchSize)
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
