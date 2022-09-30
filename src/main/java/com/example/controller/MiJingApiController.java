package com.example.controller;

import com.example.entity.data.MiJingDetail;
import com.example.entity.repo.RestBean;
import com.example.entity.repo.RestBeanBuilder;
import com.example.entity.repo.ResultCode;
import com.example.service.MijingService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Api(tags = "秘境服务", description = "储存秘境信息，获取秘境状态")
@RestController
@RequestMapping("/api/MiJing")
public class MiJingApiController {

    @Resource
    MijingService mijingService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "MiJingList", paramType = "query", dataType = "string", dataTypeClass = String.class,example = "user"),
            @ApiImplicitParam(name = "numberList", paramType = "query",  dataType = "string",dataTypeClass = String.class, example = "123456"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "存储成功"),
            @ApiResponse(code = 400, message = "存储失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "储存用户的秘境信息", notes = "将用户的秘境信息储存在mysql")
    @PostMapping("/storeList")
    public RestBean<Object> storeList(@RequestParam(value = "MiJingList") List<Integer> MiJingList,@RequestParam(value = "numberList") List<Integer> numberList) {
        return mijingService.storeList((MiJingList.toArray(new Integer[0])),numberList.toArray(new Integer[0]))?
                RestBeanBuilder.builder().code(ResultCode.STORE_SUCCESS).build().ToRestBean():
                RestBeanBuilder.builder().code(ResultCode.STORE_FAILURE).build().ToRestBean();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "获取失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "获取用户的秘境信息", notes = "从mysql获取用户的秘境信息")
    @GetMapping("/selectList")
    public RestBean<Object> selectList() {
        MiJingDetail miJingDetail = mijingService.selectList();
        return RestBeanBuilder.builder().code(ResultCode.SELECT_SUCCESS).data(miJingDetail).build().ToRestBean();
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "获取失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "用户当前的秘境状态", notes = "从redis查询用户当前执行的秘境")
    @GetMapping("/checkStatus")
    public RestBean<Object> checkStatus() {
        String status = mijingService.checkStatus();
        return RestBeanBuilder.builder().code(ResultCode.SELECT_SUCCESS).data(status).build().ToRestBean();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "获取失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "检查用户是否正在执行脚本", notes = "从redis查询用户当前是否在执行脚本")
    @GetMapping("/checkIsStarting")
    public RestBean<Object> checkIsStarting() {
        int isStarting = mijingService.checkIsStarting();
        return RestBeanBuilder.builder().code(ResultCode.SELECT_SUCCESS).data(isStarting).build().ToRestBean();
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "获取失败,请查看日志"),
            @ApiResponse(code = 401, message = "没有权限")
    })
    @ApiOperation(value = "检查用户是否执行完脚本", notes = "从redis查询用户当前是否执行完脚本")
    @GetMapping("/checkIsFinish")
    public RestBean<Object> checkIsFinish() {
        String isFinish = mijingService.checkIsFinish();
        return RestBeanBuilder.builder().code(ResultCode.SELECT_SUCCESS).data(isFinish).build().ToRestBean();
    }


}
