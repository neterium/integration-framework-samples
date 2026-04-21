package com.neterium.client.demo;

import com.neterium.client.sdk.privatelist.ListType;
import com.neterium.client.sdk.privatelist.PrivateListTemplate;
import com.neterium.client.sdk.privatelist.UploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * PrivateListRunner
 *
 * @author Bernard Ligny
 */
@Component
@Slf4j
public class PrivateListRunner implements ApplicationRunner {

    private static final String CLIENT_REF = "DEMO";
    private static final String DIR = "src/main/resources";

    @Autowired
    private PrivateListTemplate template;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        run("regular-list.csv", ListType.ENTITY_LIST, "SDK", true);
        run("custom-list.csv", ListType.CUSTOM_LIST, null, true);
        System.exit(0);
    }


    private void run(String fileName, ListType listType, String desiredListId, boolean upload) throws Exception {
        StopWatch sw = new StopWatch();

        // Build
        var file = Path.of(DIR, fileName);
        var out = template.toXmlFormat(file, listType);
        log.info("XML file: {}", out.toAbsolutePath());
        log.debug(Files.readString(out));

        if (upload) {
            // Upload
            var req = UploadRequest.builder()
                    .withListId(desiredListId)
                    .withFile(out.toFile())
                    .withClientReference(CLIENT_REF)
                    .build();
            sw.start();
            var listId = template.uploadPrivateList(req);
            sw.stop();
            log.info("Created - {} ({} ms)", listId, sw.getTotalTimeMillis());

            // Delete
            template.deletePrivateList(listId, CLIENT_REF);
            log.info("Deleted - {}", listId);
        }

        // Clean up
        Files.delete(out);
    }

}
