package com.example.dao;


import com.example.config.RedisMybatisCache;
import com.example.entity.data.UserDetail;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
//@CacheNamespace(implementation = RedisMybatisCache.class)
public interface AuthMapper {

    @Insert("insert into users(username,password,role) values (#{username},#{password},#{role})")
    boolean registerUser(@Param("username") String username, @Param("password") String password, @Param("role") String role);

    @Results(id = "user-map" ,value = {
            @Result(column = "id",property = "id"),
            @Result(column = "username",property = "username"),
               })
    @Select("select * from users where username=#{username}")
    Optional<UserDetail> findUserByUsername(@Param("username") String username);





}
