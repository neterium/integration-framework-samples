package com.neterium.client.demo.matching;

import com.neterium.client.demo.domain.Match;
import com.neterium.client.demo.repositories.MatchRepository;
import com.neterium.client.sdk.matching.MatchFinder;
import com.neterium.client.sdk.model.ScreenedObjectType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * MatchFinderImpl
 *
 * @author Bernard Ligny
 */
@Component
public class MatchFinderImpl implements MatchFinder<Match> {

    private final MatchRepository matchRepository;

    public MatchFinderImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }


    @Override
    public Optional<Match> findByTypeAndRefAndProfile(ScreenedObjectType objectType,
                                                      String objectRef,
                                                      String profileId) {
        Assert.isTrue(ScreenedObjectType.COUNTERPART.equals(objectType), "Unsupported type");
        return matchRepository.findByCounterpartIdAndProfileId(objectRef, profileId);
    }

}
