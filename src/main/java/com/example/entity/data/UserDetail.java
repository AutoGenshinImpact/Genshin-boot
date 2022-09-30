package com.example.entity.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@ApiModel("用户信息类")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail implements Serializable {
    @ApiModelProperty("用户id")
    int id;
    @ApiModelProperty("用户名")
    String username;
    @ApiModelProperty("用户密码")
    String password;
    @ApiModelProperty("用户权限")
    String role;

}
