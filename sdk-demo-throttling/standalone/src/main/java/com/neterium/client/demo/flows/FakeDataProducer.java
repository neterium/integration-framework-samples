package com.neterium.client.demo.flows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neterium.client.sdk.exception.SdkException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;


/**
 * Abstract producer of fake data
 *
 * @author Bernard Ligny
 */
@Slf4j
public abstract class FakeDataProducer<T> {

    protected final Random random = new Random();
    private final ObjectMapper mapper;
    private final Locale LOCALE = Locale.of("es"); // Spanish names
    protected final Faker faker = new Faker(LOCALE);

    private JsonNode hitRecords;


    protected FakeDataProducer(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @PostConstruct
    private void init() {
        var resource = new ClassPathResource("data/counterpart-hits.json");
        try (var is = resource.getInputStream()) {
            hitRecords = mapper.readTree(is);
        } catch (IOException e) {
            throw new SdkException(e);
        }
    }


    public T createSampleRecord(boolean withHit) {
        return (withHit ? fakeRecord(randomHitRecord()) : fakeRecord());
    }

    protected abstract T fakeRecord();

    protected abstract T fakeRecord(JsonNode template);


    protected JsonNode randomHitRecord() {
        var index = random.nextInt(hitRecords.size());
        return hitRecords.get(index);
    }

}