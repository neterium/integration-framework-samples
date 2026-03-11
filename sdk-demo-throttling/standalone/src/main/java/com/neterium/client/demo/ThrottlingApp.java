package com.neterium.client.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


/**
 * Application bootstrap
 * Appropriate <code>ApplicationRunner</code> will be invoked depending
 * on activated Spring profile ("jetscan" or "jetflow")
 *
 * @author Bernard Ligny
 */
@SpringBootApplication
@EnableCaching
@Slf4j
public class ThrottlingApp {

    public static void main(String[] args) {
        SpringApplication.run(ThrottlingApp.class, args);
    }

}