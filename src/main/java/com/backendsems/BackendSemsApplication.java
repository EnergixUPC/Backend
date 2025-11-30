package com.backendsems;

import com.backendsems.iam.domain.model.commands.SeedRolesCommand;
import com.backendsems.iam.domain.services.RoleCommandService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendSemsApplication implements CommandLineRunner {

    private final RoleCommandService roleCommandService;

    public BackendSemsApplication(RoleCommandService roleCommandService) {
        this.roleCommandService = roleCommandService;
    }

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void run(String... args) throws Exception {
        // Seed roles on startup
        roleCommandService.handle(new SeedRolesCommand());

        System.out.println("Server starting on port " + port);
        System.out.println("Swagger UI available at: http://localhost:" + port + "/swagger-ui.html");
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendSemsApplication.class, args);
    }

}
