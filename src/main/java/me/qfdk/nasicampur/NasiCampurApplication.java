package me.qfdk.nasicampur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class NasiCampurApplication {

    public static void main(String[] args) {
        SpringApplication.run(NasiCampurApplication.class, args);
    }
}
