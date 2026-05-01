package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.CommentCreateDTO;
import com.jiangce.yueting.domain.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 发表评论
     */
    void createComment(Long userId, CommentCreateDTO createDTO);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取评论列表
     * @param targetType 目标类型：1-歌曲，2-歌单，3-专辑
     * @param targetId 目标ID
     * @param userId 当前用户ID（可为null）
     * @return 评论列表
     */
    List<CommentVO> getCommentList(Integer targetType, Long targetId, Long userId);

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void likeComment(Long commentId, Long userId);

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void unlikeComment(Long commentId, Long userId);

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isLikedByUser(Long commentId, Long userId);
}
