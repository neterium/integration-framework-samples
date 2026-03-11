package com.neterium.client.demo.batch;

import com.neterium.client.sdk.batch.support.LinearPartitioner;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.neterium.client.demo.batch.ScreeningParameters.PARAM_GENERATE_RECORD_COUNT;


/**
 * Create the necessary partitions in order to process the desired number of transactions
 *
 * @author Bernard Ligny
 */
@Component
@JobScope
public class TransactionPartitioner extends LinearPartitioner {


    public TransactionPartitioner(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        super(TransactionReader.class);
        super.setItemCountProvider(() -> totalGeneratedRecords(jobParameters));
    }

    private Integer totalGeneratedRecords(Map<String, Object> jobParameters) {
        var count = (long) jobParameters.get(PARAM_GENERATE_RECORD_COUNT);
        return Math.toIntExact(count);
    }

}
