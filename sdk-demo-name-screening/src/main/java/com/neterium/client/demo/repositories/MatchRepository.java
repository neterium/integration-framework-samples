package com.neterium.client.demo.repositories;

import com.neterium.client.demo.domain.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MatchRepository
 *
 * @author Bernard Ligny
 */
@Repository
public interface MatchRepository extends MongoRepository<Match, String> {

    // --- For MatchFinder (to re-apply decision) ---
    Optional<Match> findByCounterpartIdAndProfileId(String counterpartId, String profileId);

    // --- For the 'Counterparts' web page ---

    List<Match> findByActiveAndCounterpartId(boolean active, String counterpartId);

    // --- For the 'Matches' web page ---

    Page<Match> findByActive(boolean active, Pageable pageable);

    Page<Match> findByActiveAndDecision(boolean active, Match.Decision decision, Pageable pageable);

    List<Match> findByExceptionId(String exceptionId);

}
