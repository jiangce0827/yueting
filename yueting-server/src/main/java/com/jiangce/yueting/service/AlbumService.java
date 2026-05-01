package com.jiangce.yueting.service;


import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.AlbumWithSongs;

import java.util.List;

public interface AlbumService {
    /**
     * 根据用户id获取所有专辑
     * @param userId 当前用户id
     */
    List<Album> getAllByUserId(Long userId);

    /**
     * 保存专辑
     * @param album 专辑
     * @param artistId 作者艺人id
     */
    void save(Album album, Long artistId);

    /**
     * 根据艺人id搜索专辑
     */
    PageResult searchAlbumsByArtistId(Long artistId, Integer pageNum, Integer pageSize);

    /**
     * 根据专辑id获取专辑详情
     */
    AlbumWithSongs getAlbumWithSongs(Long albumId);

    /**
     * 获取当前音乐人的专辑详情（包含已删除歌曲）
     */
    AlbumWithSongs getMyAlbumWithSongs(Long albumId);

    /**
     * 删除专辑
     * @param albumId 专辑id
     */
    void deleteAlbum(Long albumId);

    PageResult searchAlbumsByKeyword(String keyword, Integer pageNum, Integer pageSize);
}
