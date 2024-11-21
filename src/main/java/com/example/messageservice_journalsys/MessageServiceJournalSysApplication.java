package com.example.messageservice_journalsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MessageServiceJournalSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageServiceJournalSysApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() { return new RestTemplate();}


}
