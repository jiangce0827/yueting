package com.jiangce.yueting.domain.dto;

import lombok.Data;

/**
 * 发送私信DTO
 */
@Data
public class PrivateMessageSendDTO {
    private String receiverId; // 接收者ID（与receiverName二选一），使用String避免前端精度丢失
    private String receiverName; // 接收者用户名（与receiverId二选一）
    private String content; // 私信内容
}
