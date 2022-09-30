package com.example;

import com.alibaba.fastjson.JSON;
import com.example.config.ApplicationPro;
import com.example.controller.exception.NotExistInMysqlException;
import com.example.dao.AuthMapper;
import com.example.service.VerifyService;
import com.example.service.util.RedisTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
class GenshinApplicationTests {




    @Test
    public void contextLoads() throws Exception {
        int[] demo = {0, 1, 2, 3, 4};
        String demoJson = new ObjectMapper().writeValueAsString(demo);
        System.out.println(demoJson);
        List<Integer> integers = JSON.parseArray(demoJson, Integer.class);
        System.out.println(integers);


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
            System.out.println(this.name+ " continue running...");
        }
    }

    class Worker extends Thread{
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























