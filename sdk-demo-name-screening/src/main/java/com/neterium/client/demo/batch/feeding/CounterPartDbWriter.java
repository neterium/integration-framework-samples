package com.neterium.client.demo.batch.feeding;

import com.neterium.client.demo.domain.Counterpart;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * CounterPartDbWriter
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
public class CounterPartDbWriter extends MongoItemWriter<Counterpart> {

    public CounterPartDbWriter(MongoTemplate mongoTemplate) {
        super();
        super.setTemplate(mongoTemplate);
        super.setMode(Mode.UPSERT);
    }

}
