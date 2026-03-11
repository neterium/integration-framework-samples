package com.neterium.client.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Application bootstrap
 *
 * @author Bernard Ligny
 */
@SpringBootApplication(scanBasePackages = {"com.neterium.client.demo"})
@EnableCaching
public class ThrottlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThrottlingApplication.class, args);
    }

}
