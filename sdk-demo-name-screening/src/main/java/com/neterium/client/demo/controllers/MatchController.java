package com.neterium.client.demo.controllers;

import com.neterium.client.demo.domain.Match;
import com.neterium.client.demo.repositories.CounterpartRepository;
import com.neterium.client.demo.repositories.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MatchController
 *
 * @author Bernard Ligny
 */
@RestController
@RequestMapping("/rest/matches")
@Slf4j
public class MatchController {

    private final CounterpartRepository counterpartRepository;
    private final MatchRepository matchRepository;


    public MatchController(CounterpartRepository counterpartRepository,
                           MatchRepository matchRepository) {
        this.counterpartRepository = counterpartRepository;
        this.matchRepository = matchRepository;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Match> findMatches(@RequestParam(value = "filter", required = false, defaultValue = "") String filter,
                                   @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                   @RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
        var pageable = PageRequest.of(offset, limit, Sort.by(Sort.Order.asc("screenedText")));
        if (ObjectUtils.isEmpty(filter)) {
            return matchRepository.findByActive(true, pageable).getContent();
        } else {
            Match.Decision decision = Match.Decision.valueOf(filter.toUpperCase());
            return matchRepository.findByActiveAndDecision(true, decision, pageable).getContent();
        }
    }


    @PutMapping(value = "/{id}")
    @Transactional
    public void updateMatch(@PathVariable("id") String id,
                            @RequestParam(value = "decision") Match.Decision decision) {
        // Update match record
        var match = matchRepository.findById(id).orElseThrow();
        var before = match.getDecision();
        match.setDecision(decision);
        match.setLastModified(LocalDateTime.now());
        matchRepository.save(match);

        // Reflect change in counterpart counter
        counterpartRepository.findById(match.getCounterpartId())
                .ifPresent(counterpart -> {
                    var delta = 0;
                    if (decision.equals(Match.Decision.IGNORE)) {
                        delta = -1;
                    } else if (before.equals(Match.Decision.IGNORE)) {
                        delta = +1;
                    }
                    counterpart.setAlertCount(counterpart.getAlertCount() + delta);
                    counterpartRepository.save(counterpart);
                });
    }

}
