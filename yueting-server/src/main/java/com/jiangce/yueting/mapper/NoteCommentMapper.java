package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.NoteComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记评论 Mapper
 */
@Mapper
public interface NoteCommentMapper extends BaseMapper<NoteComment> {
}