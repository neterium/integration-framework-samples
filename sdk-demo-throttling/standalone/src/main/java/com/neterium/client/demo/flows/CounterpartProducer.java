package com.neterium.client.demo.flows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.model.CounterpartType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


/**
 * Producer of fake/random counterpart data
 *
 * @author Bernard Ligny
 */
@Component
@Slf4j
public class CounterpartProducer extends FakeDataProducer<Counterpart> {


    public CounterpartProducer(ObjectMapper mapper) {
        super(mapper);
    }


    @Override
    protected Counterpart fakeRecord() {
        return Counterpart.builder()
                .id(UUID.randomUUID().toString())
                .lastName(faker.name().lastName())
                .firstName(faker.name().firstName())
                .type(CounterpartType.INDIVIDUAL)
                .build();
    }


    @Override
    protected Counterpart fakeRecord(JsonNode template) {
        return Counterpart.builder()
                .id(UUID.randomUUID().toString())
                .lastName(template.get("lastname").asText())
                .firstName(
                        Optional.ofNullable(template.get("firstname"))
                                .map(JsonNode::asText)
                                .orElse(null)
                )
                .type(CounterpartType.INDIVIDUAL)
                .build();
    }

}