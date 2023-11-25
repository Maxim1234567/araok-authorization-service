package ru.araok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
public class AraokAuthorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AraokAuthorizationServiceApplication.class, args);
    }

}
