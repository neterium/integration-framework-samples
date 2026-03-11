package com.neterium.client.demo.batch.screening;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.batch.support.BasicPartitioner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * CounterPartDbReader
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class CounterPartDbReader extends MongoPagingItemReader<Counterpart> {

    /**
     * Static part of reader configuration
     */
    public CounterPartDbReader(MongoTemplate mongoTemplate,
                               @Value("${demo.jobs.page-size:50}") int pageSize) {
        super();
        super.setTemplate(mongoTemplate);
        super.setTargetType(Counterpart.class);
        super.setPageSize(pageSize);
        super.setQuery(new Query());
    }

    /**
     * Dynamic part of reader configuration
     */
    @BeforeStep
    public void configureReader(StepExecution stepExecution) {
        var value = getPartitionParam(stepExecution, BasicPartitioner.KEY_PARTITION_KEY, String.class);
        super.setQuery(new Query()
                .addCriteria(Criteria.where("partitionKey")
                        .is(value))
                .with(Sort.by(Sort.Direction.ASC, "id"))
        );
        log.debug("About to read records with partitionKey '{}' by page of {}", value, pageSize);
    }


    private <T> T getPartitionParam(StepExecution stepExecution, String paramName, Class<T> paramType) {
        var suffix = this.getClass().getSimpleName();
        return stepExecution.getExecutionContext().get(suffix + paramName, paramType);
    }

}
