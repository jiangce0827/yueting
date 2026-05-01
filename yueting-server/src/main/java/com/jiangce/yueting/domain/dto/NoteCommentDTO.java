package com.jiangce.yueting.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 笔记评论DTO
 */
@Data
public class NoteCommentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 笔记ID
     */
    private Long noteId;

    /**
     * 父评论ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
     * 回复目标用户ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyToUserId;

    /**
     * 评论内容
     */
    private String content;
}