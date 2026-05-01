package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.PlaylistSongCount;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.vo.PlaylistWithUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 歌单 Mapper
 */
@Mapper
public interface PlaylistMapper extends BaseMapper<Playlist> {

    /**
     * 查询歌单详情（含用户信息和标签）
     *
     * @param id 歌单ID
     * @return 歌单详情
     */
    PlaylistWithUser selectPlaylistWithDetail(Long id);

    /**
     * 根据标签分页查询歌单列表
     *
     * @param tagId 标签ID
     * @param page 分页参数
     * @return 歌单分页列表
     */
    IPage<PlaylistWithUser> selectPlaylistByTag(Integer tagId, Page<PlaylistWithUser> page);

    /**
     * 根据关键词搜索歌单
     *
     * @param keyword 关键词
     * @param page 分页参数
     * @return 歌单分页列表
     */
    IPage<PlaylistWithUser> selectPlaylistByName(String keyword, Page<PlaylistWithUser> page);

    /**
     * 批量查询歌单的歌曲数量
     *
     * @param songIds 歌曲ID列表
     * @return 歌单歌曲数量列表
     */
    List<PlaylistSongCount> getPlaylistSongCounts(@Param("songIds") List<Long> songIds);

    /**
     * 批量更新歌单歌曲数量
     *
     * @param updateList 更新列表
     * @return 影响行数
     */
    int batchUpdateSongCounts(@Param("list") List<PlaylistSongCount> updateList);

    /**
     * 增加歌单歌曲数量
     *
     * @param playlistId 歌单ID
     * @param i 增加的数量
     */
    default void addSongCount(@Param("playlistId") Long playlistId, @Param("i") int i) {
        Playlist playlist = selectById(playlistId);
        if (playlist != null) {
            playlist.setSongCount(playlist.getSongCount() + i);
            updateById(playlist);
        }
    }
}
