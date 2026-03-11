package com.neterium.client.demo.runners;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.demo.utils.DataGenerator;
import com.neterium.client.sdk.screening.ScreeningResponseItem;
import com.neterium.client.sdk.utils.BatchHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CounterpartRunner
 *
 * @author Bernard Ligny
 */
@Component
@Profile("jetscan")
@Slf4j
public class CounterpartRunner extends BaseRunner {

    private static final int RECORDS_COUNT = 3_000;
    private static final int BATCH_SIZE = 30;
    private static final String COLLECTION = "sanctions";
    private static final int THRESHOLD = 85;


    @Override
    public void run(ApplicationArguments args) {
        var dataStream = DataGenerator.sampleCounterparts(RECORDS_COUNT);
        BatchHelper.groupIntoBatches(dataStream, BATCH_SIZE)
                .forEach(this::screenBatchOfCounterpart);
        System.exit(0);
    }


    private void screenBatchOfCounterpart(List<Counterpart> batch) {
        final Runnable task = (() ->
                screeningTemplate.screenNames(batch, COLLECTION, THRESHOLD)
                        .forEach(this::onScreeningResult)
        );
        super.submitTask(task);
    }


    private void onScreeningResult(Counterpart counterpart, ScreeningResponseItem result) {
        if (result.getScreenerOutcome().getMatchCount() > 0) {
            log.warn("Hit for {}", counterpart);
        }
    }

}
