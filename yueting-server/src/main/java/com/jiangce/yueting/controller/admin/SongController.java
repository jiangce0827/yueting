package com.jiangce.yueting.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.jiangce.yueting.common.SongContent;
import com.jiangce.yueting.domain.dto.SongApplicationPageDTO;
import com.jiangce.yueting.domain.dto.SongPageDTO;
import com.jiangce.yueting.domain.po.SongApplication;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.service.SongApplicationService;
import com.jiangce.yueting.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController("backendSongController")
@RequestMapping("/admin/song")
@Tag(name = "后台歌曲相关接口")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;
    private final SongApplicationService songApplicationService;
    private final SongContent songContent;


    @GetMapping("/file/{filename}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "播放歌曲")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Resource> getSongFile(@PathVariable String filename) throws MalformedURLException {
        log.info("filename: " + filename);
        // 构建文件路径
        Path filePath = Paths.get(songContent.storagePath + "/", filename).normalize(); // 使用normalize()避免路径遍历攻击

        // 创建UrlResource对象
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            log.info("文件存在或可读：" + filename);
            String redisKey = "music:hot:" + filename;
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .header("Access-Control-Allow-Origin", "*")
                    .body(resource);
        } else {
            // 如果文件不存在或不可读，返回404错误
            log.info("文件不存在或无法读取：" + filename);
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/pageSong")
    @Operation(summary = "分页查询音乐", description = "返回音乐列表")
    public Result<PageResult> pageMusic(SongPageDTO songPageDTO) {
        log.info("分页查询音乐: " + songPageDTO.toString());
        PageResult pageResult = songService.pageSongWithDetail(songPageDTO);
        return Result.success(pageResult);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    @Operation(summary = "通过歌曲上传申请")
    public Result<String> approveSong(@PathVariable Long id) {
        log.info("通过歌曲上传申请，id: {}", id);
        songApplicationService.approveSong(id);
        return Result.success("成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    @Operation(summary = "驳回歌曲上传申请")
    public Result<String> rejectSong(@PathVariable Long id, @RequestParam("rejectionReason") String rejectionReason) {
        log.info("驳回歌曲上传申请，id: {}, rejectionReason: {}", id, rejectionReason);
        songApplicationService.rejectSong(id, rejectionReason);
        return Result.success("成功");
    }

    @Operation(summary = "获取审批音乐列表")
    @GetMapping("/pageSongApplication")
    public Result<PageResult> pageSongApplication(SongApplicationPageDTO songApplicationPageDTO) {
        log.info("分页查询审批音乐: {}", songApplicationPageDTO.toString());
        PageResult pageResult = songApplicationService.pageSongApplication(songApplicationPageDTO);
        return Result.success(pageResult);
    }
}
