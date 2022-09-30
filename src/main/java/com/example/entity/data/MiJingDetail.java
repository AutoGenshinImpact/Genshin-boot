package com.example.entity.data;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("秘境信息类")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiJingDetail {
    @ApiModelProperty("用户名")
    String username;
    @ApiModelProperty("数据库接收的秘境列表")
    String list;
    @ApiModelProperty("数据库接收的秘境数量列表")
    String numberList;
    @ApiModelProperty("秘境列表")
    List<Integer> toList;
    @ApiModelProperty("秘境数量列表")
    List<Integer> toNumberList;

    /**
     * 将格式转为返回形式
     */
    public MiJingDetail changeToResponse() {
        this.toList=JSON.parseArray(list, Integer.class);
        this.toNumberList=JSON.parseArray(numberList, Integer.class);
        return this;
    }
}
