package com.jiangce.yueting.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色更新 DTO
 */
@Data
public class RoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;
}