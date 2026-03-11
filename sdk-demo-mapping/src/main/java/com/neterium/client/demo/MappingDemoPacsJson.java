package com.neterium.client.demo;

import com.neterium.client.sdk.converters.Pacs008Converter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * MappingDemo for PACS-008
 * Variant#1 : working with json payloads
 *
 * @author Bernard Ligny
 */
@Slf4j
@Component
@Profile("pacs-json")
public class MappingDemoPacsJson implements ApplicationRunner {

    private static final String SAMPLE_DIR = "src/test/resources";
    private static final Path INPUT_FILE = Path.of(SAMPLE_DIR, "sample-pacs008-medium.xml");
    private static final Path CUSTOM_OPTIONS = Path.of(SAMPLE_DIR, "options.json");
    private static final Path CUSTOM_CONTEXT = Path.of(SAMPLE_DIR, "context.json");


    /**
     * Inject a ready-to-use PACS-008 converter from SDK
     */
    @Autowired
    private Pacs008Converter converter;


    /**
     * Invoke converter to read a sample PACS file
     * and print resulting json payloads in the console
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        var batchCount = new AtomicInteger(0);
        converter.convertAndSerialize(INPUT_FILE, 10, true)
                .forEach(batch ->
                        System.out.printf("--- (Batch#%s) ---\n%s%n", batchCount.incrementAndGet(), batch)
                );
        log.info("{} batches successfully generated", batchCount.get());
    }


    /**
     * Optional: customize the converter to enrich the generated payloads with
     * - custom options
     * - a custom context
     */
    @PostConstruct
    public void init() {
        converter.addContextEnricher(CUSTOM_CONTEXT);
        converter.addOptionEnricher(CUSTOM_OPTIONS);
    }

}