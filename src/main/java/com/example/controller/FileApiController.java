package com.example.controller;

import com.example.service.util.IpTools;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Api(tags = "下载文件服务", description = "下载秘境环境等文件")
@RestController
@RequestMapping("/api/download")
public class FileApiController {

    @SneakyThrows
    @GetMapping("/MiJing")
    public void downloadMiJing(HttpServletRequest request, HttpServletResponse response) {
        response.reset();
        String fileName = "environment.zip";
        try(InputStream resource = Resources.getResourceAsStream("environment.zip");) {
            byte[] buffer = new byte[resource.available()];

            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            try (BufferedInputStream bis = new BufferedInputStream(resource)){
                ServletOutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer,0,i);
                    i = bis.read(buffer);
                }
                log.info("Ip为[" + IpTools.getIpAddress(request) + "] download [" + fileName + "] success!");
            }
        }
    }





}
