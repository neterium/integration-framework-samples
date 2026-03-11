package com.neterium.client.demo.batch;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.batch.listeners.SessionJobListener;
import com.neterium.client.sdk.screening.ScreeningTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * CounterpartWriter
 * <p>
 * "Write" (ie send) a batch of counterparts into the screener
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class
CounterpartWriter implements ItemWriter<Counterpart> {

    private final ScreeningTemplate screeningTemplate;

    private String sessionId;
    private String screeningCollection;
    private int screeningThreshold;


    public CounterpartWriter(ScreeningTemplate screeningTemplate) {
        this.screeningTemplate = screeningTemplate;
    }


    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        sessionId = stepExecution.getJobExecution()
                .getExecutionContext()
                .get(SessionJobListener.SESSION_ID, String.class);
        var jobParams = new ScreeningParameters(stepExecution.getJobParameters());
        screeningCollection = jobParams.getScreeningCollection();
        screeningThreshold = jobParams.getScreeningThreshold();
    }


    @Override
    public void write(Chunk<? extends Counterpart> chunk) {
        var response = screeningTemplate.screenNames(chunk.getItems(),
                screeningCollection, screeningThreshold, sessionId);
        assert (!response.isEmpty());
    }

}
