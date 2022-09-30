package com.example.dao;

import com.example.entity.constant.MiJingList;
import com.example.entity.data.MiJingDetail;
import com.example.entity.data.UserDetail;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface MiJingMapper {
    @Insert("insert into MiJing(username,list,numberList) values (#{username},#{list},#{numberList})")
    boolean storeList(@Param("username") String username, @Param("list") String list,@Param("numberList") String numberList);

    @Results(id = "user-map" ,value = {
            @Result(column = "username",property = "username"),
            @Result(column = "list",property = "list"),
            @Result(column = "numberList",property = "numberList"),
    })
    @Select("select * from MiJing where username=#{username}")
    Optional<MiJingDetail> selectList(@Param("username") String username);

    @Update("update MiJing set list=#{list},numberList=#{numberList} where username=#{username}")
    boolean modifyList(@Param("list") String list, @Param("numberList") String numberList,@Param("username") String username);

}
