package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.UserPlayRecord;
import com.jiangce.yueting.domain.vo.UserPlayRankingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户播放记录 Mapper
 */
@Mapper
public interface UserPlayRecordMapper extends BaseMapper<UserPlayRecord> {

    /**
     * 插入播放记录
     *
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @param playTime 播放时间
     */
    void insertPlayRecord(@Param("userId") Long userId, @Param("songId") Long songId, @Param("playTime") LocalDateTime playTime);

    /**
     * 查询用户播放排行榜
     *
     * @param userId 用户ID
     * @param startTime 起始时间
     * @return 播放排行列表
     */
    List<UserPlayRankingVO> selectUserPlayRanking(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
}
