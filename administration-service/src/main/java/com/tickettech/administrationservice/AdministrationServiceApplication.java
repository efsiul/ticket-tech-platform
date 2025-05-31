package com.tickettech.administrationservice;

import com.tickettech.administrationservice.service.implementation.LoadData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AdministrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdministrationServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(LoadData loadData){
        return args -> {
            loadData.seedData();
            System.out.println("SeedDataBase OK");
        };
    }
}