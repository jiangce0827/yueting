package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.UserRegisterByEmailDTO;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;

public interface AuthService {
    /**
     * 发送验证码
     * 向用户邮箱发送验证码，并将验证码存储在redis中
     *
     * @param email 用户邮箱
     */
    void sendCodeEmail(String email);

    /**
     * 发送邮件验证码给指定用户
     * 向用户邮箱发送验证码，并将验证码存储在redis中
     */
    void sendCodeEmailByUserId(Long currentId);

    /**
     * 用户通过邮箱注册
     * 用户输入邮箱与验证码，同时可以选择是否开启账密登录方式
     *
     * @param userRegisterByEmail 用户输入的邮箱与验证码，账密(可选)
     */
    void registerByEmail(UserRegisterByEmailDTO userRegisterByEmail);

    /**
     * 用户通过账户密码登录
     *
     * @param username 账户
     * @param password 密码
     */
    UserLoginVO loginByUsername(String username, String password);

    /**
     * 用户通过邮箱验证码登录
     *
     * @param email 邮箱
     * @param code  验证码
     */
    UserLoginVO loginByEmail(String email, String code);

    /**
     * 验证邮箱是否为用户邮箱
     *
     * @param email 邮箱地址
     */
    void verifyEmail(String email);

    /**
     * 验证邮箱与验证码
     *
     * @param email 邮箱
     * @param code  验证码
     */
    void verifyEmailAndCode(String email, String code);

    /**
     * 验证邮箱验证码
     */
    void verifyEmailCode(Long userId,String code);

    /**
     * 更新用户邮箱
     *
     * @param newEmail 新邮箱
     * @param code     验证码
     * @return 更新后的用户设置
     */
    UserSettingVO updateUserEmail(String newEmail, String code);

    /**
     * 初始化账户密码
     *
     * @param username 账户
     * @param password 密码
     */
    UserSettingVO initPassword(String username, String password);

    /**
     * 更新账户密码
     *
     * @param email       邮箱
     * @param code        验证码
     * @param newPassword 密码
     */
    UserSettingVO updatePassword(String email, String code, String newPassword);

    /**
     * 禁用账户密码登录
     */
    UserSettingVO disablePassword();

    /**
     * 启用账户密码登录
     */
    UserSettingVO enablePassword();

    /**
     * 获取token
     * @param userId 用户id
     * @return token
     */
    String getToken(Long userId);


}
