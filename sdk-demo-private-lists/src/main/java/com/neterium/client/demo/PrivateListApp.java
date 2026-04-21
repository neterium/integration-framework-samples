package com.neterium.client.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


/**
 * Application bootstrap
 *
 * @author Bernard Ligny
 */
@SpringBootApplication
@EnableCaching
@Slf4j
public class PrivateListApp {

    public static void main(String[] args) {
        SpringApplication.run(PrivateListApp.class, args);
    }

}