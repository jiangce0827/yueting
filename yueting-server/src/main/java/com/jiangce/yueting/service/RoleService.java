package com.jiangce.yueting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangce.yueting.domain.dto.RoleCreateDTO;
import com.jiangce.yueting.domain.dto.RoleUpdateDTO;
import com.jiangce.yueting.domain.vo.EmployeeRoleVO;
import com.jiangce.yueting.domain.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     */
    IPage<RoleVO> pageRole(Integer current, Integer size);

    /**
     * 获取角色详情
     */
    RoleVO getRoleById(Long roleId);

    /**
     * 创建角色
     */
    void createRole(RoleCreateDTO createDTO);

    /**
     * 更新角色
     */
    void updateRole(RoleUpdateDTO updateDTO);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 获取所有角色（用于下拉选择）
     */
    List<RoleVO> listAllRoles();

    /**
     * 根据角色ID获取菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 获取管理员的角色ID列表
     */
    List<Long> getRoleIdsByEmployeeId(Long employeeId);

    /**
     * 分配角色菜单
     */
    void assignRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 分配管理员角色
     */
    void assignEmployeeRoles(EmployeeRoleVO employeeRoleVO);

    /**
     * 根据角色ID列表获取角色名称列表
     */
    List<String> getRoleNamesByRoleIds(List<String> roleIds);
}