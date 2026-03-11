package com.neterium.client.demo.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neterium.client.demo.domain.Counterpart;
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
 * CounterpartReader
 * <p>
 * "Read" (ie generate) a list of N counterparts being the union of
 * - a first collection of fake/random counterparts
 * - a second collection of black-listed counterparts (that are likely to give a positive match)
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class CounterpartReader extends AbstractPaginatedDataItemReader<Counterpart> {

    private final Random random = new Random();
    private final ObjectMapper mapper;

    private int pageNbr = 0;
    private int remaining;
    private String stepName;
    private double hitRatio;


    public CounterpartReader(ObjectMapper mapper, @Value("${demo.jobs.page-size:50}") int pageSize) {
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
    protected Iterator<Counterpart> doPageRead() {
        if (remaining > 0) {
            var toGenerate = Math.min(remaining, pageSize);
            var nbWithHits = (int) Math.round(toGenerate * hitRatio);
            List<Counterpart> page = new ArrayList<>();
            addHitRecords(page, nbWithHits);
            addNoHitRecords(page, toGenerate - nbWithHits);
            Collections.shuffle(page);
            pageNbr++;
            log.debug("Generating next {} records for {} (page {})", page.size(), stepName, pageNbr);
            remaining = remaining - page.size();
            return page.iterator();
        } else {
            return null;
        }
    }


    private void addNoHitRecords(List<Counterpart> records, int count) {
        for (int i = 0; i < count; i++) {
            records.add(FakeUtils.fakeIndividual());
        }
    }


    private void addHitRecords(List<Counterpart> records, int count) {
        try {
            var array = mapper.readTree(FakeUtils.getBlackList());
            for (int i = 0; i < count; i++) {
                var index = random.nextInt(array.size());
                var randomEntry = array.get(index);
                records.add(FakeUtils.fakeIndividualFrom(randomEntry));
            }
        } catch (IOException e) {
            throw new SdkException(e);
        }
    }


    private <T> T getPartitionParam(StepExecution stepExecution, String paramName, Class<T> paramType) {
        var suffix = this.getClass().getSimpleName();
        var value = stepExecution.getExecutionContext().get(suffix + paramName, paramType);
        assert (value != null);
        return value;
    }

}
