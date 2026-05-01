package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.ImageStorageProperties;
import com.jiangce.yueting.common.SongContent;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.service.CommonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/web/common")
@Tag(name = "通用接口")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;
    private final SongContent songContent;
    private final ImageStorageProperties imageStorageProperties;

    @PreAuthorize("permitAll()")
    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<String> uploadImage(MultipartFile image) {
        log.info("上传图片");
        String avatarUrl = commonService.uploadImage(image);
        return Result.success(avatarUrl);
    }

    @Operation(summary = "获取本地图片")
    @GetMapping("/image/{filename}")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        if (!StringUtils.hasText(imageStorageProperties.getLocalPath())) {
            return ResponseEntity.notFound().build();
        }

        Path storageDir = Paths.get(imageStorageProperties.getLocalPath()).toAbsolutePath().normalize();
        Path filePath = storageDir.resolve(filename).normalize();
        if (!filePath.startsWith(storageDir)) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(resolveImageMediaType(filename))
                .header("Access-Control-Allow-Origin", "*")
                .body(resource);
    }

    @PostMapping("/upload/musicFile")
    @PreAuthorize("permitAll()")
    @Operation(summary = "上传音乐文件")
    public Result<String> localUpload(MultipartFile file) {
        // 校验文件是否为空
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        // 校验文件类型（可选扩展）
        String originalFilename = file.getOriginalFilename();
        // 配置保存目录
        String saveDir = songContent.storagePath + "/";
        File directory = new File(saveDir);

        try {
            // 创建保存目录（如果不存在）
            if (!directory.exists() && !directory.mkdirs()) {
                log.error("目录创建失败：{}", saveDir);
                return Result.error("服务器存储目录创建失败");
            }

            // 校验文件类型（可选扩展）
            if (!StringUtils.hasText(originalFilename)) {
                return Result.error("无效的文件名");
            }

            // 提取扩展名（安全方式）
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            // 生成唯一文件名
            String objectName = UUID.randomUUID() + extension;
            Path savePath = Paths.get(saveDir, objectName);

            // 保存文件到本地
            Files.copy(file.getInputStream(), savePath);

            log.info("文件上传成功：{} -> {}", originalFilename, savePath);
            return Result.success(objectName);
        } catch (IOException e) {
            log.error("文件上传失败：{}，错误信息：{}", originalFilename, e.getMessage());
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("系统异常：", e);
            return Result.error("系统处理异常");
        }
    }


    @Operation(summary = "获取文件")
    @GetMapping("/file/{filename}")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Resource> getMusicFile(@PathVariable String filename) throws MalformedURLException {

        log.info("filename: " + filename);
        // 构建文件路径
        Path filePath = Paths.get(songContent.storagePath + "/", filename).normalize(); // 使用normalize()避免路径遍历攻击

        // 创建UrlResource对象
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            log.info("文件存在或可读：" + filename);
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

    private MediaType resolveImageMediaType(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(extension)) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            case "bmp" -> MediaType.parseMediaType("image/bmp");
            default -> MediaType.asMediaType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
        };
    }

}
