package com.backendsems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendSemsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendSemsApplication.class, args);
    }

}
