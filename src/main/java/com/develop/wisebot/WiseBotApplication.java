package com.develop.wisebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WiseBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiseBotApplication.class, args);
    }

}
