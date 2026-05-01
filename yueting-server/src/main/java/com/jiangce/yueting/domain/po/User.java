package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;
import com.jiangce.yueting.utils.BeanUtils;
import com.jiangce.yueting.utils.EmailUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId; // 用户ID
    private String nickname; // 昵称
    private String username; // 账号
    private String password; // 密码
    private String email; // 邮箱
    private String phone; // 手机号
    private String avatarUrl; // 头像地址
    private Integer gender; // 性别：0-未知，1-男，2-女 (默认0)
    private String signature; // 个性签名
    private Integer listenCount; // 累计听歌数 （默认0）
    private Integer experience; // 累计经验值 （默认0）
    private Integer followingCount; // 关注数
    private Integer followerCount; // 粉丝数
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday; // 生日
    private Integer status; // 账号状态：0-正常，1-禁用 (默认0)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusExpireAt; // 封禁剩余时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 账号创建时间
    private Integer userType; // 用户类型: 0-普通用户, 1-系统账号
    private Integer passwordStatus; //0:未设置账密,1:已启用账密登录，2:已禁用账密登录
    private Integer privacyAllowMessages; // 私信权限: 0-所有人, 1-仅关注的人
    private Integer privacyShowListeningHabits; // 听歌习惯公开: 0-公开, 1-不公开

    /**
     * 转换为UserSettingVO
     */
    public UserSettingVO toUserSettingVO() {
        UserSettingVO userSettingVO = BeanUtils.copyProperties(this, UserSettingVO.class);
        userSettingVO.setEmail(getMaskedEmail());
        return userSettingVO;
    }

    /**
     * 转换为UserLoginVO
     */
    public UserLoginVO toUserLoginVO() {
        UserLoginVO userLoginVO = BeanUtils.copyProperties(this, UserLoginVO.class);
        userLoginVO.setEmail(getMaskedEmail());
        return userLoginVO;
    }

    /**
     * 获取脱敏后的邮箱
     */
    private String getMaskedEmail() {
        return EmailUtils.getMaskedEmail(this.email);
    }
}
