package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.SysMenu;
import com.jiangce.yueting.domain.vo.SysMenuTreeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 获取所有菜单（用于构建树形结构）
     */
    default List<SysMenuTreeVO> listAllTree() {
        return selectList(null).stream().map(menu -> {
            SysMenuTreeVO vo = new SysMenuTreeVO();
            vo.setId(menu.getId());
            vo.setParentId(menu.getParentId());
            vo.setName(menu.getName());
            vo.setPath(menu.getPath());
            vo.setComponent(menu.getComponent());
            vo.setIcon(menu.getIcon());
            vo.setSort(menu.getSort());
            vo.setType(menu.getType());
            vo.setStatus(menu.getStatus());
            vo.setTypeDesc(getTypeDesc(menu.getType()));
            vo.setStatusDesc(getStatusDesc(menu.getStatus()));
            return vo;
        }).toList();
    }

    private static String getTypeDesc(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "目录";
            case 2 -> "菜单";
            default -> "按钮";
        };
    }

    private static String getStatusDesc(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "正常";
            case 1 -> "禁用";
            default -> "未知";
        };
    }

    /**
     * 根据父ID查询子菜单数量
     */
    default long countByParentId(Long parentId) {
        return selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, parentId));
    }
}