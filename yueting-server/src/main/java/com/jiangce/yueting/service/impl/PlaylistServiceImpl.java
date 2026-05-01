package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.common.PlaylistContent;
import com.jiangce.yueting.domain.dto.PlaylistUpdateDTO;
import com.jiangce.yueting.domain.po.*;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.PlaylistWithSongs;
import com.jiangce.yueting.domain.vo.PlaylistWithUser;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import com.jiangce.yueting.mapper.*;
import com.jiangce.yueting.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistMapper playlistMapper;
    private final SongMapper songMapper;
    private final SongPlaylistMapper songPlaylistMapper;
    private final PlaylistTagMapper playlistTagMapper;
    private final UserPlaylistMapper userPlaylistMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void create(String playlistName) {
        Playlist playlist = Playlist.builder()
                .playlistName(playlistName)
                .userId(BaseContext.getCurrentId())
                .coverUrl(PlaylistContent.DEFAULT_COVER_URL)
                .createdAt(LocalDateTime.now())
                .build();
        playlistMapper.insert(playlist);
    }

    @Override
    public PlaylistWithSongs getPlaylistWithSongs(Long id) {
        PlaylistWithSongs playlistWithSongs = new PlaylistWithSongs();

        Playlist playlist = playlistMapper.selectById(id);
        if (playlist == null) {
            return playlistWithSongs;
        }

        PlaylistWithUser playlistDetail = playlistMapper.selectPlaylistWithDetail(id);
        if (playlistDetail != null) {
            playlistWithSongs.setPlaylist(playlistDetail);
        }

        // 系统榜单
        if (Boolean.TRUE.equals(playlist.getIsSystem())) {
            String redisKey = "toplistSongs:" + id;

            // 1. 尝试从Redis获取榜单数据
            List<SongBaseVO> songList = (List<SongBaseVO>) redisTemplate.opsForValue().get(redisKey);

            if (songList != null) {
                playlistWithSongs.setSongs(songList);
                return playlistWithSongs;
            }

            songList = switch (playlist.getPlaylistName()) {
                case "飙升榜" -> songMapper.selectSoaringListByYesterday();
                case "热歌榜" -> songMapper.selectHotListBy7Days();
                case "新歌榜" -> {
                    Date oneWeekAgo = Date.from(Instant.now().minus(7, ChronoUnit.DAYS));
                    yield songMapper.selectNewListByHot(oneWeekAgo);
                }
                default -> new ArrayList<>();
            };
            playlistWithSongs.setSongs(songList);
            redisTemplate.opsForValue().set(redisKey, songList, 10, TimeUnit.MINUTES);
            return playlistWithSongs;
        }

        // 普通歌单
        List<SongBaseVO> songs = songMapper.selectSongsByPlaylistId(id);
        playlistWithSongs.setSongs(songs);
        return playlistWithSongs;
    }

    @Override
    @Transactional
    public void addSongToPlaylist(Long playlistId, Long songId) {
        // 检查用户是否为歌单的创建者
        checkUserIsPlaylistCreator(playlistId, BaseContext.getCurrentId());
        int i = songPlaylistMapper.insert(new SongPlaylist(playlistId, songId, LocalDateTime.now()));
        // 修改歌单内歌曲数量
        playlistMapper.addSongCount(playlistId, i);
    }

    @Override
    @Transactional
    public void deleteFormPlaylist(Long playlistId, Long songId) {
        // 检查用户是否为歌单的创建者
        checkUserIsPlaylistCreator(playlistId, BaseContext.getCurrentId());

        LambdaQueryWrapper<SongPlaylist> songPlaylistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songPlaylistLambdaQueryWrapper.eq(SongPlaylist::getPlaylistId, playlistId);
        songPlaylistLambdaQueryWrapper.eq(SongPlaylist::getSongId, songId);
        int i = songPlaylistMapper.delete(songPlaylistLambdaQueryWrapper);
        playlistMapper.addSongCount(playlistId, -1 * i);

    }

    @Override
    public void updateCover(Long id, String coverUrl) {
        Playlist playlist = playlistMapper.selectById(id);
        // 检查用户是否可以修改该歌单信息
        checkPlaylistIsEditable(playlist, BaseContext.getCurrentId());

        playlist.setCoverUrl(coverUrl);
        playlistMapper.updateById(playlist);
    }

    @Override
    public PlaylistWithUser getPlaylistWithUser(Long id) {
        return playlistMapper.selectPlaylistWithDetail(id);
    }

    @Override
    public void updateById(PlaylistUpdateDTO playlistUpdateDTO) {
        // 检查用户是否可以修改该歌单信息
        checkPlaylistIsEditable(playlistUpdateDTO.getPlaylistId(), BaseContext.getCurrentId());
        Playlist playlist = playlistUpdateDTO.toPlaylist();
        playlistMapper.updateById(playlist);
        playlistTagMapper.delete(new LambdaQueryWrapper<PlaylistTag>().eq(PlaylistTag::getPlaylistId, playlist.getPlaylistId()));
        for (Integer tagId : playlistUpdateDTO.getTagIds()) {
            playlistTagMapper.insert(new PlaylistTag(playlist.getPlaylistId(), tagId));
        }
    }

    @Override
    public void collectPlaylist(Long id) {
        //检查用户是否已收藏歌单
        LambdaQueryWrapper<UserPlaylist> userPlaylistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userPlaylistLambdaQueryWrapper.eq(UserPlaylist::getUserId, BaseContext.getCurrentId())
                .eq(UserPlaylist::getPlaylistId, id);
        if (!userPlaylistMapper.exists(userPlaylistLambdaQueryWrapper)) {
            userPlaylistMapper.insert(new UserPlaylist(BaseContext.getCurrentId(), id, PlaylistContent.RELATION_TYPE_COLLECT, LocalDateTime.now()));
        } else {
            throw new CustomException("已在收藏列表");
        }

    }

    @Override
    public void cancelCollectPlaylist(Long id) {
        LambdaQueryWrapper<UserPlaylist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlaylist::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(UserPlaylist::getPlaylistId, id);
        userPlaylistMapper.delete(queryWrapper);
    }

    @Override
    public void deletePlaylist(Long id) {
        //删除歌单详情
        LambdaQueryWrapper<SongPlaylist> songPlaylistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songPlaylistLambdaQueryWrapper.eq(SongPlaylist::getPlaylistId, id);
        songPlaylistMapper.delete(songPlaylistLambdaQueryWrapper);
        // 删除歌单
        playlistMapper.deleteById(id);
    }

    @Override
    public List<Playlist> getSystemTopLists() {
        LambdaQueryWrapper<Playlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Playlist::getIsSystem, true)
                .orderByAsc(Playlist::getPlaylistId);
        return playlistMapper.selectList(wrapper);
    }

    @Override
    public List<Playlist> getHotPlaylistsOrderByPlayCount() {
        LambdaQueryWrapper<Playlist> playlistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        playlistLambdaQueryWrapper.eq(Playlist::getIsPublic, true)
                .eq(Playlist::getIsDefault, false)
                .ne(Playlist::getUserId, 1L)
                .orderBy(true, false, Playlist::getPlayCount)
                .last("limit 0,50");
        return playlistMapper.selectList(playlistLambdaQueryWrapper);
    }

    @Override
    public PageResult getPlaylistByTag(Integer tagId, Integer pageNum, Integer pageSize) {
        Page<PlaylistWithUser> page = new Page<>(pageNum, pageSize);
        IPage<PlaylistWithUser> result = playlistMapper.selectPlaylistByTag(tagId, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public PageResult getPlaylistByName(String keyword, Integer pageNum, Integer pageSize) {
        Page<PlaylistWithUser> page = new Page<>(pageNum, pageSize);
        IPage<PlaylistWithUser> result = playlistMapper.selectPlaylistByName(keyword, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Playlist> getCreatePlaylist(Long userId) {
        LambdaQueryWrapper<Playlist> playlistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        playlistLambdaQueryWrapper.eq(Playlist::getUserId, userId);
        playlistLambdaQueryWrapper.orderBy(true, true, Playlist::getCreatedAt);
        List<Playlist> playlists = playlistMapper.selectList(playlistLambdaQueryWrapper);
        return playlists;
    }

    @Override
    public List<Playlist> getCollectPlaylist(Long userId) {
        LambdaQueryWrapper<UserPlaylist> userPlaylistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Long> playlistIds = userPlaylistMapper.selectList(userPlaylistLambdaQueryWrapper.eq(UserPlaylist::getUserId, userId).eq(UserPlaylist::getRelationType, PlaylistContent.RELATION_TYPE_COLLECT)).stream().map(UserPlaylist::getPlaylistId).collect(Collectors.toList());
        if (playlistIds.isEmpty()) {
            return new ArrayList<>();
        }
        return playlistMapper.selectBatchIds(playlistIds);
    }

    //检查用户是否为歌单的创建者
    private void checkUserIsPlaylistCreator(Long playlistId, Long userId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        checkUserIsPlaylistCreator(playlist, userId);
    }

    //检查用户是否为歌单的创建者
    private void checkUserIsPlaylistCreator(Playlist playlist, Long userId) {
        if (playlist == null || !playlist.getUserId().equals(userId)) {
            throw new CustomException("无权限");
        }
    }


    // 检查歌单是否可以修改
    private void checkPlaylistIsEditable(Long playlistId, Long userId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        checkPlaylistIsEditable(playlist, userId);
    }

    // 检查歌单是否可以修改
    private void checkPlaylistIsEditable(Playlist playlist, Long userId) {
        if (playlist == null || !playlist.getUserId().equals(BaseContext.getCurrentId()) || playlist.getIsDefault()) {
            throw new CustomException("无权限");
        }
    }

}
