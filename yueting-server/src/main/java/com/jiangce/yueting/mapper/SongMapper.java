package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.SongPageDTO;
import com.jiangce.yueting.domain.po.Song;
import com.jiangce.yueting.domain.vo.SongWithDetail;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 歌曲 Mapper
 */
@Mapper
public interface SongMapper extends BaseMapper<Song> {

    /**
     * 获取歌手热门歌曲分页列表
     *
     * @param artistId 歌手ID
     * @param page 分页参数
     * @return 歌曲分页列表
     */
    IPage<SongBaseVO> getHotSongsByArtistId(Long artistId, Page<SongBaseVO> page);

    /**
     * 根据专辑ID查询歌曲列表
     *
     * @param albumId 专辑ID
     * @return 歌曲列表
     */
    List<SongBaseVO> selectSongsByAlbumId(Long albumId);

    /**
     * 根据专辑ID查询歌曲列表（包含已下架歌曲）
     *
     * @param albumId 专辑ID
     * @return 歌曲列表
     */
    List<SongBaseVO> selectSongsByAlbumIdForArtist(@Param("albumId") Long albumId);

    /**
     * 根据歌单ID查询歌曲列表
     *
     * @param id 歌单ID
     * @return 歌曲列表
     */
    List<SongBaseVO> selectSongsByPlaylistId(Long id);

    /**
     * 根据关键词搜索歌曲
     *
     * @param keyword 关键词
     * @param page 分页参数
     * @return 歌曲分页列表
     */
    IPage<SongBaseVO> selectSongsByKeyword(String keyword, Page<SongBaseVO> page);

    /**
     * 根据歌曲ID查询歌曲详情（含关联信息）
     *
     * @param id 歌曲ID
     * @return 歌曲详情
     */
    SongWithDetail selectWithDetailBySongId(Long id);

    /**
     * 根据歌曲ID查询歌曲详情（包含已下架歌曲）
     *
     * @param id 歌曲ID
     * @return 歌曲详情
     */
    SongWithDetail selectWithDetailBySongIdIncludeDeleted(@Param("id") Long id);

    /**
     * 根据歌词关键词搜索歌曲
     *
     * @param keyword 歌词关键词
     * @param page 分页参数
     * @return 歌曲分页列表
     */
    IPage<SongWithDetail> searchSongByLyrics(String keyword, Page<SongWithDetail> page);

    /**
     * 分页查询歌曲列表（含详情）
     *
     * @param songPageDTO 查询条件
     * @param page 分页参数
     * @return 歌曲分页列表
     */
    IPage<SongWithDetail> pageMusicWithDetail(SongPageDTO songPageDTO, Page<SongWithDetail> page);

    /**
     * 增加歌曲播放量
     *
     * @param id 歌曲ID
     * @param inc 增加的播放量
     */
    void addPlayCountByMusic(@Param("songId") Long id, @Param("inc") int inc);

    /**
     * 查询最新热门歌曲列表
     *
     * @param startTime 起始时间
     * @return 歌曲列表
     */
    List<SongBaseVO> selectNewListByHot(@Param("startTime") Date startTime);

    /**
     * 按昨日播放量排行
     *
     * @return 歌曲列表
     */
    List<SongBaseVO> selectSoaringListByYesterday();

    /**
     * 按7日内播放量排行
     *
     * @return 歌曲列表
     */
    List<SongBaseVO> selectHotListBy7Days();

    /**
     * 根据歌曲ID列表批量查询歌曲
     *
     * @param songIds 歌曲ID列表
     * @return 歌曲列表
     */
    List<SongBaseVO> selectSongsByIds(@Param("songIds") List<Long> songIds);

    /**
     * 查询所有歌曲URL列表
     *
     * @return 歌曲URL列表
     */
    default List<String> selectAllSongUrls() {
        return selectList(null).stream()
                .map(Song::getSongUrl)
                .toList();
    }

    /**
     * 获取所有歌曲文件名集合
     *
     * @return 歌曲文件名集合
     */
    default Set<String> getAllSongFileNames() {
        return selectList(null).stream()
                .map(Song::getSongUrl)
                .collect(java.util.stream.Collectors.toSet());
    }
}
