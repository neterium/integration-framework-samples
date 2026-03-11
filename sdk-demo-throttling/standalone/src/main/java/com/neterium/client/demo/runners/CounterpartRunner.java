package com.neterium.client.demo.runners;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.demo.flows.CounterpartProducer;
import com.neterium.client.sdk.model.ScreenableParty;
import com.neterium.client.sdk.screening.ScreeningResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


/**
 * ApplicationRunner for counterpart
 *
 * @author Bernard Ligny
 */
@Component
@Profile("jetscan")
@Slf4j
public class CounterpartRunner extends BaseRunner {

    private final CounterpartProducer producer;

    @Value("${demo.screening.batch-size:50}")
    private int batchSize;

    public CounterpartRunner(CounterpartProducer producer) {
        this.producer = producer;
    }


    @Override
    public void sendScreeningRequests(int nbRecords, int ratePerSecond, double hitRatio) {
        var nbBatches = Math.ceilDiv(nbRecords, batchSize);
        var hitModulo = (int) Math.round(1 / hitRatio);
        final var totalIssued = new AtomicInteger(0);
        super.rateLimitedProducer(nbBatches, ratePerSecond, batchIndex -> {
                    int effectiveBatchSize = Math.min(nbRecords - totalIssued.get(), batchSize);
                    var batch = populateNewBatch(effectiveBatchSize, hitModulo);
                    screenBatchOfCounterpart(batch);
                    return totalIssued.addAndGet(batch.size());
                }
        );
    }


    private List<Counterpart> populateNewBatch(int batchSize, int hitModulo) {
        return IntStream.range(1, batchSize + 1)
                .mapToObj(i -> {
                    var withHit = ((i % hitModulo) == 0);
                    return producer.createSampleRecord(withHit);
                })
                .toList();
    }


    private void screenBatchOfCounterpart(List<Counterpart> batch) {
        final Runnable task = (() ->
                screeningTemplate.screenNames(batch, collection, threshold)
                        .forEach(this::onScreeningResult)
        );
        super.submitTask(task);
    }


    private void onScreeningResult(ScreenableParty counterpart, ScreeningResponseItem result) {
        if (result.getScreenerOutcome().getMatchCount() > 0) {
            log.info("Hit for {}", counterpart);
        }
    }

}
