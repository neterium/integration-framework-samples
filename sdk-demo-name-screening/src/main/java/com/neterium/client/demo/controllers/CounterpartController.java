package com.neterium.client.demo.controllers;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.demo.domain.Match;
import com.neterium.client.demo.repositories.CounterpartRepository;
import com.neterium.client.demo.repositories.MatchRepository;
import com.neterium.client.sdk.exception.SdkException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CounterpartController
 *
 * @author Bernard Ligny
 */
@RestController
@RequestMapping("/rest/counterparts")
public class CounterpartController {

    private final CounterpartRepository counterpartRepository;
    private final MatchRepository matchRepository;


    public CounterpartController(CounterpartRepository counterpartRepository,
                                 MatchRepository matchRepository) {
        this.counterpartRepository = counterpartRepository;
        this.matchRepository = matchRepository;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Counterpart> getAllCounterparts(@RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                                @RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
        var pageable = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("matchCount")));
        return counterpartRepository.findAll(pageable).getContent();
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Counterpart getCounterpart(@PathVariable("id") String counterpartId) {
        return counterpartRepository.findById(counterpartId)
                .orElseThrow(() -> new SdkException("Counterpart not found"));
    }


    @GetMapping(value = "/{id}/matches", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Match> getCounterpartMatches(@PathVariable("id") String counterpartId) {
        return matchRepository.findByActiveAndCounterpartId(true, counterpartId);
    }

}