package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论表（支持歌曲、歌单、专辑的评论）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long commentId; // 评论ID

    private Integer targetType; // 评论目标类型：1-歌曲，2-歌单，3-专辑

    private Long targetId; // 评论目标ID（歌曲ID/歌单ID/专辑ID）

    private Long userId; // 评论用户ID

    private Long parentId; // 父评论ID（用于回复）

    private Long replyToUserId; // 回复目标用户ID

    private String content; // 评论内容

    private Integer likeCount; // 点赞数

    private Integer status; // 状态：0-正常，1-已删除

    private LocalDateTime createdAt; // 评论时间
}
