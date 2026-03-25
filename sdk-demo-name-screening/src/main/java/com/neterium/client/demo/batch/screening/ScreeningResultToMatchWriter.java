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
import com.neterium.client.demo.domain.Match;
import com.neterium.client.sdk.batch.screening.ScreeningTuple;
import com.neterium.client.sdk.matching.MatchFinder;
import com.neterium.client.sdk.model.ScreenedObjectType;
import com.neterium.client.sdk.utils.JsonHelper;
import com.neterium.sdk.api.RepositoryApi;
import com.neterium.sdk.model.CoreResponseMatch;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ScreeningResultToMatchWriter
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
public class ScreeningResultToMatchWriter implements ItemWriter<ScreeningTuple<Counterpart>> {

    private static final boolean FETCH_FULL_PROFILE = true;

    private final ItemWriter<Match> mongoWriter;
    private final MatchFinder<Match> matchFinder;
    private final JsonHelper jsonHelper;
    private final RepositoryApi repositoryApi;

    public ScreeningResultToMatchWriter(ItemWriter<Match> matchWriter,
                                        MatchFinder<Match> matchFinder,
                                        JsonHelper jsonHelper,
                                        RepositoryApi repositoryApi) {
        this.mongoWriter = matchWriter;
        this.matchFinder = matchFinder;
        this.jsonHelper = jsonHelper;
        this.repositoryApi = repositoryApi;
    }


    @Override
    public void write(Chunk<? extends ScreeningTuple<Counterpart>> chunk) throws Exception {
        // Extract all matches screening results
        var chunkItems = new Chunk<>(prepareData(chunk));
        // Write matches to MongoDB
        mongoWriter.write(chunkItems);
    }


    private List<Match> prepareData(Chunk<? extends ScreeningTuple<Counterpart>> chunk) {
        var matches = new ArrayList<Match>();
        for (var tuple : chunk.getItems()) {
            var counterpartId = tuple.getInput().getId();
            tuple.getResult()
                    .getMatches(false)
                    .stream()
                    .filter(match -> match.getLevel() != CoreResponseMatch.LevelEnum.DISCARDED)
                    .map(match -> buildOrUpdateMatch(counterpartId, match))
                    .forEach(matches::add);
        }
        return matches;
    }


    private Match buildOrUpdateMatch(String counterPartId, CoreResponseMatch match) {
        var matchId = match.getMatchId();
        var profileId = match.getProfileId();
        var entity = matchFinder.findByTypeAndRefAndProfile(ScreenedObjectType.COUNTERPART, counterPartId, profileId)
                .orElseGet(() -> new Match(matchId, profileId, counterPartId, LocalDateTime.now()));
        entity.setScreenedText(match.getScreened());
        entity.setMatchedText(match.getMatch());
        entity.setScore(match.getScore());
        entity.setExternalId(matchId);
        entity.setProfileDetails(getProfileInfo(match));
        entity.setLocation(jsonHelper.serialize(match.getLocation()));
        entity.setActive(true);
        Optional.ofNullable(match.getProfileSummary())
                .ifPresent(summary -> entity.setCheckSum(summary.getChecksum()));
        Optional.ofNullable(match.getLevel())
                .ifPresent(level -> entity.setLevel(level.name()));
        return entity;
    }


    private String getProfileInfo(CoreResponseMatch match) {
        Object info;
        if (FETCH_FULL_PROFILE) {
            info = repositoryApi.getProfile(match.getProfileId()).getData()
                    .getFirst();
        } else {
            info = match.getProfileSummary();
        }
        return jsonHelper.serialize(info);
    }

}
