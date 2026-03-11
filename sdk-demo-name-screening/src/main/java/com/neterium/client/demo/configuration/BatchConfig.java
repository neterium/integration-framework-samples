package com.neterium.client.demo.configuration;

import com.neterium.client.demo.batch.feeding.CounterPartDbWriter;
import com.neterium.client.demo.batch.feeding.CounterPartFileReader;
import com.neterium.client.demo.batch.feeding.CounterPartTransformer;
import com.neterium.client.demo.batch.feeding.ImportStepExecutionListener;
import com.neterium.client.demo.batch.screening.CounterPartDbReader;
import com.neterium.client.demo.batch.screening.CounterPartToScreenerWriter;
import com.neterium.client.demo.batch.screening.ScreeningResultToCounterpartWriter;
import com.neterium.client.demo.batch.screening.ScreeningResultToMatchWriter;
import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.batch.screening.ScreeningPreProcessor;
import com.neterium.client.sdk.batch.screening.ScreeningTuple;
import com.neterium.client.sdk.batch.support.BasicPartitioner;
import com.neterium.client.sdk.batch.support.NeteriumBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.neterium.client.demo.batch.support.JobParamsSupport.PARAM_SCREEN_AFTER_IMPORT;


/**
 * Configuration of SpringBatch jobs
 *
 * @author Bernard Ligny
 */
@Configuration(proxyBeanMethods = false)
public class BatchConfig {

    private final NeteriumBuilder neteriumBuilder;

    @Value("${demo.jobs.chunk-size:50}")
    private int chunkSize;

    @Value("${demo.jobs.grid-size:10}")
    private int gridSize;

    @Value("${demo.screening.batch-size:50}")
    private int screeningBatchSize;

    @Value("${demo.jobs.partitioning-width:26}")
    private int partitionKeyWidth;


    public BatchConfig(NeteriumBuilder neteriumBuilder) {
        this.neteriumBuilder = neteriumBuilder;
    }


    // === Job#1 : Read a flat file, and import records into MongoDB ===

    @Bean
    public Job importJob(Step importStepMaster,
                         Step screeningJobAsStep,
                         JobExecutionDecider chainWithScreeningJobDecider) {
        return neteriumBuilder.jobBuilder("ImportJob")
                .start(importStepMaster)
                .next(chainWithScreeningJobDecider)
                .on("CONTINUE").to(screeningJobAsStep)
                .from(chainWithScreeningJobDecider)
                .on("*").end("COMPLETED")
                .end()
                .build();
    }


    @Bean
    public Step importStepMaster(Partitioner counterPartFilePartitioner,
                                 Step importStepWorker,
                                 ImportStepExecutionListener importListener) {
        return neteriumBuilder.partitionedStepBuilder("importStep.master",
                        gridSize, counterPartFilePartitioner, importStepWorker)
                .listener(importListener)
                .build();
    }


    @Bean
    public Step importStepWorker(CounterPartFileReader reader,
                                 CounterPartTransformer transformer,
                                 CounterPartDbWriter writer) {
        return neteriumBuilder.workerStepBuilder("importStep.worker",
                        chunkSize, reader, transformer, writer)
                .build();
    }


    @Bean
    public Step screeningJobAsStep(JobRepository jobRepository,
                                   Job screeningJob) {
        return new StepBuilder("screeningJobStep", jobRepository)
                .job(screeningJob)
                .build();
    }


    // === Job#2 : Screen whole database using the screening API, and store results into db ===

    @Bean
    public Job screeningJob(Step screeningStepMaster) {
        return neteriumBuilder.sessionAwareJobBuilder("ScreeningJob")
                .start(screeningStepMaster)
                .build();
    }


    @Bean
    public Step screeningStepMaster(Partitioner screenStepPartitioner,
                                    Step screeningStepWorker) {
        return neteriumBuilder.partitionedStepBuilder("screeningStep.master",
                        gridSize, screenStepPartitioner, screeningStepWorker)
                .build();
    }


    @Bean
    public Step screeningStepWorker(CounterPartDbReader counterPartReader,
                                    ItemProcessor<Counterpart, ScreeningTuple<Counterpart>> counterPartProcessor,
                                    ItemWriter<ScreeningTuple<Counterpart>> compositeWriter,
                                    CounterPartToScreenerWriter listener) {
        return neteriumBuilder.workerStepBuilder("screeningStep.worker",
                        screeningBatchSize, counterPartReader, counterPartProcessor, compositeWriter)
                .listener(listener)
                .build();
    }


    // === Misc ===


    @Bean
    public Partitioner screenStepPartitioner() {
        return new BasicPartitioner(CounterPartDbReader.class, partitionKeyWidth);
    }


    @Bean("counterPartProcessor")
    public ItemProcessor<Counterpart, ScreeningTuple<Counterpart>> prepareTuples() {
        return new ScreeningPreProcessor<>();
    }


    @Bean
    public ItemWriter<ScreeningTuple<Counterpart>> compositeWriter(
            CounterPartToScreenerWriter writer1,
            ScreeningResultToCounterpartWriter writer2,
            ScreeningResultToMatchWriter writer3) {
        return new CompositeItemWriter<>(writer1, writer2, writer3);
    }


    @Bean
    public JobExecutionDecider chainWithScreeningJobDecider() {
        return (jobExecution, stepExecution) -> {
            String paramValue = jobExecution.getJobParameters().getString(PARAM_SCREEN_AFTER_IMPORT, "true");
            boolean doContinue = Boolean.parseBoolean(paramValue);
            return new FlowExecutionStatus((doContinue ? "CONTINUE" : "STOP"));
        };
    }

}
