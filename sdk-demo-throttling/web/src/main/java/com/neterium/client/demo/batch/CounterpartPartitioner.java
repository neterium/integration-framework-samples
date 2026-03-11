package com.neterium.client.demo.batch;

import com.neterium.client.sdk.batch.support.LinearPartitioner;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.neterium.client.demo.batch.ScreeningParameters.PARAM_GENERATE_RECORD_COUNT;


/**
 * Create the necessary partitions in order to process the desired number of counterparts
 *
 * @author Bernard Ligny
 */
@Component
@JobScope
public class CounterpartPartitioner extends LinearPartitioner {

    @Value("${demo.jobs.min-partition-size:500}")
    private int minPartitionSize;

    public CounterpartPartitioner(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        super(CounterpartReader.class);
        super.setItemCountProvider(() -> totalGeneratedRecords(jobParameters));
    }

    @Override
    protected int getMinPartitionSize() {
        return minPartitionSize;
    }

    private Integer totalGeneratedRecords(Map<String, Object> jobParameters) {
        var count = (long) jobParameters.get(PARAM_GENERATE_RECORD_COUNT);
        return Math.toIntExact(count);
    }

}
