package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.RoleCreateDTO;
import com.jiangce.yueting.domain.dto.RoleUpdateDTO;
import com.jiangce.yueting.domain.po.EmployeeRole;
import com.jiangce.yueting.domain.po.Role;
import com.jiangce.yueting.domain.po.RoleMenu;
import com.jiangce.yueting.domain.vo.EmployeeRoleVO;
import com.jiangce.yueting.domain.vo.RoleVO;
import com.jiangce.yueting.mapper.EmployeeRoleMapper;
import com.jiangce.yueting.mapper.RoleMapper;
import com.jiangce.yueting.mapper.RoleMenuMapper;
import com.jiangce.yueting.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    private final RoleMenuMapper roleMenuMapper;

    private final EmployeeRoleMapper employeeRoleMapper;

    @Override
    public IPage<RoleVO> pageRole(Integer current, Integer size) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getCreatedAt);
        IPage<Role> rolePage = roleMapper.selectPage(page, wrapper);
        return rolePage.convert(this::convertToVO);
    }

    @Override
    public RoleVO getRoleById(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new CustomException("角色不存在");
        }
        RoleVO vo = convertToVO(role);
        // 设置菜单ID列表
        vo.setMenuIds(getMenuIdsByRoleId(roleId));
        return vo;
    }

    @Override
    @Transactional
    public void createRole(RoleCreateDTO createDTO) {
        // 检查角色标识是否重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, createDTO.getRoleKey());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new CustomException("角色标识已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(createDTO, role);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(role);

        // 保存角色菜单关联
        if (createDTO.getMenuIds() != null && !createDTO.getMenuIds().isEmpty()) {
            saveRoleMenus(role.getRoleId(), createDTO.getMenuIds());
        }
        log.info("创建角色成功: {}", role.getRoleName());
    }

    @Override
    @Transactional
    public void updateRole(RoleUpdateDTO updateDTO) {
        Role role = roleMapper.selectById(updateDTO.getRoleId());
        if (role == null) {
            throw new CustomException("角色不存在");
        }
        // 检查角色标识是否重复（排除自身）
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, updateDTO.getRoleKey()).ne(Role::getRoleId, updateDTO.getRoleId());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new CustomException("角色标识已存在");
        }
        BeanUtils.copyProperties(updateDTO, role);
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(role);

        // 更新角色菜单关联
        LambdaQueryWrapper<RoleMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(RoleMenu::getRoleId, updateDTO.getRoleId());
        roleMenuMapper.delete(menuWrapper);
        if (updateDTO.getMenuIds() != null && !updateDTO.getMenuIds().isEmpty()) {
            saveRoleMenus(role.getRoleId(), updateDTO.getMenuIds());
        }
        log.info("更新角色成功: {}", role.getRoleName());
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new CustomException("角色不存在");
        }
        roleMapper.deleteById(roleId);
        log.info("删除角色成功: {}", role.getRoleName());
    }

    @Override
    public List<RoleVO> listAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return roles.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream().map(RoleMenu::getMenuId).toList();
    }

    @Override
    public List<Long> getRoleIdsByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<EmployeeRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeRole::getEmployeeId, employeeId);
        List<EmployeeRole> employeeRoles = employeeRoleMapper.selectList(wrapper);
        return employeeRoles.stream().map(EmployeeRole::getRoleId).toList();
    }

    @Override
    @Transactional
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除原有关联
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);
        // 保存新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            saveRoleMenus(roleId, menuIds);
        }
        log.info("分配角色菜单成功: roleId={}", roleId);
    }

    @Override
    @Transactional
    public void assignEmployeeRoles(EmployeeRoleVO employeeRoleVO) {
        // 删除原有关联
        LambdaQueryWrapper<EmployeeRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeRole::getEmployeeId, Long.parseLong(employeeRoleVO.getEmployeeId()));
        employeeRoleMapper.delete(wrapper);
        // 保存新关联
        if (employeeRoleVO.getRoleIds() != null && !employeeRoleVO.getRoleIds().isEmpty()) {
            for (String roleIdStr : employeeRoleVO.getRoleIds()) {
                EmployeeRole er = new EmployeeRole();
                er.setEmployeeId(Long.parseLong(employeeRoleVO.getEmployeeId()));
                er.setRoleId(Long.parseLong(roleIdStr));
                employeeRoleMapper.insert(er);
            }
        }
        log.info("分配管理员角色成功: employeeId={}", employeeRoleVO.getEmployeeId());
    }

    /**
     * 保存角色菜单关联
     */
    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        for (Long menuId : menuIds) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
    }

    /**
     * 转换为VO
     */
    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setRoleId(role.getRoleId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setCreatedAt(role.getCreatedAt());
        vo.setUpdatedAt(role.getUpdatedAt());
        vo.setStatusDesc(getStatusDesc(role.getStatus()));
        return vo;
    }

    private String getStatusDesc(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "正常";
            case 1 -> "禁用";
            default -> "未知";
        };
    }

    @Override
    public List<String> getRoleNamesByRoleIds(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIdsLong = roleIds.stream().map(Long::parseLong).toList();
        List<Role> roles = roleMapper.selectBatchIds(roleIdsLong);
        return roles.stream().map(Role::getRoleName).toList();
    }
}