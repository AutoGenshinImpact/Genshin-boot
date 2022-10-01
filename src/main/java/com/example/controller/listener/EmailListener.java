package com.example.controller.listener;

import com.example.controller.listener.event.SendEmailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Slf4j
@Component
public class EmailListener implements ServletContextAware {

    private ServletContext servletContext;


    @EventListener
    public void sendEmail(SendEmailEvent event) {
        log.info("{}监听到事件源: {}", EmailListener.class.getName(), event.getSource());

    }



    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
