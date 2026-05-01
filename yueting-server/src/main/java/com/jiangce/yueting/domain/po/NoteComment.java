package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔记评论表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("note_comment")
public class NoteComment {
    @TableId(type = IdType.AUTO)
    private Long id; // 评论ID

    private Long noteId; // 笔记ID

    private Long userId; // 评论用户ID

    private Long parentId; // 父评论ID

    private Long replyToUserId; // 回复目标用户ID

    private String content; // 评论内容

    private Integer status; // 状态：0-正常，1-已删除

    private LocalDateTime createdAt; // 评论时间
}