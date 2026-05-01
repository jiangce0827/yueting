package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.NoteCommentDTO;
import com.jiangce.yueting.domain.dto.NoteCreateDTO;
import com.jiangce.yueting.domain.vo.NoteVO;

import java.util.List;

/**
 * 笔记服务接口
 */
public interface NoteService {

    /**
     * 发布笔记
     */
    void createNote(Long userId, NoteCreateDTO createDTO);

    /**
     * 删除笔记
     */
    void deleteNote(Long noteId, Long userId);

    /**
     * 获取关注用户的笔记列表
     */
    List<NoteVO> getFollowingNotes(Long userId);

    /**
     * 获取我的笔记列表
     */
    List<NoteVO> getMyNotes(Long userId);

    /**
     * 点赞笔记
     */
    void likeNote(Long noteId, Long userId);

    /**
     * 取消点赞笔记
     */
    void unlikeNote(Long noteId, Long userId);

    /**
     * 评论笔记
     */
    void commentNote(Long noteId, Long userId, NoteCommentDTO commentDTO);

    /**
     * 删除评论
     */
    void deleteComment(Long noteId, Long commentId, Long userId);

    /**
     * 转发笔记
     */
    void forwardNote(Long noteId, Long userId);

    /**
     * 获取笔记详情
     */
    NoteVO getNoteDetail(Long noteId, Long userId);

    /**
     * 获取热门用户
     */
    List<com.jiangce.yueting.domain.vo.UserBaseVO> getPopularUsers(Long userId, int limit);
}