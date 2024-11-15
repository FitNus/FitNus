package com.sparta.modulebatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.sparta.modulecommon", "com.sparta.modulebatch"})
@SpringBootApplication
public class ModuleBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleBatchApplication.class, args);
    }

}
