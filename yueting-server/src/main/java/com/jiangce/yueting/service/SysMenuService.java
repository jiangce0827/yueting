package com.jiangce.yueting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangce.yueting.domain.dto.SysMenuCreateDTO;
import com.jiangce.yueting.domain.dto.SysMenuQueryDTO;
import com.jiangce.yueting.domain.dto.SysMenuUpdateDTO;
import com.jiangce.yueting.domain.vo.SysMenuVO;
import com.jiangce.yueting.domain.vo.SysMenuTreeVO;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface SysMenuService {

    /**
     * 分页查询菜单列表
     */
    IPage<SysMenuVO> pageMenuVO(SysMenuQueryDTO queryDTO);

    /**
     * 获取菜单树形结构
     */
    List<SysMenuTreeVO> getMenuTree();

    /**
     * 获取菜单详情
     */
    SysMenuVO getMenuById(Long id);

    /**
     * 创建菜单
     */
    void createMenu(SysMenuCreateDTO createDTO);

    /**
     * 更新菜单
     */
    void updateMenu(SysMenuUpdateDTO updateDTO);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);

    /**
     * 获取所有父级菜单（用于下拉选择）
     */
    List<SysMenuVO> listParentMenus();

    /**
     * 根据角色ID列表获取菜单树形结构
     */
    List<SysMenuTreeVO> getMenuTreeByRoleIds(List<Long> roleIds);
}