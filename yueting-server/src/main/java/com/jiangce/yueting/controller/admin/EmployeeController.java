package com.jiangce.yueting.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.EmployeeCreateDTO;
import com.jiangce.yueting.domain.dto.EmployeeUpdateDTO;
import com.jiangce.yueting.domain.po.Employee;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.EmployeeLoginVO;
import com.jiangce.yueting.domain.vo.EmployeeRoleVO;
import com.jiangce.yueting.domain.vo.EmployeeVO;
import com.jiangce.yueting.domain.vo.SysMenuTreeVO;
import com.jiangce.yueting.mapper.EmployeeMapper;
import com.jiangce.yueting.service.RoleService;
import com.jiangce.yueting.service.SysMenuService;
import com.jiangce.yueting.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("AdminEmployeeController")
@RequestMapping("/admin/employee")
@Tag(name = "管理员相关接口")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeMapper employeeMapper;

    private final RoleService roleService;

    private final SysMenuService sysMenuService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    @PreAuthorize("permitAll()")
    public Result<EmployeeLoginVO> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee employee = employeeMapper.selectOne(queryWrapper);
        if (employee == null || !passwordEncoder.matches(password, employee.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        // 生成JWT令牌
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        String token = JwtUtils.generateToken(employee.getEmployeeId().toString(), roles);

        // 获取该管理员的角色ID列表
        List<Long> roleIds = roleService.getRoleIdsByEmployeeId(employee.getEmployeeId());
        // 根据角色获取菜单树
        List<SysMenuTreeVO> menus = sysMenuService.getMenuTreeByRoleIds(roleIds);

        EmployeeLoginVO employeeLoginVO = new EmployeeLoginVO(String.valueOf(employee.getEmployeeId()), employee.getRealName(), employee.getIsActive(), employee.getLastLoginAt(), employee.getCreatedAt(), token, menus);
        return Result.success(employeeLoginVO);
    }

    @Operation(summary = "分页查询员工列表")
    @GetMapping("/list")
    public Result<PageResult> pageEmployee(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "员工姓名") @RequestParam(required = false) String realName) {
        Page<Employee> page = new Page<>(current, size);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(Employee::getRealName, realName);
        }
        wrapper.orderByDesc(Employee::getCreatedAt);
        IPage<Employee> employeePage = employeeMapper.selectPage(page, wrapper);

        // 转换为 VO
        List<EmployeeVO> voList = employeePage.getRecords().stream().map(this::convertToVO).toList();
        PageResult result = new PageResult(employeePage.getTotal(), voList);
        return Result.success(result);
    }

    @Operation(summary = "获取员工详情")
    @GetMapping("/{id}")
    public Result<EmployeeVO> getEmployeeById(@Parameter(description = "员工ID") @PathVariable String id) {
        Employee employee = employeeMapper.selectById(Long.parseLong(id));
        if (employee == null) {
            throw new CustomException("员工不存在");
        }
        return Result.success(convertToVO(employee));
    }

    @Operation(summary = "创建员工")
    @PostMapping
    public Result<Void> createEmployee(@RequestBody EmployeeCreateDTO createDTO) {
        // 检查账号是否重复
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, createDTO.getUsername());
        if (employeeMapper.selectCount(wrapper) > 0) {
            throw new CustomException("账号已存在");
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(createDTO, employee);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCreatedAt(LocalDateTime.now());
        employeeMapper.insert(employee);

        // 分配角色
        if (createDTO.getRoleIds() != null && !createDTO.getRoleIds().isEmpty()) {
            EmployeeRoleVO employeeRoleVO = new EmployeeRoleVO();
            employeeRoleVO.setEmployeeId(String.valueOf(employee.getEmployeeId()));
            employeeRoleVO.setRoleIds(createDTO.getRoleIds());
            roleService.assignEmployeeRoles(employeeRoleVO);
        }
        log.info("创建员工成功: {}", employee.getUsername());
        return Result.success(null);
    }

    @Operation(summary = "更新员工")
    @PutMapping
    public Result<Void> updateEmployee(@RequestBody EmployeeUpdateDTO updateDTO) {
        Employee employee = employeeMapper.selectById(Long.parseLong(updateDTO.getEmployeeId()));
        if (employee == null) {
            throw new CustomException("员工不存在");
        }
        // 检查账号是否重复（排除自身）
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, updateDTO.getUsername()).ne(Employee::getEmployeeId, Long.parseLong(updateDTO.getEmployeeId()));
        if (employeeMapper.selectCount(wrapper) > 0) {
            throw new CustomException("账号已存在");
        }
        BeanUtils.copyProperties(updateDTO, employee);
        // 如果密码不为空，则加密更新
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        } else {
            employee.setPassword(null); // 不更新密码
        }
        employeeMapper.updateById(employee);

        // 更新角色
        EmployeeRoleVO employeeRoleVO = new EmployeeRoleVO();
        employeeRoleVO.setEmployeeId(String.valueOf(employee.getEmployeeId()));
        employeeRoleVO.setRoleIds(updateDTO.getRoleIds());
        roleService.assignEmployeeRoles(employeeRoleVO);

        log.info("更新员工成功: {}", employee.getUsername());
        return Result.success(null);
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@Parameter(description = "员工ID") @PathVariable String id) {
        Employee employee = employeeMapper.selectById(Long.parseLong(id));
        if (employee == null) {
            throw new CustomException("员工不存在");
        }
        employeeMapper.deleteById(Long.parseLong(id));
        log.info("删除员工成功: {}", employee.getUsername());
        return Result.success(null);
    }

    /**
     * 转换为 VO
     */
    private EmployeeVO convertToVO(Employee employee) {
        EmployeeVO vo = new EmployeeVO();
        vo.setEmployeeId(String.valueOf(employee.getEmployeeId()));
        vo.setUsername(employee.getUsername());
        vo.setRealName(employee.getRealName());
        vo.setIsActive(employee.getIsActive());
        vo.setIsActiveDesc(employee.getIsActive() != null && employee.getIsActive() == 1 ? "启用" : "禁用");
        vo.setLastLoginAt(employee.getLastLoginAt());
        vo.setCreatedAt(employee.getCreatedAt());

        // 获取角色信息
        List<Long> roleIdsLong = roleService.getRoleIdsByEmployeeId(employee.getEmployeeId());
        List<String> roleIds = roleIdsLong.stream().map(String::valueOf).toList();
        List<String> roleNames = roleService.getRoleNamesByRoleIds(roleIds);
        vo.setRoleIds(roleIds);
        vo.setRoleNames(roleNames);

        return vo;
    }
}