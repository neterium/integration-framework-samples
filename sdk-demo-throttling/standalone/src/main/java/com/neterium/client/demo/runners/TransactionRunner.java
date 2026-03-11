package com.neterium.client.demo.runners;

import com.neterium.client.demo.domain.Transaction;
import com.neterium.client.demo.flows.TransactionProducer;
import com.neterium.client.sdk.model.ScreenableTransaction;
import com.neterium.client.sdk.screening.ScreeningResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


/**
 * ApplicationRunner for transactions
 *
 * @author Bernard Ligny
 */
@Component
@Profile("jetflow")
@Slf4j
public class TransactionRunner extends BaseRunner {

    private final TransactionProducer producer;

    public TransactionRunner(TransactionProducer producer) {
        this.producer = producer;
    }


    @Override
    public void sendScreeningRequests(int nbRecords, int ratePerSecond, double hitRatio) {
        var hitModulo = (int) Math.round(1 / hitRatio);
        super.rateLimitedProducer(nbRecords, ratePerSecond, index -> {
                    var withHit = ((index % hitModulo) == 0);
                    var next = producer.createSampleRecord(withHit);
                    screenSingleTransaction(next);
                    return index;
                }
        );
    }


    private void screenSingleTransaction(Transaction transaction) {
        final Runnable task = (() -> {
            var result = screeningTemplate.screenTransaction(transaction, collection, threshold);
            onScreeningResult(transaction, result);
        }
        );
        super.submitTask(task);
    }


    private void onScreeningResult(ScreenableTransaction transaction, ScreeningResponseItem result) {
        if (result.getScreenerOutcome().getMatchCount() > 0) {
            log.info("Hit for {}", transaction);
        }
    }

}