package com.jiangce.yueting.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 管理员角色分配 VO
 */
@Data
public class EmployeeRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    private String employeeId;

    /**
     * 角色ID列表
     */
    private List<String> roleIds;
}