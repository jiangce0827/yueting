package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.NoteForward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 笔记转发 Mapper
 */
@Mapper
public interface NoteForwardMapper extends BaseMapper<NoteForward> {

    /**
     * 检查用户是否已转发笔记
     */
    default boolean existsByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId) {
        return selectCount(new LambdaQueryWrapper<NoteForward>()
                .eq(NoteForward::getNoteId, noteId)
                .eq(NoteForward::getUserId, userId)) > 0;
    }

    /**
     * 删除用户对笔记的转发
     */
    default int deleteByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId) {
        return delete(new LambdaQueryWrapper<NoteForward>()
                .eq(NoteForward::getNoteId, noteId)
                .eq(NoteForward::getUserId, userId));
    }
}