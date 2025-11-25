package com.backendsems;

import com.backendsems.iam.application.commands.SeedRolesCommand;
import com.backendsems.iam.application.commandhandlers.SeedRolesCommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendSemsApplication implements CommandLineRunner {

    private final SeedRolesCommandHandler seedRolesCommandHandler;

    public BackendSemsApplication(SeedRolesCommandHandler seedRolesCommandHandler) {
        this.seedRolesCommandHandler = seedRolesCommandHandler;
    }

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void run(String... args) throws Exception {
        // Seed roles on startup
        seedRolesCommandHandler.handle(new SeedRolesCommand());

        System.out.println("Server starting on port " + port);
        System.out.println("Swagger UI available at: http://localhost:" + port + "/swagger-ui.html");
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendSemsApplication.class, args);
    }

}
