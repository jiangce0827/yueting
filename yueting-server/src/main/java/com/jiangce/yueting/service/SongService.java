package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.SongPageDTO;
import com.jiangce.yueting.domain.dto.SongUploadDTO;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.SongWithDetail;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SongService {
    /**
     * 上传歌曲
     */
    void uploadSong(SongUploadDTO songUploadDTO);

    /**
     * 获取艺人热门歌曲
     * @param artistId 艺人id
     */
    List<SongBaseVO> getHotSongsByArtistId(Long artistId);

    /**
     * 根据专辑id删除专辑内歌曲及艺人歌曲关联信息
     * @param albumId 专辑id
     */
    void removeMusicByAlbumId(Long albumId);

    /**
     * 根据歌曲id删除歌曲及艺人歌曲关联信息
     */
    void removeMusic(Long songId);


    /**
     * 播放歌曲
     * @param songId 歌曲id
     */
    ResponseEntity<Resource> playMusic(Long songId);

    SongWithDetail getSongWithDetailBySongId(Long id);

    PageResult searchSong(String keyword, Integer pageNum, Integer pageSize);

    PageResult searchSongsByLyrics(String keyword, Integer pageNum, Integer pageSize);

    PageResult pageSongWithDetail(SongPageDTO songPageDTO);

    void incrementPlay(Long id);

    void recordUserPlay(Long userId, Long songId);
}
