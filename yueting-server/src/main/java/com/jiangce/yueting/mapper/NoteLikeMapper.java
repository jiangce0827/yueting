package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.NoteLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 笔记点赞 Mapper
 */
@Mapper
public interface NoteLikeMapper extends BaseMapper<NoteLike> {

    /**
     * 检查用户是否已点赞笔记
     */
    default boolean existsByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId) {
        return selectCount(new LambdaQueryWrapper<NoteLike>()
                .eq(NoteLike::getNoteId, noteId)
                .eq(NoteLike::getUserId, userId)) > 0;
    }

    /**
     * 删除用户对笔记的点赞
     */
    default int deleteByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId) {
        return delete(new LambdaQueryWrapper<NoteLike>()
                .eq(NoteLike::getNoteId, noteId)
                .eq(NoteLike::getUserId, userId));
    }
}