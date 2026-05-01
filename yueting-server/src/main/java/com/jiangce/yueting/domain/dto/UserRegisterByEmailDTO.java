package com.jiangce.yueting.domain.dto;

import lombok.Data;

@Data
public class UserRegisterByEmailDTO {
    private String email;
    private String code;
    private String username;
    private String password;
}
