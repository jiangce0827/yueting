package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签 Mapper
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
