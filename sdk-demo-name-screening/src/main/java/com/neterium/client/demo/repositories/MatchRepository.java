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

    Optional<Match> findByCounterpartIdAndProfileId(String counterpartId, String profileId);

    List<Match> findByCounterpartId(String counterpartId);

    Page<Match> findByDecision(Match.Decision decision, Pageable pageable);

}
