package com.sparta.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.sparta")
public class AuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionApplication.class, args);
    }

}
