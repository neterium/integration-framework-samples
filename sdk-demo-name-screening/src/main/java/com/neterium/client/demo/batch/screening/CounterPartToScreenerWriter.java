package com.neterium.client.demo.batch.screening;

import com.neterium.client.demo.batch.support.JobParamsSupport;
import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.batch.listeners.SessionJobListener;
import com.neterium.client.sdk.batch.screening.ScreeningTuple;
import com.neterium.client.sdk.screening.ScreeningTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * CounterPartToScreenerWriter
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class CounterPartToScreenerWriter implements ItemWriter<ScreeningTuple<Counterpart>>, StepExecutionListener {

    private final ScreeningTemplate screeningTemplate;

    private String sessionId;
    private String screeningCollection;
    private int screeningThreshold;


    public CounterPartToScreenerWriter(ScreeningTemplate screeningTemplate) {
        this.screeningTemplate = screeningTemplate;
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        sessionId = stepExecution.getJobExecution()
                .getExecutionContext()
                .get(SessionJobListener.SESSION_ID, String.class);
        var jobParams = new JobParamsSupport(stepExecution.getJobParameters());
        screeningCollection = jobParams.getScreeningCollection();
        screeningThreshold = jobParams.getScreeningThreshold();
    }


    @Override
    public void write(Chunk<? extends ScreeningTuple<Counterpart>> chunk) {
        // Send counterparts batch to screener
        var tuples = chunk.getItems();
        var counterparts = tuples
                .stream()
                .map(ScreeningTuple::getInput)
                .toList();
        var responseMap = screeningTemplate.screenNames(counterparts, screeningCollection, screeningThreshold, sessionId);
        // Assign the result to each corresponding input counterpart
        for (var tuple : tuples) {
            tuple.setResult(responseMap.get(tuple.getInput()));
        }
    }

}
