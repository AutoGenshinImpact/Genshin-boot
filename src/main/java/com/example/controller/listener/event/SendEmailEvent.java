package com.example.controller.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 * 发送邮件事件
 */
public class SendEmailEvent extends ApplicationEvent {


    public SendEmailEvent(Object source) {
        super(source);
    }
}
