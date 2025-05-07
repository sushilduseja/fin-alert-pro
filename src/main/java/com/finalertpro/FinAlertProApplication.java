package com.finalertpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinAlertProApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinAlertProApplication.class, args);
    }
}
