package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树形结构VO
 */
@Data
public class SysMenuTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 父菜单ID，0表示顶级菜单
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * 显示顺序
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

    /**
     * 菜单类型描述
     */
    private String typeDesc;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 子菜单列表
     */
    private List<SysMenuTreeVO> children;
}