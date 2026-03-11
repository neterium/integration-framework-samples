package com.neterium.client.demo.batch.feeding;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.demo.files.CsvCounterPart;
import com.neterium.client.sdk.batch.support.PartitionKeyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CounterPartTransformer
 * Turn csv records into db entities
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class CounterPartTransformer implements ItemProcessor<CsvCounterPart, Counterpart> {

    private static final Object lock = true;
    private static PartitionKeyFactory sharedPartitionKeyFactory;


    @Value("${demo.jobs.partitioning-width:26}")
    public void setPartitionKeyWidth(int width) {
        synchronized (lock) {
            if (sharedPartitionKeyFactory == null) {
                sharedPartitionKeyFactory = new PartitionKeyFactory(width);
            }
        }
    }


    @Override
    public Counterpart process(CsvCounterPart input) {
        var entity = Counterpart.builder()
                .id(input.getRecordId())
                .type(input.getType())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .middleNames(input.getMiddleNames())
                .gender(input.getGender())
                .dateOfBirth(input.getDateOfBirth())
                .registrationNumber(input.getRegistrationNumber())
                .registrationCountryCode(input.getRegistrationCountryCode())
                .addressCityName(input.getAddressCityName())
                .addressCountryCode(input.getAddressCountryCode())
                .lastImport(LocalDateTime.now())
                .partitionKey(sharedPartitionKeyFactory.pick())
                .build();
        log.trace("Prepared entity: {}", entity);
        return entity;
    }

}
