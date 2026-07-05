package com.backendsems;

import com.backendsems.iam.domain.model.commands.SeedRolesCommand;
import com.backendsems.iam.domain.services.RoleCommandService;
import com.backendsems.news.domain.model.commands.SeedNewsItemsCommand;
import com.backendsems.news.domain.services.NewsItemCommandService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendSemsApplication implements CommandLineRunner {

    private final RoleCommandService roleCommandService;
    private final NewsItemCommandService newsItemCommandService;

    public BackendSemsApplication(RoleCommandService roleCommandService, NewsItemCommandService newsItemCommandService) {
        this.roleCommandService = roleCommandService;
        this.newsItemCommandService = newsItemCommandService;
    }

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void run(String... args) throws Exception {
        // Seed roles on startup
        roleCommandService.handle(new SeedRolesCommand());

        // Seed standard news and tips on startup
        newsItemCommandService.handle(new SeedNewsItemsCommand());

        System.out.println("Server starting on port " + port);
        System.out.println("Swagger UI available at: http://localhost:" + port + "/swagger-ui.html");
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendSemsApplication.class, args);
    }

}
