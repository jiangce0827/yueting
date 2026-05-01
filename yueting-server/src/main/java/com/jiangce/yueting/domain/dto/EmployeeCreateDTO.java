package com.jiangce.yueting.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 员工创建 DTO
 */
@Data
public class EmployeeCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 是否启用（1-是，0-否）
     */
    private Integer isActive;

    /**
     * 角色ID列表
     */
    private List<String> roleIds;
}