package com.neterium.client.demo.runners;

import com.neterium.client.demo.domain.Transaction;
import com.neterium.client.demo.utils.DataGenerator;
import com.neterium.client.sdk.model.ScreenableTransaction;
import com.neterium.client.sdk.screening.ScreeningResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TransactionRunner
 *
 * @author Bernard Ligny
 */
@Component
@Profile("jetflow")
@Slf4j
public class TransactionRunner extends BaseRunner {

    private static final int RECORDS_COUNT = 25;
    private static final String COLLECTION = "sanctions";
    private static final int THRESHOLD = 85;


    @Override
    public void run(ApplicationArguments args) {
        DataGenerator.sampleTransactions(RECORDS_COUNT)
                .forEach(this::screenSingleTransaction);
        System.exit(0);
    }


    private void screenSingleTransaction(Transaction transaction) {
        final Runnable task = (() -> {
            var result = screeningTemplate.screenTransaction(transaction, COLLECTION, THRESHOLD);
            onScreeningResult(transaction, result);
        });
        super.submitTask(task);
    }


    private void onScreeningResult(ScreenableTransaction<?> transaction, ScreeningResponseItem result) {
        if (result.getScreenerOutcome().getMatchCount() > 0) {
            log.warn("Hit for {}", transaction);
        }
    }

}
