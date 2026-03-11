package com.neterium.client.demo.runners;

import com.neterium.client.sdk.screening.ScreeningTemplate;
import com.neterium.client.sdk.throttling.Throttleable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * BaseRunner
 * Base class for <code>ApplicationRunner</code> implementation
 *
 * @author Bernard Ligny
 */
public abstract class BaseRunner implements ApplicationRunner {

    @Autowired
    protected ScreeningTemplate screeningTemplate;

    @Autowired
    @Throttleable
    private ThreadPoolTaskExecutor taskExecutor;


    @PostConstruct
    private void init() {
        taskExecutor.setAwaitTerminationSeconds(30);
    }

    protected void submitTask(Runnable task) {
        taskExecutor.submit(task);
    }

}
