package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.SongUploadDTO;
import com.jiangce.yueting.domain.dto.SongApplicationPageDTO;
import com.jiangce.yueting.domain.po.Song;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.SongWithDetail;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.SongBaseVO;
import com.jiangce.yueting.mapper.SongMapper;
import com.jiangce.yueting.service.SongApplicationService;
import com.jiangce.yueting.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/song")
@Slf4j
@Tag(name = "歌曲相关接口")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;
    private final SongApplicationService songApplicationService;

    @PostMapping("/me/upload")
    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "上传歌曲")
    public Result<String> uploadSong(@RequestBody SongUploadDTO songUploadDTO) {
        log.info("上传歌曲：{}", songUploadDTO);
        songService.uploadSong(songUploadDTO);
        return Result.success("成功");
    }

    @GetMapping("/hot/{artistId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取艺人热门歌曲")
    public Result<List<SongBaseVO>> getHotSongsByArtistId(@PathVariable Long artistId) {
        log.info("获取艺人热门歌曲：{}", artistId);
        return Result.success(songService.getHotSongsByArtistId(artistId));
    }

    @DeleteMapping("/me/{songId}")
    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "删除歌曲")
    public Result<String> removeMusic(@PathVariable Long songId) {
        log.info("删除歌曲：{}", songId);
        songService.removeMusic(songId);
        return Result.success("成功");
    }

    @GetMapping("/me/applications")
    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "获取我的歌曲审核记录")
    public Result<PageResult> pageMySongApplications(SongApplicationPageDTO songApplicationPageDTO) {
        log.info("获取我的歌曲审核记录: {}", songApplicationPageDTO);
        return Result.success(songApplicationService.pageMySongApplication(songApplicationPageDTO));
    }

    @GetMapping("/play/{songId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "播放歌曲")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Resource> playMusic(@PathVariable Long songId) {
        log.info("播放歌曲：{}", songId);
        return songService.playMusic(songId);
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取歌曲信息")
    public Result<SongWithDetail> getSongWithDetail(@PathVariable Long id) {
        log.info("获取歌曲详细信息：{}", id);
        return Result.success(songService.getSongWithDetailBySongId(id));
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    @Operation(summary = "搜索歌曲")
    public Result<PageResult> searchSong(@RequestParam("keyword") String keyword, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        log.info("搜索歌曲：{},{},{}", keyword, pageNum, pageSize);
        return Result.success(songService.searchSong(keyword, pageNum, pageSize));
    }

    @GetMapping("/search/lyrics")
    @PreAuthorize("permitAll()")
    @Operation(summary = "搜索歌词")
    public Result<PageResult> searchLyrics(@RequestParam String keyword, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        log.info("搜索歌词：{},{},{}", keyword, pageNum, pageSize);
        return Result.success(songService.searchSongsByLyrics(keyword, pageNum, pageSize));
    }
    @PreAuthorize("permitAll()")
    @PostMapping("/increment-play/{id}")
    @Operation(summary = "用户播放歌曲次数加1")
    public Result<String> incrementPlay(@PathVariable Long id) {
        log.info("歌曲播放次数加1：{}", id);
        // 1.歌曲播放数 +1
        songService.incrementPlay(id);
        // 2.记录用户播放历史（如果已登录）
        Long userId = BaseContext.getCurrentId();
        if (userId != null) {
            songService.recordUserPlay(userId, id);
        }
        return Result.success("成功");
    }

}
