package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工 VO
 */
@Data
public class EmployeeVO implements Serializable {

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
     * 真实姓名
     */
    private String realName;

    /**
     * 是否启用（1-是，0-否）
     */
    private Integer isActive;

    /**
     * 是否启用描述
     */
    private String isActiveDesc;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 角色ID列表
     */
    private List<String> roleIds;

    /**
     * 角色名称列表
     */
    private List<String> roleNames;
}