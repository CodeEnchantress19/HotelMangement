package com.example.hotelback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.CacheControl.maxAge;

@SpringBootApplication
@EnableJpaRepositories("com.*")
@ComponentScan(basePackages = { "com.*" })
@EntityScan("com.example.hotelback.*")
@ComponentScan(basePackages = "com.example.hotelback") // Add your package here

public class HotelBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBackApplication.class, args);
    }




}
