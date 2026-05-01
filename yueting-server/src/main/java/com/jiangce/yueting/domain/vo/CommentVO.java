package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论VO
 */
@Data
public class CommentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commentId;

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
     * 评论用户ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
     * 评论用户昵称
     */
    private String nickname;

    /**
     * 评论用户头像
     */
    private String avatarUrl;

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
     * 回复目标用户昵称
     */
    private String replyToNickname;

    /**
     * 被回复的评论内容（引用）
     */
    private String quoteContent;

    /**
     * 被回复的评论作者昵称（引用）
     */
    private String quoteNickname;

    /**
     * 被引用的评论是否已被删除
     */
    private Boolean quotedCommentDeleted;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean liked;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
