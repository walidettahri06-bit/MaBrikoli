package com.mabrikoli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Mabrikoli API.
 */
@SpringBootApplication
@EnableJpaAuditing
public class MabrikoliApplication {

    public static void main(String[] args) {
        SpringApplication.run(MabrikoliApplication.class, args);
    }
}
