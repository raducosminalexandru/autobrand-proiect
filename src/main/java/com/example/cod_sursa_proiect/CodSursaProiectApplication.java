package com.example.cod_sursa_proiect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodSursaProiectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodSursaProiectApplication.class, args);
    }
}