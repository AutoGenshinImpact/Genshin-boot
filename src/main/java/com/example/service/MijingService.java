package com.example.service;

import com.example.entity.data.MiJingDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 秘境相关接口
 */
public interface MijingService {

    /**
     * @param list 秘境列表
     * @return 是否添加成功
     */
    boolean storeList(Integer[] list,Integer[] numberList);

    /**
     * @return 用户的秘境列表
     */
    MiJingDetail selectList();

    /**
     * @return 用户正在进行的秘境信息
     */
    String checkStatus();

    /**
     * @return 检查用户是否正在执行脚本
     */
    int checkIsStarting();

    /**
     * @return 检查用户的脚本是否结束
     */
    int checkIsFinish();
}
