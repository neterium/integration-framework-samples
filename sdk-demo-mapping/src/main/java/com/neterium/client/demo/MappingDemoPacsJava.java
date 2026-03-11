package com.neterium.client.demo;

import com.neterium.client.sdk.converters.Pacs008Converter;
import com.neterium.sdk.model.CoreRequestContext;
import com.neterium.sdk.model.CoreScreenOptions;
import com.neterium.sdk.model.JetFlowRequestBody;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * MappingDemo for PACS-008
 * Variant#2 : working with java payloads
 *
 * @author Bernard Ligny
 */
@Slf4j
@Component
@Profile("pacs-java")
public class MappingDemoPacsJava implements ApplicationRunner {

    private static final String SAMPLE_DIR = "src/test/resources";
    private static final Path INPUT_FILE = Path.of(SAMPLE_DIR, "sample-pacs008-small.xml");


    /**
     * Inject a ready-to-use PACS-008 converter from SDK
     */
    @Autowired
    private Pacs008Converter converter;


    /**
     * Invoke converter to read a sample PACS file
     * and show resulting java pojo's in the console
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        var batchCount = new AtomicInteger(0);
        converter.convert(INPUT_FILE, 5)
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
        converter.addEnricher(this::addCustomOptions);
        converter.addEnricher(this::addCustomContext);
    }


    private JetFlowRequestBody addCustomOptions(JetFlowRequestBody request) {
        // Let's set a dynamic threshold depending on transaction amount
        var bigAmount = request.getRecords()
                .stream()
                .map(record -> record.getObject().getAmount())
                .filter(Objects::nonNull)
                .anyMatch(amount -> amount > 100_000);
        var options = new CoreScreenOptions();
        options.setThreshold(bigAmount ? 70 : 85);
        options.addTagsItem(CoreScreenOptions.TagsEnum.SAN);
        options.riskCountries(List.of("RU"));
        return request.options(options);
    }


    private JetFlowRequestBody addCustomContext(JetFlowRequestBody request) {
        var context = new CoreRequestContext();
        context.setProjectReference("Neterium");
        context.setClientReference("Demo (java variant)");
        return request.context(context);
    }

}