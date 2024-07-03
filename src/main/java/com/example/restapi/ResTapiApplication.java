package com.example.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ResTapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResTapiApplication.class, args);
    }
}
