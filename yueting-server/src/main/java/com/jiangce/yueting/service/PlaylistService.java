package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.PlaylistUpdateDTO;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.PlaylistWithSongs;
import com.jiangce.yueting.domain.vo.PlaylistWithUser;

import java.util.List;

public interface PlaylistService {
    void create(String playlistName);

    PlaylistWithSongs getPlaylistWithSongs(Long id);

    void addSongToPlaylist(Long playlistId, Long songId);

    void deleteFormPlaylist(Long playlistId, Long songId);

    void updateCover(Long id, String coverUrl);

    PlaylistWithUser getPlaylistWithUser(Long id);

    void updateById(PlaylistUpdateDTO playlistUpdateDTO);

    void collectPlaylist(Long id);

    void cancelCollectPlaylist(Long id);

    void deletePlaylist(Long id);

    List<Playlist> getSystemTopLists();

    List<Playlist> getHotPlaylistsOrderByPlayCount();

    PageResult getPlaylistByTag(Integer tagId, Integer pageNum, Integer pageSize);

    PageResult getPlaylistByName(String keyword,  Integer pageNum, Integer pageSize);

    List<Playlist> getCreatePlaylist(Long userId);

    List<Playlist> getCollectPlaylist(Long userId);
}
