package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私信会话VO
 */
@Data
public class PrivateMessageVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long messageId; // 消息ID

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long senderId; // 发送者ID

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId; // 接收者ID

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long otherUserId; // 对方用户ID（用于查询聊天记录）

    private String senderName; // 发送者昵称（显示名称）
    private String senderAvatar; // 发送者头像
    private Boolean isOfficial; // 是否官方账号
    private String content; // 消息内容
    private Boolean isRead; // 是否已读
    private LocalDateTime createdAt; // 发送时间
}
