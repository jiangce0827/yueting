package com.jiangce.yueting.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单分页查询DTO
 */
@Data
public class SysMenuQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 菜单名称（模糊查询）
     */
    private String name;

    /**
     * 状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 菜单类型：1-目录，2-菜单，3-按钮
     */
    private Integer type;
}