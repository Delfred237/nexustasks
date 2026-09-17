package com.nexustasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NexusTasksApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusTasksApplication.class, args);
    }
}