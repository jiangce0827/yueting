package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.common.PlaylistContent;
import com.jiangce.yueting.common.UserContent;
import com.jiangce.yueting.domain.dto.UserRegisterByEmailDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.vo.ArtistInfoVO;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.mapper.PlaylistMapper;
import com.jiangce.yueting.mapper.UserMapper;
import com.jiangce.yueting.service.AuthService;
import com.jiangce.yueting.service.PlaylistService;
import com.jiangce.yueting.utils.JwtUtils;
import com.jiangce.yueting.utils.StringUtils;
import com.jiangce.yueting.utils.ValidateCodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ArtistMapper artistMapper;
    private final PlaylistMapper playlistMapper;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;    //邮件发送者

    /**
     * 发送验证码
     * 向用户邮箱发送验证码，并将验证码存储在redis中
     *
     * @param email 用户邮箱
     */
    @Override
    public void sendCodeEmail(String email) {
        //生成验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        //构建一个邮件的对象
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //设置邮件发件者
        simpleMailMessage.setFrom(from);
        //设置邮件接受者
        simpleMailMessage.setTo(email);
        //设置邮箱的主题
        simpleMailMessage.setSubject("悦听音乐验证码");
        //设置邮件的正文
        String text = "【悦听音乐】您的悦听音乐验证码是: " + code + "，有效时间五分钟，请尽快验证";
        log.info("邮件内容：{}",text);
        simpleMailMessage.setText(text);

        try {
            //将验证码存在redis中，时效五分钟
            redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);

            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("发送失败");
        }
        log.info("发送成功,code:{}", code);
    }

    @Override
    public void sendCodeEmailByUserId(Long currentId) {
        User user = userMapper.selectById(currentId);
        sendCodeEmail(user.getEmail());
    }

    /**
     * 用户通过邮箱注册
     * 用户输入邮箱与验证码，同时可以选择是否开启账密登录方式
     *
     * @param userRegisterByEmail 用户输入的邮箱与验证码，账密(可选)
     */
    @Override
    @Transactional
    public void registerByEmail(UserRegisterByEmailDTO userRegisterByEmail) {


        // 验证邮箱和验证码
        this.verifyEmailAndCode(userRegisterByEmail.getEmail(), userRegisterByEmail.getCode());

        // 判断邮箱是否被注册
        this.checkEmailIsRegistered(userRegisterByEmail.getEmail());

        //如果用户想要设置账户和密码
        if (StringUtils.isAllHasLength(userRegisterByEmail.getUsername(), userRegisterByEmail.getPassword())) {
            // 判断用户名是否被注册
            this.checkUsernameIsRegistered(userRegisterByEmail.getUsername());
        }

        //生成不重复的昵称
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        String nickName;
        do {
            nickName = StringUtils.generateRandomNickname(10);
            userQueryWrapper.clear();
            userQueryWrapper.eq(User::getNickname, nickName);
        } while (userMapper.selectCount(userQueryWrapper) > 0);

        // 构建用户信息
        User user = User.builder()
                .nickname(nickName)
                .email(userRegisterByEmail.getEmail())
                .avatarUrl(UserContent.DEFAULT_AVATAR)
                .gender(UserContent.DEFAULT_GENDER)
                .passwordStatus(UserContent.PASSWORD_STATUS_NO_SET)
                .createdAt(LocalDateTime.now())
                .build();

        // 如果用户名密码不为空，则设置用户名和密码,并且启用账密登录
        if (StringUtils.isAllHasLength(userRegisterByEmail.getUsername(), userRegisterByEmail.getPassword())) {
            user.setUsername(userRegisterByEmail.getUsername());
            user.setPassword(userRegisterByEmail.getPassword());
            user.setPasswordStatus(UserContent.PASSWORD_STATUS_ENABLE);
        }
        userMapper.insert(user);
        Playlist playlist = Playlist.builder()
                .userId(user.getUserId())
                .playlistName("我喜欢的音乐")
                .coverUrl(PlaylistContent.DEFAULT_COVER_URL)
                .isDefault(true)
                .isPublic(true)
                .build();
        playlistMapper.insert(playlist);
    }

    /**
     * 用户通过账户密码登录
     *
     * @param username 账户
     * @param password 密码
     */
    @Override
    public UserLoginVO loginByUsername(String username, String password) {
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getUsername, username)
                .eq(User::getPassword, password);
        // 查询用户
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            throw new CustomException("用户名或密码错误");
        }
        // 判断用户是否启用账密登录
        if (user.getPasswordStatus().equals(UserContent.PASSWORD_STATUS_DISABLE)) {
            throw new CustomException("账密密码已被禁用，请更换登录方式");
        }
        // 判断用户是否被封禁
        if (userIsBan(user)) {
            throw new CustomException("用户已被封禁，解封时间：" + user.getStatusExpireAt().toString());
        }
        return user.toUserLoginVO();
    }

    /**
     * 用户通过邮箱验证码登录
     *
     * @param email 邮箱
     * @param code  验证码
     */
    @Override
    public UserLoginVO loginByEmail(String email, String code) {
        verifyEmailAndCode(email, code);
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        // 判断用户是否被封禁
        if (userIsBan(user)) {
            throw new CustomException("用户已被封禁，解封时间：" + user.getStatusExpireAt().toString());
        }
        return user.toUserLoginVO();
    }

    /**
     * 验证邮箱是否为用户邮箱
     *
     * @param email 邮箱地址
     */
    @Override
    public void verifyEmail(String email) {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        if (!user.getEmail().equals(email)) {
            throw new CustomException("邮箱错误");
        }
    }

    /**
     * 验证邮箱与验证码
     *
     * @param email 邮箱
     * @param code  验证码
     */
    @Override
    public void verifyEmailAndCode(String email, String code) {
        String codeInRedis = (String) redisTemplate.opsForValue().get(email);
        if (StringUtils.isEmpty(codeInRedis) || !codeInRedis.equals(code)) {
            throw new CustomException("验证码错误");
        }
    }

    @Override
    public void verifyEmailCode(Long userId, String code) {
        User user = userMapper.selectById(userId);
        verifyEmailAndCode(user.getEmail(), code);
    }

    /**
     * 更新用户邮箱
     *
     * @param newEmail 新邮箱
     * @param code     验证码
     * @return 更新后的用户设置
     */
    @Override
    public UserSettingVO updateUserEmail(String newEmail, String code) {
        verifyEmailAndCode(newEmail, code); // 验证邮箱与验证码
        checkEmailIsRegistered(newEmail); // 判断邮箱是否被注册
        User user = userMapper.selectById(BaseContext.getCurrentId());
        user.setEmail(newEmail);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    /**
     * 初始化账户密码
     *
     * @param username 账户
     * @param password 密码
     */
    @Override
    public UserSettingVO initPassword(String username, String password) {
        checkUsernameIsRegistered(username); // 判断账户名是否被注册
        User user = userMapper.selectById(BaseContext.getCurrentId());
        user.setUsername(username);
        user.setPassword(password);
        user.setPasswordStatus(UserContent.PASSWORD_STATUS_ENABLE); // 启用账密登录
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    /**
     * 更新账户密码
     *
     * @param email       邮箱
     * @param code        验证码
     * @param newPassword 密码
     */
    @Override
    public UserSettingVO updatePassword(String email, String code, String newPassword) {
        verifyEmailAndCode(email, code);
        User user = userMapper.selectById(BaseContext.getCurrentId());
        if (!user.getEmail().equals(email)) {
            throw new CustomException("邮箱错误");
        }
        user.setPassword(newPassword);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    /**
     * 禁用账户密码
     */
    @Override
    public UserSettingVO disablePassword() {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        user.setPasswordStatus(UserContent.PASSWORD_STATUS_DISABLE);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    /**
     * 启用账户密码登录
     */
    @Override
    public UserSettingVO enablePassword() {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        user.setPasswordStatus(UserContent.PASSWORD_STATUS_ENABLE);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    @Override
    public String getToken(Long userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        list.add("ROLE_USER");
        // 判断用户是否为歌手
        LambdaQueryWrapper<Artist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Artist::getUserId, userId);
        if (artistMapper.exists(queryWrapper)) {
            list.add("ROLE_ARTIST");
        }
        String token = JwtUtils.generateToken(user.getUserId().toString(), list);
        return token;
    }

    /**
     * 判断用户是否被封禁
     *
     * @param user 用户
     * @return true: 被封禁，false: 未被封禁
     */
    private boolean userIsBan(User user) {
        if (user.getStatus().equals(UserContent.TYPE_BAN)) {
            // 判断是否已解封
            if (LocalDateTime.now().isBefore(user.getStatusExpireAt())) {
                return true;
            }
            user.setStatus(UserContent.TYPE_NO_BAN);
            userMapper.updateById(user);
        }
        return false;
    }

    /**
     * 检查邮箱是否被注册
     */
    private void checkEmailIsRegistered(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        if (userMapper.exists(queryWrapper)) {
            throw new CustomException("邮箱已被注册");
        }
    }

    /**
     * 检查账户名是否被注册
     */
    private void checkUsernameIsRegistered(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        if (userMapper.exists(queryWrapper)) {
            throw new CustomException("账户已被注册");
        }
    }

}
