package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.CommentCreateDTO;
import com.jiangce.yueting.domain.po.Comment;
import com.jiangce.yueting.domain.po.CommentLike;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.vo.CommentVO;
import com.jiangce.yueting.mapper.CommentLikeMapper;
import com.jiangce.yueting.mapper.CommentMapper;
import com.jiangce.yueting.mapper.PlaylistMapper;
import com.jiangce.yueting.mapper.UserMapper;
import com.jiangce.yueting.service.CommentService;
import com.jiangce.yueting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    private final CommentLikeMapper commentLikeMapper;

    private final UserMapper userMapper;

    private final PlaylistMapper playlistMapper;

    private final NotificationService notificationService;

    @Override
    @Transactional
    public void createComment(Long userId, CommentCreateDTO createDTO) {
        Comment comment = new Comment();
        comment.setTargetType(createDTO.getTargetType());
        comment.setTargetId(createDTO.getTargetId());
        comment.setUserId(userId);
        comment.setParentId(createDTO.getParentId());
        comment.setReplyToUserId(createDTO.getReplyToUserId());
        comment.setContent(createDTO.getContent());
        comment.setLikeCount(0);
        comment.setStatus(0);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        // 创建评论通知
        createCommentNotification(userId, comment, createDTO);

        log.info("发表评论成功: userId={}, targetType={}, targetId={}", userId, createDTO.getTargetType(), createDTO.getTargetId());
    }

    /**
     * 创建评论通知
     * 通知类型：3-回复评论, 6-评论歌单, 7-评论歌曲, 8-评论专辑
     * targetType: 1-歌曲, 2-歌单, 3-专辑 (表示被评论对象的类型，用于前端跳转)
     */
    private void createCommentNotification(Long userId, Comment comment, CommentCreateDTO createDTO) {
        // 情况1：回复评论 - 通知原评论作者
        if (createDTO.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(createDTO.getParentId());
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                notificationService.createNotification(
                        parentComment.getUserId(),  // 接收者：原评论作者
                        userId,                      // 发送者：当前评论者
                        3,                          // 类型：3-评论回复
                        comment.getContent(),       // 内容
                        comment.getCommentId(),     // 目标ID：新评论ID
                        createDTO.getTargetType(),  // 目标类型：被评论对象的类型(1-歌曲,2-歌单,3-专辑)
                        createDTO.getTargetId()     // 目标父级ID：歌曲/歌单/专辑ID
                );
            }
        }
        // 情况2：评论歌单 - 通知歌单创建者
        else if (createDTO.getTargetType() == 2) {
            Playlist playlist = playlistMapper.selectById(createDTO.getTargetId());
            if (playlist != null && !playlist.getUserId().equals(userId)) {
                notificationService.createNotification(
                        playlist.getUserId(),       // 接收者：歌单创建者
                        userId,                      // 发送者：评论者
                        6,                          // 类型：6-评论歌单
                        comment.getContent(),       // 内容
                        comment.getCommentId(),     // 目标ID：评论ID
                        createDTO.getTargetType(),  // 目标类型：2-歌单
                        createDTO.getTargetId()     // 目标父级ID：歌单ID
                );
            }
        }
        // 情况3：评论歌曲 - 暂不处理（歌曲没有明确的所有者）
        // 情况4：评论专辑 - 暂不处理
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new CustomException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException("无权限删除此评论");
        }
        // 软删除：更新状态
        Comment updateComment = new Comment();
        updateComment.setCommentId(commentId);
        updateComment.setStatus(1);
        commentMapper.updateById(updateComment);

        log.info("删除评论成功: commentId={}", commentId);
    }

    @Override
    public List<CommentVO> getCommentList(Integer targetType, Long targetId, Long userId) {
        // 获取所有评论（扁平结构，按最新排序，只查询未删除的）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTargetType, targetType)
                .eq(Comment::getTargetId, targetId)
                .eq(Comment::getStatus, 0)
                .orderByDesc(Comment::getCommentId);
        List<Comment> comments = commentMapper.selectList(wrapper);

        return comments.stream()
                .map(c -> convertToVO(c, userId))
                .collect(Collectors.toList());
    }

    private CommentVO convertToVO(Comment comment, Long currentUserId) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(comment.getCommentId());
        vo.setTargetType(comment.getTargetType());
        vo.setTargetId(comment.getTargetId());
        vo.setUserId(comment.getUserId());
        vo.setParentId(comment.getParentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreatedAt(comment.getCreatedAt());

        // 检查当前用户是否已点赞
        if (currentUserId != null) {
            vo.setLiked(commentLikeMapper.existsByCommentIdAndUserId(comment.getCommentId(), currentUserId));
        } else {
            vo.setLiked(false);
        }

        // 获取评论用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatarUrl(user.getAvatarUrl());
        }

        // 获取回复目标用户昵称
        if (comment.getReplyToUserId() != null) {
            User replyToUser = userMapper.selectById(comment.getReplyToUserId());
            if (replyToUser != null) {
                vo.setReplyToNickname(replyToUser.getNickname());
            }
        }

        // 获取被回复的评论内容（引用）
        if (comment.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(comment.getParentId());
            if (parentComment != null && parentComment.getStatus() != null && parentComment.getStatus() == 1) {
                // 父评论已被删除
                vo.setQuotedCommentDeleted(true);
            } else if (parentComment != null) {
                vo.setQuoteContent(parentComment.getContent());
                User parentUser = userMapper.selectById(parentComment.getUserId());
                if (parentUser != null) {
                    vo.setQuoteNickname(parentUser.getNickname());
                }
                vo.setQuotedCommentDeleted(false);
            } else {
                // 父评论不存在（级联删除等情况）
                vo.setQuotedCommentDeleted(true);
            }
        }

        return vo;
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new CustomException("评论不存在");
        }
        // 检查是否已点赞
        if (commentLikeMapper.existsByCommentIdAndUserId(commentId, userId)) {
            throw new CustomException("已经点赞过该评论");
        }
        // 添加点赞记录
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLike.setCreatedAt(LocalDateTime.now());
        commentLikeMapper.insert(commentLike);
        // 更新评论点赞数
        Comment updateComment = new Comment();
        updateComment.setCommentId(commentId);
        updateComment.setLikeCount(comment.getLikeCount() + 1);
        commentMapper.updateById(updateComment);

        // 创建点赞通知
        if (!comment.getUserId().equals(userId)) {
            notificationService.createNotification(
                    comment.getUserId(),  // 接收者：评论作者
                    userId,              // 发送者：点赞者
                    1,                  // 类型：1-评论点赞
                    comment.getContent(), // 内容：评论内容
                    commentId,           // 目标ID：评论ID
                    comment.getTargetType(), // 目标类型：被评论对象的类型(1-歌曲,2-歌单,3-专辑)
                    comment.getTargetId()    // 目标父级ID：歌曲/歌单/专辑ID
            );
        }

        log.info("点赞评论成功: commentId={}, userId={}", commentId, userId);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new CustomException("评论不存在");
        }
        // 检查是否已点赞
        if (!commentLikeMapper.existsByCommentIdAndUserId(commentId, userId)) {
            throw new CustomException("尚未点赞该评论");
        }
        // 删除点赞记录
        commentLikeMapper.deleteByCommentIdAndUserId(commentId, userId);
        // 更新评论点赞数
        Comment updateComment = new Comment();
        updateComment.setCommentId(commentId);
        updateComment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        commentMapper.updateById(updateComment);

        log.info("取消点赞评论成功: commentId={}, userId={}", commentId, userId);
    }

    @Override
    public boolean isLikedByUser(Long commentId, Long userId) {
        if (userId == null) {
            return false;
        }
        return commentLikeMapper.existsByCommentIdAndUserId(commentId, userId);
    }
}
