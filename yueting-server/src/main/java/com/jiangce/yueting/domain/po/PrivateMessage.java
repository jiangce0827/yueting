package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 私信表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("private_message")
public class PrivateMessage {
    @TableId(type = IdType.AUTO)
    private Long messageId; // 消息ID

    private Long senderId; // 发送者用户ID

    private Long receiverId; // 接收者用户ID

    private String content; // 消息内容

    private Boolean isRead; // 是否已读：0-未读，1-已读

    private Integer status; // 状态：0-正常，1-发送者删除，2-接收者删除

    private LocalDateTime createdAt; // 发送时间
}
