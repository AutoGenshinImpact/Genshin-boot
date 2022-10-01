package com.example;

import com.example.service.EmailService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
class GenshinApplicationTests {


    @Resource
    EmailService emailService;

    @Test
    public void contextLoads() throws Exception {
        emailService.sendEmail("lmq122677@qq.com");

    }

    class WaitThread extends Thread {
        private String name;
        private CountDownLatch c;

        public WaitThread(String name, CountDownLatch c) {
            this.name = name;
            this.c = c;
        }

        @SneakyThrows
        @Override
        public void run() {
            System.out.println(this.name + " wait...");
            c.await();
            System.out.println(this.name + " continue running...");
        }
    }

    class Worker extends Thread {
        private String name;
        private CountDownLatch c;

        public Worker(String name, CountDownLatch c) {
            this.name = name;
            this.c = c;
        }

        @SneakyThrows
        @Override
        public void run() {
            System.out.println(this.name + " is running...");
            Thread.sleep(2);
            System.out.println(this.name + " is end.");
            c.countDown();

        }
    }

}























