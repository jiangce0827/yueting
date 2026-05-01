package com.jiangce.yueting.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建菜单请求DTO
 */
@Data
public class SysMenuCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID，0表示顶级菜单
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 显示顺序，数字越小越靠前
     */
    private Integer sort;

    /**
     * 菜单类型：1-目录，2-菜单，3-按钮
     */
    private Integer type;

    /**
     * 状态：0-正常，1-禁用
     */
    private Integer status;
}