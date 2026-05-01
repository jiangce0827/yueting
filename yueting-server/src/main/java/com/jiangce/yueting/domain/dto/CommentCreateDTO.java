package com.jiangce.yueting.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论创建DTO
 */
@Data
public class CommentCreateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论目标类型：1-歌曲，2-歌单，3-专辑
     */
    private Integer targetType;

    /**
     * 评论目标ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long targetId;

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
