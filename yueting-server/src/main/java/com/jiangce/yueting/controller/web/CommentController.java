package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.CommentCreateDTO;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.CommentVO;
import com.jiangce.yueting.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@Slf4j
@RestController
@RequestMapping("/web/comment")
@Tag(name = "评论相关接口")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Void> createComment(@RequestBody CommentCreateDTO createDTO) {
        Long userId = BaseContext.getCurrentId();
        commentService.createComment(userId, createDTO);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = BaseContext.getCurrentId();
        commentService.deleteComment(commentId, userId);
        return Result.success(null);
    }

    @Operation(summary = "获取评论列表")
    @GetMapping
    public Result<List<CommentVO>> getCommentList(
            @Parameter(description = "目标类型：1-歌曲，2-歌单，3-专辑") @RequestParam Integer targetType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        Long userId = BaseContext.getCurrentId();
        List<CommentVO> comments = commentService.getCommentList(targetType, targetId, userId);
        return Result.success(comments);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "点赞评论")
    @PostMapping("/{commentId}/like")
    public Result<Void> likeComment(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = BaseContext.getCurrentId();
        commentService.likeComment(commentId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "取消点赞评论")
    @DeleteMapping("/{commentId}/like")
    public Result<Void> unlikeComment(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = BaseContext.getCurrentId();
        commentService.unlikeComment(commentId, userId);
        return Result.success(null);
    }
}
