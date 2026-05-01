package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long notificationId; // 通知ID

    private Long receiverId; // 接收者用户ID

    private Long senderId; // 触发者用户ID（系统通知为NULL）

    private Integer type; // 通知类型：1-评论点赞，2-笔记点赞，3-评论回复，4-新增关注，5-系统公告，6-私信

    private String content; // 通知内容（私信内容或系统公告内容）

    private Long targetId; // 关联目标ID（如评论ID、笔记ID、用户ID等）

    private Integer targetType; // 关联目标类型：1-评论，2-笔记，3-歌曲，4-歌单，5-专辑，6-用户

    private Long targetSubId; // 关联目标的父级ID（如评论所属的歌曲/歌单ID）

    private Boolean isRead; // 是否已读：0-未读，1-已读

    private Integer status; // 状态：0-正常，1-接收者删除

    private LocalDateTime createdAt; // 创建时间
}
