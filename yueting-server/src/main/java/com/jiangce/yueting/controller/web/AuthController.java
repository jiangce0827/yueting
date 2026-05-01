package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.UserRegisterByEmailDTO;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;
import com.jiangce.yueting.service.AuthService;
import com.jiangce.yueting.service.CommonService;
import com.jiangce.yueting.utils.JwtUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/auth")
@Slf4j
@Tag(name = "认证相关接口")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 发送验证码到指定邮箱
     * 向用户邮箱发送验证码，并将验证码存储在redis中
     *
     * @param email 目标邮箱
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "给指定邮箱发送验证码")
    @PostMapping("/sendCode/email")
    public Result<String> sendEmailCode(@RequestParam("email") String email) {
        log.info("发送邮件验证码，email：{}", email);
        authService.sendCodeEmail(email);
        return Result.success("发送成功");
    }

    /**
     * 发送邮件验证码给当前用户的邮箱
     * 向用户邮箱发送验证码，并将验证码存储在redis中
     *
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "给用户发邮箱送验证码")
    @PostMapping("/me/sendCode/email")
    public Result<String> sendEmailCodeToMe() {
        log.info("发送邮件验证码");
        authService.sendCodeEmailByUserId(BaseContext.getCurrentId());
        return Result.success("发送成功");
    }

    /**
     * 用户通过邮箱注册
     * 用户输入邮箱与验证码，同时可以选择是否开启账密登录方式
     *
     * @param userRegisterByEmail 用户输入的邮箱与验证码，账密(可选)
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "用户通过邮箱注册")
    @PostMapping("/register/email")
    public Result<String> registerByEmail(@RequestBody UserRegisterByEmailDTO userRegisterByEmail) {
        log.info("用户通过邮箱注册，{}", userRegisterByEmail);
        authService.registerByEmail(userRegisterByEmail);
        return Result.success("注册成功");
    }

    /**
     * 用户通过账户密码登录
     *
     * @param username 账户
     * @param password 密码
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "用户通过账户密码登录")
    @PostMapping("/login/username")
    public Result<UserLoginVO> loginByUsername(@RequestParam("username") String username, @RequestParam("password") String password) {
        log.info("用户通过用户名密码登录，username：{}，password：{}", username, password);
        UserLoginVO userLoginVO = authService.loginByUsername(username, password);

        // 生成JWT令牌
        String token = authService.getToken(userLoginVO.getUserId());
        userLoginVO.setToken(token);
        return Result.success(userLoginVO);
    }


    /**
     * 用户通过邮箱验证码登录
     *
     * @param email 邮箱
     * @param code  验证码
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "用户通过邮箱验证码登录")
    @PostMapping("/login/email")
    public Result<UserLoginVO> loginByEmail(@RequestParam("email") String email, @RequestParam("code") String code) {

        log.info("用户通过邮箱密码登录，email：{}，code：{}", email, code);
        UserLoginVO userLoginVO = authService.loginByEmail(email, code);
        // 生成JWT令牌
        String token = authService.getToken(userLoginVO.getUserId());
        userLoginVO.setToken(token);
        return Result.success(userLoginVO);
    }

    /**
     * 验证邮箱是否为用户邮箱
     *
     * @param email 邮箱
     */
    @PostMapping("/me/email/verify")
    @Operation(summary = "验证邮箱是否为用户邮箱")
    @PreAuthorize("hasRole('USER')")
    public Result<String> verifyEmail(@RequestParam("email") String email) {
        log.info("验证邮箱，email：{}", email);
        authService.verifyEmail(email);
        return Result.success("验证成功");
    }

    /**
     * 验证邮箱及验证码是否正确
     *
     * @param email 邮箱
     * @param code  验证码
     */
    @PostMapping("/email/verify")
    @Operation(summary = "验证邮箱及验证码是否正确")
    @PreAuthorize("permitAll()")
    public Result<String> verifyEmailAndCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        log.info("验证邮箱及验证码，email：{}，code：{}", email, code);
        authService.verifyEmailAndCode(email, code);
        return Result.success("验证成功");
    }

    /**
     * 更新用户邮箱
     *
     * @param newEmail 新邮箱
     * @param code     验证码
     */
    @PutMapping("/me/email")
    @Operation(summary = "更新用户邮箱")
    @PreAuthorize("hasRole('USER')")
    public Result<UserSettingVO> updateUserEmail(@RequestParam("newEmail") String newEmail, @RequestParam("code") String code) {
        log.info("更新用户邮箱，newEmail：{},code:{}", newEmail, code);
        UserSettingVO userSettingVO = authService.updateUserEmail(newEmail, code);
        return Result.success(userSettingVO);
    }

    /**
     * 初始化账密
     *
     * @param username 账户
     * @param password 密码
     * @return 用户设置
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "初始化账户密码")
    @PostMapping("/me/password")
    public Result<UserSettingVO> initPassword(@RequestParam("username") String username, @RequestParam("password") String password) {
        log.info("初始化密码，username：{},password:{}", username, password);
        UserSettingVO userSettingVO = authService.initPassword(username, password);
        return Result.success(userSettingVO);
    }

    /**
     * 禁用密码登录
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "禁用密码登录")
    @PutMapping("/me/password/disable")
    public Result<UserSettingVO> disablePassword() {
        log.info("禁用密码登录");
        UserSettingVO userSettingVO = authService.disablePassword();
        return Result.success(userSettingVO);
    }

    /**
     * 启用账密登录
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "启用密码登录")
    @PutMapping("/me/password/enable")
    public Result<UserSettingVO> enablePassword() {
        log.info("启用密码登录");
        UserSettingVO userSettingVO = authService.enablePassword();
        return Result.success(userSettingVO);
    }

    /**
     * 修改登录密码
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "修改登录密码")
    @PutMapping("/me/password")
    public Result<UserSettingVO> updatePassword(@RequestParam("email") String email,
                                                @RequestParam("code") String code,
                                                @RequestParam("newPassword") String newPassword) {
        log.info("修改登录密码，email：{},code:{},newPassword:{}", email, code, newPassword);
        UserSettingVO userSettingVO = authService.updatePassword(email, code, newPassword);
        return Result.success(userSettingVO);
    }

    /**
     * 获取token
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me/token/userId")
    @Operation(summary = "获取token")
    public Result<String> getToken() {
        log.info("获取token");
        String token = authService.getToken(BaseContext.getCurrentId());
        return Result.success(token);
    }

}
