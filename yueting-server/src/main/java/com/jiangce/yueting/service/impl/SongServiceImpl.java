package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.ApplicationContent;
import com.jiangce.yueting.common.ArtistContent;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.common.SongContent;
import com.jiangce.yueting.domain.dto.PlaylistSongCount;
import com.jiangce.yueting.domain.dto.SongPageDTO;
import com.jiangce.yueting.domain.dto.SongUploadDTO;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.po.Song;
import com.jiangce.yueting.domain.po.SongApplication;
import com.jiangce.yueting.domain.po.SongApplicationApproval;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.ArtistWithIdentitiesVO;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import com.jiangce.yueting.domain.vo.SongWithDetail;
import com.jiangce.yueting.mapper.AlbumMapper;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.mapper.PlaylistMapper;
import com.jiangce.yueting.mapper.SongApplicationApprovalMapper;
import com.jiangce.yueting.mapper.SongApplicationMapper;
import com.jiangce.yueting.mapper.SongArtistMapper;
import com.jiangce.yueting.mapper.SongMapper;
import com.jiangce.yueting.mapper.SongPlaylistMapper;
import com.jiangce.yueting.mapper.UserPlayRecordMapper;
import com.jiangce.yueting.service.AlbumService;
import com.jiangce.yueting.service.ArtistService;
import com.jiangce.yueting.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SongServiceImpl implements SongService {
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;
    private final SongApplicationMapper songApplicationMapper;
    private final SongApplicationApprovalMapper songApplicationApprovalMapper;
    private final SongMapper songMapper;
    private final SongArtistMapper songArtistMapper;
    private final SongPlaylistMapper songPlaylistMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;
    private final SongContent songContent;
    private final UserPlayRecordMapper userPlayRecordMapper;

    @Autowired
    public SongServiceImpl(
            @Lazy AlbumService albumService,
            ArtistService artistService,
            ArtistMapper artistMapper,
            SongApplicationMapper songApplicationMapper,
            SongApplicationApprovalMapper songApplicationApprovalMapper,
            SongMapper songMapper,
            SongArtistMapper songArtistMapper,
            SongPlaylistMapper songPlaylistMapper,
            StringRedisTemplate stringRedisTemplate,
            AlbumMapper albumMapper,
            PlaylistMapper playlistMapper,
            SongContent songContent,
            UserPlayRecordMapper userPlayRecordMapper
    ) {
        this.albumService = albumService;
        this.artistService = artistService;
        this.artistMapper = artistMapper;
        this.songApplicationMapper = songApplicationMapper;
        this.songApplicationApprovalMapper = songApplicationApprovalMapper;
        this.songMapper = songMapper;
        this.songArtistMapper = songArtistMapper;
        this.songPlaylistMapper = songPlaylistMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.albumMapper = albumMapper;
        this.playlistMapper = playlistMapper;
        this.songContent = songContent;
        this.userPlayRecordMapper = userPlayRecordMapper;
    }

    @Override
    @Transactional
    public void uploadSong(SongUploadDTO songUploadDTO) {
        Artist artist = artistService.getCurrentArtist();
        ArtistWithIdentitiesVO artistWithIdentitiesVO = artistService.getCurrentArtistWithIdentities();
        if (artist == null) {
            throw new CustomException("您不是歌手，无法上传歌曲");
        }
        if (artistWithIdentitiesVO.getIdentities().isEmpty()) {
            throw new CustomException("请先激活艺人身份");
        }
        if (artist.getStatus().equals(ArtistContent.STATUS_DISABLE)) {
            throw new CustomException("您的艺人身份被禁用，解封时间" + artist.getStatusExpiredAt());
        }

        Long artistId = artist.getArtistId();
        Long albumId = songUploadDTO.getAlbumId();
        if (songUploadDTO.getIsNewAlbum()) {
            Album album = songUploadDTO.getAlbum();
            albumService.save(album, artistId);
            albumId = album.getAlbumId();
        }

        List<SongApplication> songApplications = new ArrayList<>();
        for (SongUploadDTO.SongInfo songInfo : songUploadDTO.getSongs()) {
            SongApplication songApplication = songInfo.getSongApplication();
            songApplication.setArtistId(artistId);
            songApplication.setAlbumId(albumId);
            songApplication.setAppliedAt(LocalDateTime.now());
            songApplication.setStatus(ApplicationContent.STATUS_UNREVIEWED);
            songApplications.add(songApplication);
        }
        for (SongApplication songApplication : songApplications) {
            songApplicationMapper.insert(songApplication);
        }

        List<SongApplicationApproval> songApplicationApprovals = new ArrayList<>();
        Set<Long> artistSet = new HashSet<>();
        for (int i = 0; i < songUploadDTO.getSongs().size(); i++) {
            SongUploadDTO.SongInfo songInfo = songUploadDTO.getSongs().get(i);
            artistSet.clear();
            artistSet.addAll(songInfo.getSingerIds());
            artistSet.addAll(songInfo.getLyricistIds());
            artistSet.addAll(songInfo.getComposerIds());
            for (Long targetArtistId : artistSet) {
                songApplicationApprovals.add(
                        new SongApplicationApproval(
                                null,
                                songApplications.get(i).getApplicationId(),
                                targetArtistId,
                                LocalDateTime.now(),
                                null,
                                ApplicationContent.STATUS_UNREVIEWED
                        )
                );
            }
        }
        songApplicationApprovalMapper.insertBatch(songApplicationApprovals);
    }

    @Override
    public List<SongBaseVO> getHotSongsByArtistId(Long artistId) {
        Page<SongBaseVO> page = new Page<>(1, 50);
        IPage<SongBaseVO> result = songMapper.getHotSongsByArtistId(artistId, page);
        return result.getRecords();
    }

    @Override
    @Transactional
    public void removeMusicByAlbumId(Long albumId) {
        List<SongBaseVO> songBaseVOS = songMapper.selectSongsByAlbumId(albumId);
        List<Long> songIds = songBaseVOS.stream().map(SongBaseVO::getSongId).toList();
        if (songIds.isEmpty()) {
            return;
        }

        List<PlaylistSongCount> playlistCounts = playlistMapper.getPlaylistSongCounts(songIds);
        if (!playlistCounts.isEmpty()) {
            playlistMapper.batchUpdateSongCounts(playlistCounts);
        }

        LambdaUpdateWrapper<Song> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Song::getSongId, songIds)
                .set(Song::getStatus, SongContent.STATUS_DELETED);
        songMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void removeMusic(Long songId) {
        Song song = songMapper.selectById(songId);
        if (song == null) {
            throw new CustomException("歌曲不存在");
        }
        if (Objects.equals(song.getStatus(), SongContent.STATUS_DELETED)) {
            throw new CustomException("歌曲已下架");
        }

        SongWithDetail songWithDetail = songMapper.selectWithDetailBySongId(songId);
        if (songWithDetail == null || songWithDetail.getArtistIds() == null) {
            throw new CustomException("歌曲不存在");
        }

        Set<Long> artistSet = Arrays.stream(songWithDetail.getArtistIds().split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        Artist artist = artistService.getCurrentArtist();
        if (artist == null) {
            throw new CustomException("您不是歌手，无法删除歌曲");
        }
        if (!artistSet.contains(artist.getArtistId())) {
            throw new CustomException("您不是该歌曲的所属艺人，无法删除歌曲");
        }

        List<Long> songIds = List.of(songId);
        List<PlaylistSongCount> playlistCounts = playlistMapper.getPlaylistSongCounts(songIds);
        if (!playlistCounts.isEmpty()) {
            playlistMapper.batchUpdateSongCounts(playlistCounts);
        }

        Song updateSong = new Song();
        updateSong.setSongId(songId);
        updateSong.setStatus(SongContent.STATUS_DELETED);
        songMapper.updateById(updateSong);
    }

    @Override
    public ResponseEntity<Resource> playMusic(Long songId) {
        Song song = songMapper.selectById(songId);
        if (song == null) {
            throw new CustomException("歌曲不存在");
        }
        if (!Objects.equals(song.getStatus(), SongContent.STATUS_USABLE)) {
            throw new CustomException("音乐已下架");
        }

        String filename = song.getSongUrl();
        Path filePath = Paths.get(songContent.storagePath + "/", filename).normalize();

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        if (resource.exists() || resource.isReadable()) {
            log.info("文件存在或可读：{}", filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .header("Access-Control-Allow-Origin", "*")
                    .body(resource);
        }

        log.info("文件不存在或无法读取：{}", filename);
        return ResponseEntity.status(404).body(null);
    }

    @Override
    public SongWithDetail getSongWithDetailBySongId(Long id) {
        Song song = songMapper.selectById(id);
        if (song == null) {
            throw new CustomException("歌曲不存在");
        }
        SongWithDetail songWithDetail = songMapper.selectWithDetailBySongIdIncludeDeleted(id);
        if (songWithDetail == null) {
            throw new CustomException("歌曲不存在");
        }
        return songWithDetail;
    }

    @Override
    public PageResult searchSong(String keyword, Integer pageNum, Integer pageSize) {
        Page<SongBaseVO> page = new Page<>(pageNum, pageSize);
        IPage<SongBaseVO> result = songMapper.selectSongsByKeyword(keyword, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public PageResult searchSongsByLyrics(String keyword, Integer pageNum, Integer pageSize) {
        Page<SongWithDetail> page = new Page<>(pageNum, pageSize);
        IPage<SongWithDetail> result = songMapper.searchSongByLyrics(keyword, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public PageResult pageSongWithDetail(SongPageDTO songPageDTO) {
        Page<SongWithDetail> page = new Page<>(songPageDTO.getPageNum(), songPageDTO.getPageSize());
        IPage<SongWithDetail> result = songMapper.pageMusicWithDetail(songPageDTO, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public void incrementPlay(Long id) {
        stringRedisTemplate.opsForHash().increment(SongContent.PLAY_COUNT_KEY, id.toString(), 1);
    }

    @Override
    public void recordUserPlay(Long userId, Long songId) {
        userPlayRecordMapper.insertPlayRecord(userId, songId, LocalDateTime.now());
    }
}
