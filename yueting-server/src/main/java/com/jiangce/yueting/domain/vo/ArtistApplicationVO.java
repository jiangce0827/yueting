package com.jiangce.yueting.domain.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiangce.yueting.common.ArtistContent;
import com.jiangce.yueting.domain.po.Artist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistApplicationVO {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long applicationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private String userName;
    private String phone;
    private String email;
    private Integer applyType; // 申请类型：0：新艺人，1：认领
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedAt; // 申请时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt; // 审核时间
    private Integer status; // 状态：0：待审核，1：已通过，2：已拒绝
}
