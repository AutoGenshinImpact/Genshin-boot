package com.example.config;

import com.example.controller.filter.ExceptionFilter;
import com.example.service.util.RedisTools;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.annotation.Resource;
import javax.servlet.Filter;

@Configuration
public class FilterConfiguration {

    @Resource
    RedisTools<String> redisTools;




    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
