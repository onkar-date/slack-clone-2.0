package com.slack.clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Main Spring Boot application class for Slack Clone
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableMongoAuditing
public class SlackCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlackCloneApplication.class, args);
    }
}
