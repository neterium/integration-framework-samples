package com.neterium.client.demo.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neterium.client.demo.domain.Transaction;
import com.neterium.client.demo.domain.utils.FakeUtils;
import com.neterium.client.sdk.batch.support.LinearPartitioner;
import com.neterium.client.sdk.exception.SdkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;


/**
 * TransactionReader
 * <p>
 * "Read" (ie generate) a list of N transactions being the union of
 * - a first collection of fake/random transactions
 * - a second collection of transactions involving a black-listed party (that is likely to
 * give a positive match)
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class TransactionReader extends AbstractPaginatedDataItemReader<Transaction> {

    private final Random random = new Random();
    private final ObjectMapper mapper;

    private int pageNbr = 0;
    private int remaining;
    private String stepName;
    private double hitRatio;


    public TransactionReader(ObjectMapper mapper, @Value("${demo.jobs.page-size:50}") int pageSize) {
        super();
        super.setName(this.getClass().getSimpleName());
        super.setPageSize(pageSize);
        this.mapper = mapper;
    }


    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        remaining = getPartitionParam(stepExecution, LinearPartitioner.KEY_MAX_ITEMS, Integer.class);
        stepName = stepExecution.getStepName();
        var jobParams = new ScreeningParameters(stepExecution.getJobParameters());
        hitRatio = jobParams.getHitRatio();
    }


    @Override
    protected Iterator<Transaction> doPageRead() {
        if (remaining > 0) {
            var toGenerate = Math.min(remaining, pageSize);
            var nbHits = (int) Math.round(toGenerate * hitRatio);
            List<Transaction> page = new ArrayList<>();
            addHitRecords(page, nbHits);
            addNoHitRecords(page, toGenerate - nbHits);
            Collections.shuffle(page);
            pageNbr++;
            log.debug("Generating next {} records for {} (page {})", page.size(), stepName, pageNbr);
            remaining = remaining - page.size();
            return page.iterator();
        } else {
            return null;
        }
    }


    private void addHitRecords(List<Transaction> records, int count) {
        try {
            var array = mapper.readTree(FakeUtils.getBlackList());
            for (int i = 0; i < count; i++) {
                var index = random.nextInt(array.size());
                var randomEntry = array.get(index);
                records.add(FakeUtils.fakeTransactionFrom(randomEntry));
            }
        } catch (IOException e) {
            throw new SdkException(e);
        }
    }


    private void addNoHitRecords(List<Transaction> records, int count) {
        for (int i = 0; i < count; i++) {
            records.add(FakeUtils.fakeTransaction());
        }
    }


    private <T> T getPartitionParam(StepExecution stepExecution, String paramName, Class<T> paramType) {
        var suffix = this.getClass().getSimpleName();
        var value = stepExecution.getExecutionContext().get(suffix + paramName, paramType);
        assert (value != null);
        return value;
    }


}
