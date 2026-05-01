package com.jiangce.yueting.domain.dto;

import com.jiangce.yueting.common.ArtistApplicationContent;
import com.jiangce.yueting.domain.po.ArtistApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistApplicationDTO {
    private Integer applyType; // 申请类型：0：新艺人，1：认领
    private Long claimedArtistId; // 认领的艺人id
    private String artistAvatarUrl; // 艺人头像
    private String artistCoverUrl; // 艺人页头图
    private String artistName; // 艺人名
    private Integer gender; // 性别
    private Integer language; // 语言
    private String artistBio; // 艺人简介
    private String realName; // 真实姓名
    private String idCard; // 身份证
    private String back; // 身份证反面
    private String front; // 身份证正面
    private String halfBody; // 半身照
    private String code; // 验证码

    public ArtistApplication toArtistApplication() {
        return new ArtistApplication(null, null, applyType, claimedArtistId, artistAvatarUrl, artistCoverUrl, artistName, gender, language, artistBio, realName, idCard, back, front, halfBody, null, null, LocalDateTime.now(), null, ArtistApplicationContent.STATUS_UNREVIEWED);
    }
}
