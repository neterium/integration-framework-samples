package com.neterium.client.demo.batch.screening;

import com.neterium.client.demo.domain.Match;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * ResetAllMatches
 *
 * @author Bernard Ligny
 */
@Component
@Slf4j
public class ResetAllMatches implements StepExecutionListener {

    private final MongoTemplate mongoTemplate;


    public ResetAllMatches(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        var update = new Update().set("active", false);
        var results = mongoTemplate.updateMulti(new Query(), update, Match.class);
        log.info("{} matches deactivated", results.getModifiedCount());
    }

}
