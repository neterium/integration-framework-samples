package com.neterium.client.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * MappingApp.
 * Appropriate <code>ApplicationRunner</code> will be invoked depending
 * on activated Spring profile (ie "pacs-java", "pacs-json", or "fin)
 *
 * @author Bernard Ligny
 */
@SpringBootApplication
@Slf4j
public class MappingApp {

    public static void main(String[] args) {
        SpringApplication.run(MappingApp.class, args);
    }

}