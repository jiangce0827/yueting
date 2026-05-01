package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.PlaylistTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 歌单-标签关联 Mapper
 */
@Mapper
public interface PlaylistTagMapper extends BaseMapper<PlaylistTag> {
}
