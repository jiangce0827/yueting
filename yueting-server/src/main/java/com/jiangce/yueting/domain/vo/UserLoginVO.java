package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId; // 用户ID
    private String email; // 加密邮箱
    private String nickname; // 昵称
    private String avatarUrl; // 头像地址
    private Integer gender; // 性别：0-未知，1-男，2-女 (默认0)
    private String signature; // 个性签名
    private Integer listenCount; // 累计听歌数 （默认0）
    private Integer experience; // 累计经验值 （默认0）
    private Integer followingCount; // 关注数
    private Integer followerCount; // 粉丝数
    private Integer vip; // VIP: 0-否，1-100：等级(默认0)
    private LocalDateTime vipExpireAt; // VIP到期时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday; // 生日
    private Integer userType; // 用户类型: 0-普通用户, 1-系统账号
    private Integer passwordStatus; //0:未设置账密,1:已启用账密登录，2:已禁用账密登录
    private Integer privacyAllowMessages; // 私信权限: 0-所有人, 1-仅关注的人
    private Integer privacyShowListeningHabits; // 听歌习惯公开: 0-公开, 1-不公开
    private String token;
}
