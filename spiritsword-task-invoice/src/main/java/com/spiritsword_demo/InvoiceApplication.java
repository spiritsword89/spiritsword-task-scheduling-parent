package com.spiritsword_demo;

import com.spiritsword.config.EnableSpiritswordTaskSchedulingExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSpiritswordTaskSchedulingExecutor
public class InvoiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvoiceApplication.class, args);
    }
}
