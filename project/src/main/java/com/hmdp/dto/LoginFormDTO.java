package com.hmdp.dto;

import lombok.Data;

@Data
public class LoginFormDTO {
    private String phone;
    //验证码
    private String code;
    private String password;
}
