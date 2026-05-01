package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.SysMenuCreateDTO;
import com.jiangce.yueting.domain.dto.SysMenuQueryDTO;
import com.jiangce.yueting.domain.dto.SysMenuUpdateDTO;
import com.jiangce.yueting.domain.po.SysMenu;
import com.jiangce.yueting.domain.po.RoleMenu;
import com.jiangce.yueting.domain.vo.SysMenuTreeVO;
import com.jiangce.yueting.domain.vo.SysMenuVO;
import com.jiangce.yueting.mapper.RoleMenuMapper;
import com.jiangce.yueting.mapper.SysMenuMapper;
import com.jiangce.yueting.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public IPage<SysMenuVO> pageMenuVO(SysMenuQueryDTO queryDTO) {
        Page<SysMenu> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(SysMenu::getName, queryDTO.getName());
        }
        if (queryDTO.getStatus() != null && queryDTO.getStatus() >= 0) {
            wrapper.eq(SysMenu::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getType() != null && queryDTO.getType() > 0) {
            wrapper.eq(SysMenu::getType, queryDTO.getType());
        }

        wrapper.orderByAsc(SysMenu::getSort).orderByDesc(SysMenu::getCreatedAt);

        IPage<SysMenu> menuPage = sysMenuMapper.selectPage(page, wrapper);
        IPage<SysMenuVO> voPage = menuPage.convert(this::convertToVO);

        // 补充父菜单名称
        Map<Long, String> parentNameMap = sysMenuMapper.selectList(null).stream()
                .collect(Collectors.toMap(SysMenu::getId, SysMenu::getName, (a, b) -> a));
        for (SysMenuVO vo : voPage.getRecords()) {
            if (vo.getParentId() != null && vo.getParentId() > 0) {
                vo.setParentName(parentNameMap.getOrDefault(vo.getParentId(), "未知"));
            } else {
                vo.setParentName("顶级菜单");
            }
        }

        return voPage;
    }

    @Override
    public List<SysMenuTreeVO> getMenuTree() {
        List<SysMenuTreeVO> allMenus = sysMenuMapper.listAllTree();
        return buildTree(allMenus, 0L);
    }

    @Override
    public SysMenuVO getMenuById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new CustomException("菜单不存在");
        }
        SysMenuVO vo = convertToVO(menu);
        if (menu.getParentId() != null && menu.getParentId() > 0) {
            SysMenu parent = sysMenuMapper.selectById(menu.getParentId());
            vo.setParentName(parent != null ? parent.getName() : "未知");
        } else {
            vo.setParentName("顶级菜单");
        }
        return vo;
    }

    @Override
    @Transactional
    public void createMenu(SysMenuCreateDTO createDTO) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(createDTO, menu);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        sysMenuMapper.insert(menu);
        log.info("创建菜单成功: {}", menu.getName());
    }

    @Override
    @Transactional
    public void updateMenu(SysMenuUpdateDTO updateDTO) {
        SysMenu menu = sysMenuMapper.selectById(updateDTO.getId());
        if (menu == null) {
            throw new CustomException("菜单不存在");
        }
        BeanUtils.copyProperties(updateDTO, menu);
        menu.setUpdatedAt(LocalDateTime.now());
        sysMenuMapper.updateById(menu);
        log.info("更新菜单成功: {}", menu.getName());
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new CustomException("菜单不存在");
        }
        // 检查是否有子菜单
        long childCount = sysMenuMapper.countByParentId(id);
        if (childCount > 0) {
            throw new CustomException("该菜单下存在子菜单，请先删除子菜单");
        }
        sysMenuMapper.deleteById(id);
        log.info("删除菜单成功: {}", menu.getName());
    }

    @Override
    public List<SysMenuVO> listParentMenus() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, 0).orderByAsc(SysMenu::getSort);
        List<SysMenu> topMenus = sysMenuMapper.selectList(wrapper);
        return topMenus.stream().map(this::convertToVO).toList();
    }

    /**
     * 构建树形结构
     */
    private List<SysMenuTreeVO> buildTree(List<SysMenuTreeVO> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .sorted((a, b) -> {
                    if (a.getSort() == null) a.setSort(0);
                    if (b.getSort() == null) b.setSort(0);
                    return a.getSort().compareTo(b.getSort());
                })
                .peek(menu -> {
                    List<SysMenuTreeVO> children = buildTree(allMenus, menu.getId());
                    if (!children.isEmpty()) {
                        menu.setChildren(children);
                    } else {
                        menu.setChildren(new ArrayList<>());
                    }
                })
                .toList();
    }

    /**
     * 转换为VO
     */
    private SysMenuVO convertToVO(SysMenu menu) {
        SysMenuVO vo = new SysMenuVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setType(menu.getType());
        vo.setStatus(menu.getStatus());
        vo.setCreatedAt(menu.getCreatedAt());
        vo.setUpdatedAt(menu.getUpdatedAt());
        vo.setTypeDesc(getTypeDesc(menu.getType()));
        vo.setStatusDesc(getStatusDesc(menu.getStatus()));
        return vo;
    }

    private String getTypeDesc(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "目录";
            case 2 -> "菜单";
            case 3 -> "按钮";
            default -> "未知";
        };
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
    public List<SysMenuTreeVO> getMenuTreeByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 根据角色ID列表获取菜单ID列表
        LambdaQueryWrapper<RoleMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(RoleMenu::getRoleId, roleIds);
        List<Long> menuIds = roleMenuMapper.selectList(menuWrapper).stream()
                .map(RoleMenu::getMenuId)
                .distinct()
                .toList();
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 获取所有菜单
        List<SysMenuTreeVO> allMenus = sysMenuMapper.listAllTree();
        // 找出所有需要保留的菜单ID（包括父级菜单）
        List<Long> allMenuIds = sysMenuMapper.selectList(null).stream()
                .map(SysMenu::getId)
                .toList();
        List<Long> menusToKeep = new ArrayList<>(menuIds);
        // 递归添加所有父级菜单
        for (Long menuId : menuIds) {
            addParentMenus(menuId, allMenus, menusToKeep);
        }
        // 过滤菜单，保留有权限的菜单及其父级
        List<SysMenuTreeVO> filteredMenus = allMenus.stream()
                .filter(menu -> menusToKeep.contains(menu.getId()))
                .toList();
        return buildTree(filteredMenus, 0L);
    }

    /**
     * 递归添加父级菜单ID
     */
    private void addParentMenus(Long menuId, List<SysMenuTreeVO> allMenus, List<Long> menusToKeep) {
        for (SysMenuTreeVO menu : allMenus) {
            if (menu.getId().equals(menuId)) {
                if (menu.getParentId() != null && menu.getParentId() != 0) {
                    if (!menusToKeep.contains(menu.getParentId())) {
                        menusToKeep.add(menu.getParentId());
                        addParentMenus(menu.getParentId(), allMenus, menusToKeep);
                    }
                }
                break;
            }
        }
    }
}