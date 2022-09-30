package com.example.controller;

import com.example.controller.exception.NotExistInMysqlException;
import com.example.dao.AuthMapper;
import com.example.entity.constant.ThreadDetails;
import com.example.entity.data.UserDetail;
import com.example.entity.repo.RestBean;
import com.example.entity.repo.RestBeanBuilder;
import com.example.entity.repo.ResultCode;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "用户服务", description = "修改用户信息，查询用户信息等")
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Resource
    AuthMapper authMapper;



    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "获取失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "获取用户详细信息", notes = "获取用户的信息，游戏信息等")
    @GetMapping("/getUserDetails")
    public RestBean<UserDetail> getUserDetails() {
        //获取用户
        String name = ThreadDetails.getUsername();
        UserDetail userDetail = authMapper.findUserByUsername(name).orElseThrow(() -> new NotExistInMysqlException("不存在该用户!"));
        return RestBeanBuilder.<UserDetail>builder().code(ResultCode.REGISTER_SUCCESS).data(userDetail).build().ToRestBean();
    }






}
