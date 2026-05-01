package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.Note;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记 Mapper
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}