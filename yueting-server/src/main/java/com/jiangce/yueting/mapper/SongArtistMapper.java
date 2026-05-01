package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.SongArtist;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 歌曲-艺人关联 Mapper
 */
@Mapper
public interface SongArtistMapper extends BaseMapper<SongArtist> {

    /**
     * 批量插入歌曲-艺人关联
     *
     * @param songArtistList 歌曲-艺人关联列表
     */
    void insertBatch(List<SongArtist> songArtistList);

    /**
     * 根据歌曲ID批量删除关联记录
     *
     * @param songIds 歌曲ID列表
     */
    void deleteBySongIds(List<Long> songIds);
}
