package com.thejavaloom.prodbugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
public class ProdBugsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdBugsApplication.class, args);
    }
}
