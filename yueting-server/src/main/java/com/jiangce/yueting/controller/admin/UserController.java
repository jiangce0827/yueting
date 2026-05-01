package com.jiangce.yueting.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.jiangce.yueting.domain.dto.UserPageDTO;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("AdminUserController")
@RequestMapping("/admin/user")
@Tag(name = "后台用户相关接口")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "分页查询用户列表", description = "UserPageDTO内参数非必须,返回用户信息列表")
    @GetMapping("/pageUser")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult> pageUser(UserPageDTO userDTO) {
        log.info("查询用户列表{}", userDTO);
        PageResult pageResult = userService.pageUser(userDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "修改用户信息", description = "携带需要更新的信息")
    @PostMapping("/updateUser")
    public Result<String> update(@RequestBody User user) {
        log.info("修改用户信息{}", user.toString());
        userService.updateUser(user);
        return Result.success("成功");
    }
}
