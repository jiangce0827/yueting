package com.jiangce.yueting.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 员工更新 DTO
 */
@Data
public class EmployeeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    private String employeeId;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码（为空则不修改）
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