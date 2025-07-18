package com.spiritsword_demo.test;

import com.spiritsword_demo.job.handler.PaymentUpdateHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class DemoTest {

    @Test
    public void run() {
        String name = PaymentUpdateHandler.class.getName();
        System.out.println(name);
    }
}
