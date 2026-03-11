package com.neterium.client.demo.batch.screening;

import com.neterium.client.demo.domain.Match;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * MatchDbWriter
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class MatchDbWriter extends MongoItemWriter<Match> {

    public MatchDbWriter(MongoTemplate mongoTemplate) {
        super();
        super.setTemplate(mongoTemplate);
        super.setMode(Mode.UPSERT);
    }

}
