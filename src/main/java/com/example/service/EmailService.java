package com.example.service;

/**
 * 发送邮件服务
 */
public interface EmailService {

    /**
     * @param mail 邮件地址
     * @return 是否发送成功
     */
    boolean sendEmail(String mail);

    /**
     * @param mail 邮件地址
     * @return 是否退订成功
     */
    boolean unsubscribeEmail(String mail) throws Exception;

    /**
     * @param mail 邮件地址
     * @return 是否续订成功
     */
    boolean renewEmail(String mail) throws Exception;
}
