package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.SongPlaylist;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 歌曲-歌单关联 Mapper
 */
@Mapper
public interface SongPlaylistMapper extends BaseMapper<SongPlaylist> {

    /**
     * 根据歌曲ID批量删除歌单关联记录
     *
     * @param songId 歌曲ID列表
     */
    void deleteBySongIds(List<Long> songId);
}
