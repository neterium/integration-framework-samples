package com.neterium.client.demo.configuration;

import com.neterium.client.demo.batch.ScreeningParameters;
import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.batch.support.NeteriumBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;


/**
 * Configuration of SpringBatch jobs
 *
 * @author Bernard Ligny
 */
@Configuration(proxyBeanMethods = false)
@Profile("jetscan")
public class JetScanBatchConfig {

    private final NeteriumBuilder neteriumBuilder;

    @Value("${demo.jobs.grid-size:10}")
    private int gridSize;


    public JetScanBatchConfig(NeteriumBuilder neteriumBuilder) {
        this.neteriumBuilder = neteriumBuilder;
    }


    @Bean
    public Job screeningJob(Step screeningStepMaster) {
        return neteriumBuilder.sessionAwareJobBuilder("ScreeningJob")
                .start(screeningStepMaster)
                .build();
    }


    @Bean
    public Step screeningStepMaster(Partitioner counterpartPartitioner,
                                    Step screeningStepWorker) {
        return neteriumBuilder.partitionedStepBuilder("ScreeningStep",
                        gridSize,
                        counterpartPartitioner,
                        screeningStepWorker)
                .build();
    }


    @Bean
    public Step screeningStepWorker(CompletionPolicy chunkCompletionPolicy,
                                    ItemReader<Counterpart> counterpartReader,
                                    ItemWriter<Counterpart> counterpartWriter) {
        return neteriumBuilder.workerStepBuilder("ScreeningStep.worker",
                        chunkCompletionPolicy,
                        counterpartReader,
                        counterpartWriter)
                .build();
    }


    @Bean
    @StepScope
    public CompletionPolicy dynamicBatchSizePolicy(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        var batchSize = (Long) jobParameters.get(ScreeningParameters.PARAM_BATCH_SIZE);
        return new SimpleCompletionPolicy(batchSize.intValue());
    }

}
