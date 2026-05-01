package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知VO
 */
@Data
public class NotificationVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long notificationId; // 通知ID

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId; // 接收者ID

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long senderId; // 触发者ID

    private String senderName; // 触发者昵称
    private String senderAvatar; // 触发者头像
    private Integer type; // 通知类型
    private String content; // 通知内容

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long targetId; // 关联目标ID

    private Integer targetType; // 关联目标类型

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long targetSubId; // 关联目标的父级ID

    private Boolean isRead; // 是否已读
    private LocalDateTime createdAt; // 创建时间

    // 扩展字段（用于显示）
    private String targetName; // 被评论目标的名称（如歌曲名、歌单名）
}
