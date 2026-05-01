package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.UserPlaylist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-歌单关联 Mapper
 */
@Mapper
public interface UserPlaylistMapper extends BaseMapper<UserPlaylist> {
}
