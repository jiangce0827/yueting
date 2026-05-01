package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.po.Banner;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.AlbumWithAuthor;
import com.jiangce.yueting.domain.vo.BannerAdminVO;
import com.jiangce.yueting.domain.vo.BannerTargetOptionVO;
import com.jiangce.yueting.domain.vo.BannerVO;
import com.jiangce.yueting.domain.vo.PlaylistWithUser;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import com.jiangce.yueting.domain.vo.SongWithDetail;
import com.jiangce.yueting.mapper.AlbumMapper;
import com.jiangce.yueting.mapper.BannerMapper;
import com.jiangce.yueting.service.AlbumService;
import com.jiangce.yueting.service.BannerService;
import com.jiangce.yueting.service.PlaylistService;
import com.jiangce.yueting.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 轮播图 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;
    private final SongService songService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;
    private final AlbumMapper albumMapper;

    @Override
    public PageResult pageBanners(Integer pageNum, Integer pageSize) {
        Page<Banner> page = new Page<>(pageNum, pageSize);
        IPage<Banner> result = bannerMapper.pageBanners(page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public List<BannerVO> getEnabledBanners() {
        return bannerMapper.selectEnabledBanners();
    }

    @Override
    public BannerAdminVO getBannerById(Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new CustomException("轮播图不存在");
        }
        return BannerAdminVO.builder()
                .bannerId(banner.getBannerId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .targetType(banner.getTargetType())
                .targetId(banner.getTargetId())
                .targetUrl(banner.getTargetUrl())
                .sortOrder(banner.getSortOrder())
                .status(banner.getStatus())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void addBanner(Banner banner) {
        bannerMapper.insert(banner);
        log.info("新增轮播图: {}", banner.getTitle());
    }

    @Override
    @Transactional
    public void updateBanner(Banner banner) {
        Banner existing = bannerMapper.selectById(banner.getBannerId());
        if (existing == null) {
            throw new CustomException("轮播图不存在");
        }
        bannerMapper.updateById(banner);
        log.info("更新轮播图: {}", banner.getBannerId());
    }

    @Override
    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new CustomException("轮播图不存在");
        }
        bannerMapper.deleteById(bannerId);
        log.info("删除轮播图: {}", bannerId);
    }

    @Override
    @Transactional
    public void updateStatus(Long bannerId, Integer status) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new CustomException("轮播图不存在");
        }
        banner.setStatus(status);
        bannerMapper.updateById(banner);
        log.info("更新轮播图状态: bannerId={}, status={}", bannerId, status);
    }
    @Override
    public List<BannerTargetOptionVO> searchBannerTargets(Integer targetType, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        String normalizedKeyword = keyword.trim();
        return switch (targetType) {
            case 1 -> mapSongTargets(songService.searchSong(normalizedKeyword, 1, 10));
            case 2 -> mapAlbumTargets(albumService.searchAlbumsByKeyword(normalizedKeyword, 1, 10));
            case 3 -> mapPlaylistTargets(playlistService.getPlaylistByName(normalizedKeyword, 1, 10));
            default -> throw new CustomException("涓嶆敮鎸佺殑璺宠浆绫诲瀷");
        };
    }

    @Override
    public BannerTargetOptionVO getBannerTargetOption(Integer targetType, String targetId) {
        if (!StringUtils.hasText(targetId)) {
            throw new CustomException("璺宠浆鐩爣涓嶈兘涓虹┖");
        }

        Long parsedTargetId;
        try {
            parsedTargetId = Long.valueOf(targetId);
        } catch (NumberFormatException ex) {
            throw new CustomException("璺宠浆鐩爣ID鏍煎紡涓嶆纭?");
        }

        return switch (targetType) {
            case 1 -> buildSongTargetOption(songService.getSongWithDetailBySongId(parsedTargetId));
            case 2 -> buildAlbumTargetOption(albumMapper.selectAlbumWithAuthorById(parsedTargetId));
            case 3 -> buildPlaylistTargetOption(playlistService.getPlaylistWithUser(parsedTargetId));
            default -> throw new CustomException("涓嶆敮鎸佺殑璺宠浆绫诲瀷");
        };
    }

    @SuppressWarnings("unchecked")
    private List<BannerTargetOptionVO> mapSongTargets(PageResult pageResult) {
        return ((List<SongBaseVO>) pageResult.getRecords()).stream()
                .map(song -> BannerTargetOptionVO.builder()
                        .targetId(song.getSongId())
                        .title(song.getSongName())
                        .subtitle(song.getArtistNames())
                        .coverUrl(song.getCoverUrl())
                        .build())
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<BannerTargetOptionVO> mapAlbumTargets(PageResult pageResult) {
        return ((List<Album>) pageResult.getRecords()).stream()
                .map(album -> BannerTargetOptionVO.builder()
                        .targetId(album.getAlbumId())
                        .title(album.getAlbumName())
                        .subtitle(album.getDescription())
                        .coverUrl(album.getCoverUrl())
                        .build())
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<BannerTargetOptionVO> mapPlaylistTargets(PageResult pageResult) {
        return ((List<Playlist>) pageResult.getRecords()).stream()
                .map(playlist -> BannerTargetOptionVO.builder()
                        .targetId(playlist.getPlaylistId())
                        .title(playlist.getPlaylistName())
                        .subtitle(playlist.getDescription())
                        .coverUrl(playlist.getCoverUrl())
                        .build())
                .toList();
    }

    private BannerTargetOptionVO buildSongTargetOption(SongWithDetail song) {
        if (song == null) {
            throw new CustomException("鏈壘鍒版寚瀹氭瓕鏇?");
        }
        return BannerTargetOptionVO.builder()
                .targetId(song.getSongId())
                .title(song.getSongName())
                .subtitle(song.getArtistNames())
                .coverUrl(song.getCoverUrl())
                .build();
    }

    private BannerTargetOptionVO buildAlbumTargetOption(AlbumWithAuthor album) {
        if (album == null) {
            throw new CustomException("鏈壘鍒版寚瀹氫笓杈?");
        }
        return BannerTargetOptionVO.builder()
                .targetId(album.getAlbumId())
                .title(album.getAlbumName())
                .subtitle(album.getArtistNames())
                .coverUrl(album.getCoverUrl())
                .build();
    }

    private BannerTargetOptionVO buildPlaylistTargetOption(PlaylistWithUser playlist) {
        if (playlist == null) {
            throw new CustomException("鏈壘鍒版寚瀹氭瓕鍗?");
        }
        return BannerTargetOptionVO.builder()
                .targetId(playlist.getPlaylistId())
                .title(playlist.getPlaylistName())
                .subtitle(playlist.getUserName())
                .coverUrl(playlist.getCoverUrl())
                .build();
    }
}
