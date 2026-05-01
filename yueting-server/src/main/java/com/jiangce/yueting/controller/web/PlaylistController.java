package com.jiangce.yueting.controller.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangce.yueting.domain.dto.PlaylistUpdateDTO;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.PlaylistWithSongs;
import com.jiangce.yueting.domain.vo.PlaylistWithUser;
import com.jiangce.yueting.mapper.PlaylistMapper;
import com.jiangce.yueting.service.PlaylistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/web/playlist")
@Tag(name = "歌单相关接口")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/me/create")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户创建歌单")
    public Result<String> create(@RequestParam("playlistName") String playlistName) {
        playlistService.create(playlistName);
        return Result.success();
    }

    @PutMapping("/me/{id}/cover")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户更新歌单封面")
    public Result<String> updateCover(@PathVariable("id") Long id, @RequestParam("coverUrl") String coverUrl) {
        playlistService.updateCover(id, coverUrl);
        return Result.success();
    }


    @GetMapping("/create/{userId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户获取创建的歌单")
    public Result<List<Playlist>> getCreatePlaylist(@PathVariable Long userId) {
        log.info("获取创建的歌单：{}", userId);
        return Result.success(playlistService.getCreatePlaylist(userId));
    }

    @GetMapping("/collect/{userId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户获取收藏的歌单")
    public Result<List<Playlist>> getCollectPlaylistByUserId(@PathVariable Long userId) {
        return Result.success(playlistService.getCollectPlaylist(userId));
    }

    @GetMapping("/me/{id}/withSongs")
    @PreAuthorize("permitAll()")
    @Operation(summary = "用户获取指定歌单及歌曲信息")
    public Result<PlaylistWithSongs> getPlaylistWithSongs(@PathVariable("id") Long id) {
        log.info("获取歌单及歌曲信息：{}", id);
        return Result.success(playlistService.getPlaylistWithSongs(id));
    }

    @GetMapping("/me/{id}/withUser")
    @PreAuthorize("permitAll()")
    @Operation(summary = "用户获取指定歌单及用户信息")
    public Result<PlaylistWithUser> getPlaylistWithUser(@PathVariable("id") Long id) {
        log.info("获取歌单及用户信息：{}", id);
        return Result.success(playlistService.getPlaylistWithUser(id));
    }

    @PostMapping("/me/{playlistId}/add/{songId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户添加歌曲到歌单")
    public Result<String> addSongToPlaylist(@PathVariable("playlistId") Long playlistId, @PathVariable("songId") Long songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return Result.success();
    }

    @PostMapping("/me/{playlistId}/delete/{songId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户歌单删除歌曲")
    public Result<String> deleteFormPlaylist(@PathVariable("playlistId") Long playlistId, @PathVariable("songId") Long songId) {
        playlistService.deleteFormPlaylist(playlistId, songId);
        return Result.success();
    }

    @PutMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户更新歌单信息")
    public Result<String> updatePlaylist(@PathVariable("id") Long id, @RequestBody PlaylistUpdateDTO playlistUpdateDTO) {
        playlistUpdateDTO.setPlaylistId(id);
        playlistService.updateById(playlistUpdateDTO);
        return Result.success();
    }

    @PostMapping("/me/{id}/collect")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户收藏歌单")
    public Result<String> collectPlaylist(@PathVariable("id") Long id) {
        playlistService.collectPlaylist(id);
        return Result.success();
    }

    @DeleteMapping("/me/{id}/collect")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户取消收藏歌单")
    public Result<String> cancelCollectPlaylist(@PathVariable("id") Long id) {
        playlistService.cancelCollectPlaylist(id);
        return Result.success();
    }

    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户删除歌单")
    public Result<String> deletePlaylist(@PathVariable("id") Long id) {
        playlistService.deletePlaylist(id);
        return Result.success();
    }

    @GetMapping("/toplist")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取系统榜单列表")
    public Result<List<Playlist>> getSystemTopLists() {
        return Result.success(playlistService.getSystemTopLists());
    }

    @GetMapping("/hot/playCount")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取最热歌单")
    public Result<List<Playlist>> getHotPlaylist() {
        return Result.success(playlistService.getHotPlaylistsOrderByPlayCount());
    }

    @GetMapping("/search/tag")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据标签搜索歌单")
    public Result<PageResult> searchPlaylistByTag(@RequestParam(value = "tagId", required = false) Integer tagId,
                                                  @RequestParam("pageNum") Integer pageNum,
                                                  @RequestParam("pageSize") Integer pageSize) {
        log.info("根据标签搜索歌单：{},{},{}", tagId, pageNum, pageSize);
        return Result.success(playlistService.getPlaylistByTag(tagId, pageNum, pageSize));
    }

    @GetMapping("/search/keyword")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据关键字搜索歌单")
    public Result<PageResult> searchPlaylistByName(@RequestParam("keyword") String keyword, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        log.info("根据歌单名搜索歌单：{}", keyword);
        return Result.success(playlistService.getPlaylistByName(keyword, pageNum, pageSize));
    }

}
