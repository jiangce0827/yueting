package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.NoteCommentDTO;
import com.jiangce.yueting.domain.dto.NoteCreateDTO;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.NoteVO;
import com.jiangce.yueting.domain.vo.UserBaseVO;
import com.jiangce.yueting.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 笔记控制器
 */
@Slf4j
@RestController
@RequestMapping("/web/note")
@Tag(name = "笔记相关接口")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "发布笔记")
    @PostMapping
    public Result<Void> createNote(@RequestBody NoteCreateDTO createDTO) {
        Long userId = BaseContext.getCurrentId();
        noteService.createNote(userId, createDTO);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "删除笔记")
    @DeleteMapping("/{noteId}")
    public Result<Void> deleteNote(@Parameter(description = "笔记ID") @PathVariable Long noteId) {
        Long userId = BaseContext.getCurrentId();
        noteService.deleteNote(noteId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取关注用户的笔记列表")
    @GetMapping("/following")
    public Result<List<NoteVO>> getFollowingNotes() {
        Long userId = BaseContext.getCurrentId();
        List<NoteVO> notes = noteService.getFollowingNotes(userId);
        return Result.success(notes);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取我的笔记列表")
    @GetMapping("/my")
    public Result<List<NoteVO>> getMyNotes() {
        Long userId = BaseContext.getCurrentId();
        List<NoteVO> notes = noteService.getMyNotes(userId);
        return Result.success(notes);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "点赞笔记")
    @PostMapping("/{noteId}/like")
    public Result<Void> likeNote(@Parameter(description = "笔记ID") @PathVariable Long noteId) {
        Long userId = BaseContext.getCurrentId();
        noteService.likeNote(noteId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "取消点赞笔记")
    @DeleteMapping("/{noteId}/like")
    public Result<Void> unlikeNote(@Parameter(description = "笔记ID") @PathVariable Long noteId) {
        Long userId = BaseContext.getCurrentId();
        noteService.unlikeNote(noteId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "评论笔记")
    @PostMapping("/{noteId}/comment")
    public Result<Void> commentNote(
            @Parameter(description = "笔记ID") @PathVariable Long noteId,
            @RequestBody NoteCommentDTO commentDTO) {
        Long userId = BaseContext.getCurrentId();
        commentDTO.setNoteId(noteId);
        noteService.commentNote(noteId, userId, commentDTO);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "删除评论")
    @DeleteMapping("/{noteId}/comment/{commentId}")
    public Result<Void> deleteComment(
            @Parameter(description = "笔记ID") @PathVariable Long noteId,
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = BaseContext.getCurrentId();
        noteService.deleteComment(noteId, commentId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "转发笔记")
    @PostMapping("/{noteId}/forward")
    public Result<Void> forwardNote(@Parameter(description = "笔记ID") @PathVariable Long noteId) {
        Long userId = BaseContext.getCurrentId();
        noteService.forwardNote(noteId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取笔记详情")
    @GetMapping("/{noteId}")
    public Result<NoteVO> getNoteDetail(@Parameter(description = "笔记ID") @PathVariable Long noteId) {
        Long userId = BaseContext.getCurrentId();
        NoteVO note = noteService.getNoteDetail(noteId, userId);
        return Result.success(note);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取热门用户")
    @GetMapping("/popular-users")
    public Result<List<UserBaseVO>> getPopularUsers(
            @Parameter(description = "数量") @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = BaseContext.getCurrentId();
        List<UserBaseVO> users = noteService.getPopularUsers(userId, limit);
        return Result.success(users);
    }
}