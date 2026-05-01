package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.SongDailyPlayCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface SongDailyPlayCountMapper extends BaseMapper<SongDailyPlayCount> {

    /**
     * 插入或累加当日播放量
     *
     * @param songId    歌曲ID
     * @param playDate  播放日期
     * @param playCount 增加播放量
     */
    void insertOrUpdate(@Param("songId") Long songId, @Param("playDate") LocalDate playDate, @Param("playCount") int playCount);
}
