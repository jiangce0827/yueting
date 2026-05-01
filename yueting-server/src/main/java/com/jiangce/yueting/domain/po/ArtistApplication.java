package com.jiangce.yueting.domain.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jiangce.yueting.common.ArtistContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistApplication {
    @TableId(type = IdType.AUTO)
    private Long applicationId;
    private Long userId;
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
    private Long reviewedBy;// 审核人id
    private String rejectionReason; // 拒绝原因
    private LocalDateTime appliedAt; // 申请时间
    private LocalDateTime reviewedAt; // 审核时间
    private Integer status; // 状态：0：待审核，1：已通过，2：已拒绝

    public Artist toArtist() {
        return new Artist(null,null,this.artistName, this.artistAvatarUrl, this.artistCoverUrl, this.gender, this.language, this.artistBio, null, null,this.realName, this.idCard, LocalDateTime.now(), ArtistContent.STATUS_NOT_ACTIVE,null);
    }
}
