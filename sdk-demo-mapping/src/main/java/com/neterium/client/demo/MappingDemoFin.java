package com.neterium.client.demo;

import com.neterium.client.sdk.converters.SwiftFinMT103Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * MappingDemo for swift MT-103
 *
 * @author Bernard Ligny
 */
@Slf4j
@Component
@Profile("fin")
public class MappingDemoFin implements ApplicationRunner {

    private static final String SAMPLE_DIR = "src/test/resources";
    private static final Path INPUT_FILE = Path.of(SAMPLE_DIR, "sample-mt-103.fin");
    //private static final Path INPUT_FILE = Path.of(SAMPLE_DIR, "sample-mt-541.fin");


    /**
     * Inject a ready-to-use FIN converter from SDK
     */
    @Autowired
    private SwiftFinMT103Converter converter;
    //private SwiftFinMT541Converter converter;


    /**
     * Invoke converter to read a sample FIN file
     * and print resulting json payloads in the console
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        var batchCount = new AtomicInteger(0);
        converter.convertAndSerialize(INPUT_FILE, 3, true)
                .forEach(batch ->
                        System.out.printf("--- (Batch#%s) ---\n%s%n", batchCount.incrementAndGet(), batch)
                );
        log.info("{} batches successfully generated", batchCount.get());
    }

}