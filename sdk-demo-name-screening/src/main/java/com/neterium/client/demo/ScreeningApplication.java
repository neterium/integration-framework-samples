package com.neterium.client.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application bootstrap
 *
 * @author Bernard Ligny
 */
@SpringBootApplication(scanBasePackages = {"com.neterium.client.demo"})
@EnableCaching
@EnableScheduling
public class ScreeningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScreeningApplication.class, args);
    }

}
