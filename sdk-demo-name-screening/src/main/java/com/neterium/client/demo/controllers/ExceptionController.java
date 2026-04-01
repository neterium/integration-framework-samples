package com.neterium.client.demo.controllers;

import com.neterium.client.demo.repositories.MatchRepository;
import com.neterium.client.sdk.screening.ExceptionTemplate;
import com.neterium.sdk.api.ExceptionsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ExceptionController
 *
 * @author Bernard Ligny
 */
@RestController
@RequestMapping("/rest/exceptions")
@Slf4j
public class ExceptionController {

    @Value("${neterium.screening.client-reference}")
    private String clientReference;


    private final ExceptionTemplate exceptionTemplate;
    private final ExceptionsApi exceptionsApi;
    private final MatchRepository matchRepository;

    public ExceptionController(ExceptionTemplate exceptionTemplate,
                               ExceptionsApi exceptionsApi,
                               MatchRepository matchRepository) {
        this.exceptionTemplate = exceptionTemplate;
        this.exceptionsApi = exceptionsApi;
        this.matchRepository = matchRepository;
    }


    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String createExceptionForMatch(@RequestParam(value = "matchId") String matchId) {
        var match = matchRepository.findById(matchId).orElseThrow();
        var exceptionId = exceptionTemplate.createWhiteListException(
                match.getScreenedText(),
                match.getMatchedText(),
                match.getProfileId(),
                true);
        match.setExceptionId(exceptionId);
        matchRepository.save(match);
        return exceptionId;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<?> getExceptions(@RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                 @RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
        var outcome = exceptionsApi.getExceptions(offset, limit, null, null, null, null,
                null, clientReference);
        return outcome.getData();
    }


    @DeleteMapping(value = "/{id}")
    public void deleteException(@PathVariable("id") String exceptionId) {
        exceptionTemplate.deleteException(exceptionId);
        // Reset decision for impacted matches
        var matches = matchRepository.findByExceptionId(exceptionId);
        matches.forEach(match -> match.setDecision(null));
        matchRepository.saveAll(matches);
    }

}
