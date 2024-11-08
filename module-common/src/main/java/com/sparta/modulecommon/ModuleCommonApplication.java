package com.sparta.modulecommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ModuleCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleCommonApplication.class, args);
    }

}
