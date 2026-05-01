package com.jiangce.yueting.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangce.yueting.domain.dto.RoleCreateDTO;
import com.jiangce.yueting.domain.dto.RoleUpdateDTO;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.EmployeeRoleVO;
import com.jiangce.yueting.domain.vo.RoleVO;
import com.jiangce.yueting.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
@Tag(name = "角色管理")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping
    public Result<PageResult> pageRole(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<RoleVO> page = roleService.pageRole(current, size);
        PageResult result = new PageResult(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<RoleVO> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {
        RoleVO role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Void> createRole(@RequestBody RoleCreateDTO createDTO) {
        roleService.createRole(createDTO);
        return Result.success(null);
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public Result<Void> updateRole(@RequestBody RoleUpdateDTO updateDTO) {
        roleService.updateRole(updateDTO);
        return Result.success(null);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success(null);
    }

    @Operation(summary = "获取所有角色（用于下拉选择）")
    @GetMapping("/all")
    public Result<List<RoleVO>> listAllRoles() {
        List<RoleVO> roles = roleService.listAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "分配角色菜单")
    @PutMapping("/{roleId}/menus")
    public Result<Void> assignRoleMenus(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        roleService.assignRoleMenus(roleId, menuIds);
        return Result.success(null);
    }

    @Operation(summary = "获取管理员的角色ID列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<Long>> getEmployeeRoles(@Parameter(description = "管理员ID") @PathVariable Long employeeId) {
        List<Long> roleIds = roleService.getRoleIdsByEmployeeId(employeeId);
        return Result.success(roleIds);
    }

    @Operation(summary = "分配管理员角色")
    @PutMapping("/employee")
    public Result<Void> assignEmployeeRoles(@RequestBody EmployeeRoleVO employeeRoleVO) {
        roleService.assignEmployeeRoles(employeeRoleVO);
        return Result.success(null);
    }
}