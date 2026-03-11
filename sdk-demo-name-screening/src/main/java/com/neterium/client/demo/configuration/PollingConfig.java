package com.neterium.client.demo.configuration;

import com.neterium.client.sdk.files.DirectoryWatcher;
import com.neterium.client.sdk.files.FileProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.time.Duration;

/**
 * PollingConfig
 *
 * @author Bernard Ligny
 */
@Configuration(proxyBeanMethods = false)
public class PollingConfig {

    @Value("${demo.polling.directory}")
    private Path pollingDirectory;

    @Value("${demo.polling.interval}")
    private Duration pollingInterval;

    @Bean
    public DirectoryWatcher directoryWatcher(FileProcessor fileProcessor) {
        var filter = DirectoryWatcher.matchExtensions(".csv");
        return new DirectoryWatcher(pollingDirectory,  filter, pollingInterval, fileProcessor);
    }

}
