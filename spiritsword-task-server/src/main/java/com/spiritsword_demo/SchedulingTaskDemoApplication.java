package com.spiritsword_demo;

import com.spiritsword.config.EnableSpiritswordSchedulingServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSpiritswordSchedulingServer
public class SchedulingTaskDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulingTaskDemoApplication.class, args);
    }
}
