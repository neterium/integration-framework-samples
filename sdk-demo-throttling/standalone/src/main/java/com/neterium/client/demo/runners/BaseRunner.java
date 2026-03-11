package com.neterium.client.demo.runners;

import com.google.common.util.concurrent.RateLimiter;
import com.neterium.client.sdk.screening.ScreeningTemplate;
import com.neterium.client.sdk.throttling.Throttleable;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * Base class for <code>ApplicationRunner</code> implementation
 *
 * @author Bernard Ligny
 */
@Slf4j
public abstract class BaseRunner implements ApplicationRunner {


    @Value("${demo.screening.collection:sanctions}")
    protected String collection;

    @Value("${demo.screening.options.threshold:80}")
    protected int threshold;

    @Value("${demo.hit-ratio:0.2}")
    private double hitRatio;

    @Autowired
    protected ScreeningTemplate screeningTemplate;

    @Autowired
    @Throttleable
    private ThreadPoolTaskExecutor taskExecutor;


    @PostConstruct
    private void init() {
        taskExecutor.setAwaitTerminationSeconds(60 * 60); // 1 hour
    }

    protected void submitTask(Runnable task) {
        taskExecutor.submit(task);
    }


    @Override
    public void run(ApplicationArguments args) {
        try {
            List<String> params = args.getNonOptionArgs();
            if (params.size() != 2) {
                System.out.println("Usage: ThrottlingApp nbRecords ratePerSecond");
                System.exit(-1);
            }
            var nbRecords = Integer.parseInt(params.get(0));
            var rate = Integer.parseInt(params.get(1));
            log.info("Starting app with {} records using a rate of {} requests/sec", nbRecords, rate);
            sendScreeningRequests(nbRecords, rate, hitRatio);
            taskExecutor.initiateShutdown();
            System.exit(0);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            t.printStackTrace(System.err); //NOSONAR
            System.exit(-1);
        }
    }


    @SuppressWarnings("UnstableApiUsage")
    protected void rateLimitedProducer(int nbTimes, int ratePerSecond, UnaryOperator<Integer> operation) {
        var rateLimiter = RateLimiter.create(ratePerSecond);
        IntStream.range(1, nbTimes + 1).forEach(index -> {
            rateLimiter.acquire();
            int totalProcessed = operation.apply(index);
            if ((index % ratePerSecond) == 0) {
                log.info("*** Issued: {} records ({} requests) ***", totalProcessed, index);
            }
        });
    }


    protected abstract void sendScreeningRequests(int nbRecords, int ratePerSecond, double hitRatio) throws Throwable;


}
