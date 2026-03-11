/*
 * Copyright 2006-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.neterium.client.demo.batch.screening;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.sdk.batch.screening.ScreeningTuple;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ScreeningResultToCounterpartWriter
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
public class ScreeningResultToCounterpartWriter implements ItemWriter<ScreeningTuple<Counterpart>> {

    private final ItemWriter<Counterpart> mongoWriter;

    public ScreeningResultToCounterpartWriter(ItemWriter<Counterpart> counterPartDbWriter) {
        this.mongoWriter = counterPartDbWriter;
    }


    @Override
    public void write(Chunk<? extends ScreeningTuple<Counterpart>> chunk) throws Exception {
        // Update input counterparts based on screening results
        var updatedCounterparts = chunk.getItems()
                .stream()
                .peek(this::updateRecord) // NOSONAR
                .map(ScreeningTuple::getInput)
                .toList();
        // Push changes to MongoDB
        var chunkItems = new Chunk<>(updatedCounterparts);
        mongoWriter.write(chunkItems);
    }


    private void updateRecord(ScreeningTuple<Counterpart> tuple) {
        var counterpart = tuple.getInput();
        counterpart.setLastScreenedAt(LocalDateTime.now());
        var screeningResult = tuple.getResult();
        counterpart.setMatchCount(screeningResult.getScreenerOutcome().getMatchCount());
        counterpart.setAlertCount(screeningResult.getAlertCount());
    }

}
