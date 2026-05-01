package com.jiangce.yueting.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangce.yueting.domain.dto.SysMenuCreateDTO;
import com.jiangce.yueting.domain.dto.SysMenuQueryDTO;
import com.jiangce.yueting.domain.dto.SysMenuUpdateDTO;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.SysMenuVO;
import com.jiangce.yueting.domain.vo.SysMenuTreeVO;
import com.jiangce.yueting.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/menu")
@Tag(name = "菜单管理")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "分页查询菜单列表")
    @GetMapping
    public Result<PageResult> pageMenu(SysMenuQueryDTO queryDTO) {
        IPage<SysMenuVO> page = sysMenuService.pageMenuVO(queryDTO);
        PageResult result = new PageResult(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @Operation(summary = "获取菜单树形结构")
    @GetMapping("/tree")
    public Result<List<SysMenuTreeVO>> getMenuTree() {
        List<SysMenuTreeVO> tree = sysMenuService.getMenuTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public Result<SysMenuVO> getMenuById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        SysMenuVO menu = sysMenuService.getMenuById(id);
        return Result.success(menu);
    }

    @Operation(summary = "创建菜单")
    @PostMapping
    public Result<Void> createMenu(@RequestBody SysMenuCreateDTO createDTO) {
        sysMenuService.createMenu(createDTO);
        return Result.success(null);
    }

    @Operation(summary = "更新菜单")
    @PutMapping
    public Result<Void> updateMenu(@RequestBody SysMenuUpdateDTO updateDTO) {
        sysMenuService.updateMenu(updateDTO);
        return Result.success(null);
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@Parameter(description = "菜单ID") @PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success(null);
    }

    @Operation(summary = "获取父级菜单列表")
    @GetMapping("/parents")
    public Result<List<SysMenuVO>> listParentMenus() {
        List<SysMenuVO> parents = sysMenuService.listParentMenus();
        return Result.success(parents);
    }
}