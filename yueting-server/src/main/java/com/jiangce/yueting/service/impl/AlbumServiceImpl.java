package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.po.AlbumArtist;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.AlbumWithAuthor;
import com.jiangce.yueting.domain.vo.AlbumWithSongs;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import com.jiangce.yueting.mapper.AlbumArtistMapper;
import com.jiangce.yueting.mapper.AlbumMapper;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.mapper.SongMapper;
import com.jiangce.yueting.service.AlbumService;
import com.jiangce.yueting.service.ArtistService;
import com.jiangce.yueting.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final ArtistService artistService;
    private final AlbumMapper albumMapper;
    private final AlbumArtistMapper albumArtistMapper;
    private final SongMapper songMapper;
    private final SongService songService;
    private final ArtistMapper artistMapper;

    @Override
    public List<Album> getAllByUserId(Long userId) {
        Long artistId = artistMapper.selectArtistByUserId(userId).getArtistId();
        return albumMapper.selectAlbumsByArtistId(artistId);
    }

    @Override
    @Transactional
    public void save(Album album, Long artistId) {
        albumMapper.insert(album);
        log.info("新增专辑: {}", album);
        albumArtistMapper.insert(new AlbumArtist(album.getAlbumId(), artistId));
    }

    @Override
    public PageResult searchAlbumsByArtistId(Long artistId, Integer pageNum, Integer pageSize) {
        Page<Album> page = new Page<>(pageNum, pageSize);
        IPage<Album> result = albumMapper.pageAlbumsByArtistId(artistId, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public AlbumWithSongs getAlbumWithSongs(Long albumId) {
        AlbumWithAuthor albumWithAuthor = albumMapper.selectAlbumWithAuthorById(albumId);
        List<SongBaseVO> songs = songMapper.selectSongsByAlbumId(albumId);
        songs.forEach(songBaseVO -> songBaseVO.setAlbumName(albumWithAuthor.getAlbumName()));
        return new AlbumWithSongs(albumWithAuthor.getAlbumId(), albumWithAuthor.getAlbumName(), albumWithAuthor.getArtistIds(), albumWithAuthor.getArtistNames(), albumWithAuthor.getCoverUrl(), albumWithAuthor.getDescription(), albumWithAuthor.getReleaseDate(), songs);
    }

    @Override
    public AlbumWithSongs getMyAlbumWithSongs(Long albumId) {
        Artist artist = artistService.getCurrentArtist();
        if (artist == null) {
            throw new CustomException("您不是音乐人，无法查看专辑详情");
        }
        LambdaQueryWrapper<AlbumArtist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumArtist::getAlbumId, albumId)
                .eq(AlbumArtist::getArtistId, artist.getArtistId());
        if (!albumArtistMapper.exists(wrapper)) {
            throw new CustomException("您无权查看该专辑");
        }
        AlbumWithAuthor albumWithAuthor = albumMapper.selectAlbumWithAuthorById(albumId);
        if (albumWithAuthor == null) {
            throw new CustomException("专辑不存在");
        }
        List<SongBaseVO> songs = songMapper.selectSongsByAlbumIdForArtist(albumId);
        songs.forEach(songBaseVO -> songBaseVO.setAlbumName(albumWithAuthor.getAlbumName()));
        return new AlbumWithSongs(
                albumWithAuthor.getAlbumId(),
                albumWithAuthor.getAlbumName(),
                albumWithAuthor.getArtistIds(),
                albumWithAuthor.getArtistNames(),
                albumWithAuthor.getCoverUrl(),
                albumWithAuthor.getDescription(),
                albumWithAuthor.getReleaseDate(),
                songs
        );
    }

    @Override
    @Transactional
    public void deleteAlbum(Long albumId) {
        Album album = albumMapper.selectById(albumId);
        if(album == null) {
            throw new CustomException("专辑不存在");
        }
        Artist artist = artistMapper.selectArtistByUserId(BaseContext.getCurrentId());
        if(artist == null) {
            throw new CustomException("您不是歌手，无法删除专辑");
        }
        LambdaQueryWrapper<AlbumArtist> albumArtistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        albumArtistLambdaQueryWrapper.eq(AlbumArtist::getAlbumId, albumId)
                .eq(AlbumArtist::getArtistId, artist.getArtistId());
        if (!albumArtistMapper.exists(albumArtistLambdaQueryWrapper)) {
            throw new CustomException("您不是专辑的创建者，无法删除专辑");
        }
        // 删除专辑内音乐
        songService.removeMusicByAlbumId(albumId);
        // 删除专辑艺人关联信息
        albumArtistLambdaQueryWrapper.clear();
        albumArtistLambdaQueryWrapper.eq(AlbumArtist::getAlbumId, albumId);
        albumArtistMapper.delete(albumArtistLambdaQueryWrapper);
        //删除专辑信息
        albumMapper.deleteById(albumId);
    }

    @Override
    public PageResult searchAlbumsByKeyword(String keyword, Integer pageNum, Integer pageSize) {
        Page<Album> page = new Page<>(pageNum, pageSize);
        IPage<Album> albumList = albumMapper.selectAlbumsByKeyword(keyword, page);
        return new PageResult(albumList.getTotal(), albumList.getRecords());
    }
}
