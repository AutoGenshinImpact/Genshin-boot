package com.example.service.impl;

import com.example.controller.exception.NotExistInMysqlException;
import com.example.controller.listener.event.SendEmailEvent;
import com.example.dao.MiJingMapper;
import com.example.entity.constant.ThreadDetails;
import com.example.entity.data.MiJingDetail;
import com.example.service.MijingService;
import com.example.service.util.RedisTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class MijingServiceImpl implements MijingService , ApplicationEventPublisherAware {

    @Resource
    MiJingMapper miJingMapper;
    @Resource
    RedisTemplate<Object, Object> redisTemplate;
    @Resource
    RedisTools<String> redisTools;
    @Resource
    RedisTools<Object> objRedisTools;

    private static final String MIJING_TOKEN = "MiJing:";
    private static final String MIJING_IS_START_TOKEN = "MiJing:finish:";

    public ApplicationEventPublisher applicationEventPublisher;

    @Override
    public boolean storeList(Integer[] list, Integer[] numberList) {
        try {
            String listJSON = new ObjectMapper().writeValueAsString(list);
            String numberListJSON = new ObjectMapper().writeValueAsString(numberList);
            String username = ThreadDetails.getUsername();
            Optional<MiJingDetail> miJingDetail = miJingMapper.selectList(username);
            if (!miJingDetail.isPresent()) {
                return miJingMapper.storeList(username, listJSON, numberListJSON);
            }
            return miJingMapper.modifyList(listJSON, numberListJSON, username);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public MiJingDetail selectList() {
        String username = ThreadDetails.getUsername();
        Optional<MiJingDetail> miJingList = miJingMapper.selectList(username);
        MiJingDetail miJingDetail = miJingList.orElseThrow(() -> new NotExistInMysqlException("该用户没有添加过秘境"));
        return miJingDetail.changeToResponse();

    }

    @Override
    public String checkStatus() {
        String username = ThreadDetails.getUsername();

        String status = redisTools.getStringFromRedis(MIJING_TOKEN + username, "该用户当前没有执行秘境!");

        return status;
    }



    @Override
    public boolean checkIsFinish() {
        String username = ThreadDetails.getUsername();
        Object isFinish = objRedisTools.getFromRedis(MIJING_IS_START_TOKEN + username, "该脚本还在执行!");
        redisTools.deleteFromRedis(MIJING_IS_START_TOKEN + username);
        //发布发送邮件事件\
        applicationEventPublisher.publishEvent(new SendEmailEvent(username));

        return true;
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
