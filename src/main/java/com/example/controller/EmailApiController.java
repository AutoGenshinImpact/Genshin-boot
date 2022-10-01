package com.example.controller;

import com.example.entity.constant.ThreadDetails;
import com.example.entity.data.MiJingDetail;
import com.example.entity.repo.RestBean;
import com.example.entity.repo.RestBeanBuilder;
import com.example.entity.repo.ResultCode;
import com.example.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 邮件相关接口
 */
@Slf4j
@Api(tags = "邮件服务", description = "邮件推定等内容")
@RestController
@RequestMapping("/api/email")
public class EmailApiController {

    @Resource
    EmailService emailService;

    @SneakyThrows
    @ApiResponses({
            @ApiResponse(code = 200, message = "退订成功"),
            @ApiResponse(code = 400, message = "退订失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "退订邮件提醒", notes = "将退订信息储存在redis30天，期间不会再发送邮件")
    @GetMapping("/unsubscribe")
    public RestBean<Object> unsubscribe() {
        String username = ThreadDetails.getUsername();
        emailService.unsubscribeEmail(username);
        return RestBeanBuilder.builder().code(ResultCode.UNSUBSCRIBE_SUCCESS).build().ToRestBean();
    }

    @SneakyThrows
    @ApiResponses({
            @ApiResponse(code = 200, message = "续订成功"),
            @ApiResponse(code = 400, message = "获取失败,请查看日志"),
            @ApiResponse(code = 401, message = "续订权限")
    })
    @ApiOperation(value = "续订邮件提醒", notes = "续订退订的邮件，继续发送")
    @GetMapping("/renew")
    public RestBean<Object> renew() {
        String username = ThreadDetails.getUsername();
        emailService.renewEmail(username);
        return RestBeanBuilder.builder().code(ResultCode.RENEW_SUCCESS).build().ToRestBean();
    }
}
