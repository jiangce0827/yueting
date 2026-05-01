package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.AlbumWithSongs;
import com.jiangce.yueting.mapper.AlbumMapper;
import com.jiangce.yueting.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/album")
@Slf4j
@Tag(name = "专辑相关接口")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/me/albums")
    @Operation(summary = "获取我的专辑")
    public Result<List<Album>> getMyAlbums() {
        List<Album> albumList = albumService.getAllByUserId(BaseContext.getCurrentId());
        return Result.success(albumList);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/artist/{artistId}/albums")
    @Operation(summary = "获取艺人专辑")
    public Result<PageResult> getArtistAlbums(
            @PathVariable Long artistId,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize
    ) {
        log.info("获取艺人专辑：{}", artistId);
        PageResult pageResult = albumService.searchAlbumsByArtistId(artistId, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{albumId}/albumWithSongs")
    @Operation(summary = "获取专辑及歌曲详情")
    public Result<AlbumWithSongs> getAlbumWithSongs(@PathVariable Long albumId) {
        log.info("获取专辑及歌曲详情：{}", albumId);
        AlbumWithSongs albumWithSongs = albumService.getAlbumWithSongs(albumId);
        return Result.success(albumWithSongs);
    }

    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/me/{albumId}/albumWithSongs")
    @Operation(summary = "获取我的专辑及歌曲详情")
    public Result<AlbumWithSongs> getMyAlbumWithSongs(@PathVariable Long albumId) {
        log.info("获取我的专辑及歌曲详情：{}", albumId);
        return Result.success(albumService.getMyAlbumWithSongs(albumId));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{albumId}/basic")
    @Operation(summary = "获取专辑基本信息")
    public Result<Album> getAlbumBasic(@PathVariable Long albumId) {
        log.info("获取专辑基本信息：{}", albumId);
        Album album = albumMapper.selectById(albumId);
        return Result.success(album);
    }

    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/{albumId}/cover")
    @Operation(summary = "更新专辑封面")
    public Result<String> updateAlbumCover(@PathVariable Long albumId, @RequestParam("coverUrl") String coverUrl) {
        log.info("更新专辑封面：{}", albumId);
        Album album = new Album();
        album.setAlbumId(albumId);
        album.setCoverUrl(coverUrl);
        albumMapper.updateById(album);
        return Result.success("更新成功");
    }

    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/{albumId}/basic")
    @Operation(summary = "更新专辑基本信息")
    public Result<String> updateAlbumBasic(@PathVariable Long albumId, @RequestBody Album album) {
        log.info("更新专辑基本信息：{}", albumId);
        album.setAlbumId(albumId);
        albumMapper.updateById(album);
        return Result.success("更新成功");
    }

    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/{albumId}")
    @Operation(summary = "删除专辑")
    public Result<String> deleteAlbum(@PathVariable Long albumId) {
        log.info("删除专辑：{}", albumId);
        albumService.deleteAlbum(albumId);
        return Result.success("删除成功");
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    @Operation(summary = "搜索专辑")
    public Result<PageResult> searchAlbum(
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize
    ) {
        log.info("搜索专辑：{}", keyword);
        return Result.success(albumService.searchAlbumsByKeyword(keyword, pageNum, pageSize));
    }
}
