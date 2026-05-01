package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.ArtistNotificationContent;
import com.jiangce.yueting.common.ApplicationContent;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.common.SongArtistContent;
import com.jiangce.yueting.domain.dto.SongApplicationPageDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.po.Song;
import com.jiangce.yueting.domain.po.SongApplication;
import com.jiangce.yueting.domain.po.SongArtist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.SongApplicationVO;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.mapper.SongApplicationMapper;
import com.jiangce.yueting.mapper.SongArtistMapper;
import com.jiangce.yueting.mapper.SongMapper;
import com.jiangce.yueting.service.ArtistService;
import com.jiangce.yueting.service.NotificationService;
import com.jiangce.yueting.service.SongApplicationService;
import com.jiangce.yueting.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongApplicationServiceImpl implements SongApplicationService {
    private final SongApplicationMapper songApplicationMapper;
    private final SongMapper songMapper;
    private final SongArtistMapper songArtistMapper;
    private final ArtistMapper artistMapper;
    private final ArtistService artistService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void approveSong(Long id) {
        SongApplication songApplication = songApplicationMapper.selectById(id);
        if (!songApplication.getStatus().equals(ApplicationContent.STATUS_UNREVIEWED)) {
            throw new CustomException("申请已被审核");
        }

        songApplication.setStatus(ApplicationContent.STATUS_APPROVE);
        songApplication.setReviewedBy(BaseContext.getCurrentId());
        songApplication.setReviewedAt(LocalDateTime.now());
        songApplicationMapper.updateById(songApplication);

        Song song = songApplication.toSong();
        songMapper.insert(song);

        List<SongArtist> songArtistList = new ArrayList<>();
        List<Long> singerIds = StringUtils.stringToLongList(songApplication.getSingerIds());
        for (Long singerId : singerIds) {
            songArtistList.add(new SongArtist(song.getSongId(), singerId, SongArtistContent.ROLE_TYPE_SINGER));
        }
        List<Long> lyricistIds = StringUtils.stringToLongList(songApplication.getLyricistIds());
        for (Long lyricistId : lyricistIds) {
            songArtistList.add(new SongArtist(song.getSongId(), lyricistId, SongArtistContent.ROLE_TYPE_LYRICIST));
        }
        List<Long> composerIds = StringUtils.stringToLongList(songApplication.getComposerIds());
        for (Long composerId : composerIds) {
            songArtistList.add(new SongArtist(song.getSongId(), composerId, SongArtistContent.ROLE_TYPE_COMPOSER));
        }
        songArtistMapper.insertBatch(songArtistList);

        createSongReviewNotification(songApplication, ArtistNotificationContent.TYPE_SONG_APPROVED,
                "你提交的歌曲《" + songApplication.getSongName() + "》已审核通过");
    }

    @Override
    @Transactional
    public void rejectSong(Long id, String rejectionReason) {
        SongApplication songApplication = songApplicationMapper.selectById(id);
        if (!songApplication.getStatus().equals(ApplicationContent.STATUS_UNREVIEWED)) {
            throw new CustomException("申请已被审核");
        }

        songApplication.setStatus(ApplicationContent.STATUS_REJECT);
        songApplication.setReviewedBy(BaseContext.getCurrentId());
        songApplication.setRejectionReason(rejectionReason);
        songApplication.setReviewedAt(LocalDateTime.now());
        songApplicationMapper.updateById(songApplication);

        String reason = rejectionReason == null || rejectionReason.isBlank() ? "无" : rejectionReason;
        createSongReviewNotification(songApplication, ArtistNotificationContent.TYPE_SONG_REJECTED,
                "你提交的歌曲《" + songApplication.getSongName() + "》未通过审核，原因：" + reason);
    }

    @Override
    public PageResult pageSongApplication(SongApplicationPageDTO songApplicationPageDTO) {
        return querySongApplicationPage(songApplicationPageDTO);
    }

    @Override
    public PageResult pageMySongApplication(SongApplicationPageDTO songApplicationPageDTO) {
        Artist currentArtist = artistService.getCurrentArtist();
        if (currentArtist == null) {
            throw new CustomException("您还不是音乐人，无法查看审核记录");
        }
        songApplicationPageDTO.setArtistId(currentArtist.getArtistId());
        return querySongApplicationPage(songApplicationPageDTO);
    }

    private PageResult querySongApplicationPage(SongApplicationPageDTO songApplicationPageDTO) {
        Page<SongApplicationVO> page = new Page<>(songApplicationPageDTO.getPageNum(), songApplicationPageDTO.getPageSize());
        IPage<SongApplicationVO> result = songApplicationMapper.pageMusicReview(songApplicationPageDTO, page);
        hydrateArtistNames(result.getRecords());
        return new PageResult(result.getTotal(), result.getRecords());
    }

    private void hydrateArtistNames(List<SongApplicationVO> records) {
        List<Long> artistIds = new ArrayList<>();
        records.forEach(songApplication -> {
            artistIds.addAll(parseArtistIds(songApplication.getSingerIds()));
            artistIds.addAll(parseArtistIds(songApplication.getLyricistIds()));
            artistIds.addAll(parseArtistIds(songApplication.getComposerIds()));
        });

        Map<Long, Artist> artistMap = artistIds.isEmpty()
            ? new HashMap<>()
            : artistMapper.selectArtistsMapByIds(artistIds);

        records.forEach(songApplication -> {
            songApplication.setSingerNames(joinArtistNames(songApplication.getSingerIds(), artistMap));
            songApplication.setLyricistNames(joinArtistNames(songApplication.getLyricistIds(), artistMap));
            songApplication.setComposerNames(joinArtistNames(songApplication.getComposerIds(), artistMap));
        });
    }

    private List<Long> parseArtistIds(String rawIds) {
        List<Long> ids = new ArrayList<>();
        if (rawIds == null || rawIds.isBlank()) {
            return ids;
        }
        for (String rawId : rawIds.split(",")) {
            if (!rawId.isBlank()) {
                ids.add(Long.parseLong(rawId));
            }
        }
        return ids;
    }

    private String joinArtistNames(String rawIds, Map<Long, Artist> artistMap) {
        if (rawIds == null || rawIds.isBlank()) {
            return "";
        }

        List<String> names = new ArrayList<>();
        for (String rawId : rawIds.split(",")) {
            if (rawId.isBlank()) {
                continue;
            }
            Long artistId = Long.parseLong(rawId);
            Artist artist = artistMap.get(artistId);
            names.add(artist != null ? artist.getArtistName() : "未知");
        }
        return String.join(",", names);
    }

    private void createSongReviewNotification(SongApplication songApplication, Integer type, String content) {
        Artist artist = artistMapper.selectById(songApplication.getArtistId());
        if (artist == null || artist.getUserId() == null) {
            return;
        }

        notificationService.createNotification(
                artist.getUserId(),
                null,
                type,
                content,
                songApplication.getApplicationId(),
                ArtistNotificationContent.TARGET_TYPE_SONG_APPLICATION,
                songApplication.getAlbumId()
        );
    }
}
