package com.jiangce.yueting.service.impl;

import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.common.ImageStorageProperties;
import com.jiangce.yueting.service.CommonService;
import com.jiangce.yueting.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    private final AliOssUtil aliOssUtil;
    private final ImageStorageProperties imageStorageProperties;

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("请选择要上传的图片");
        }

        String objectName = generateImageObjectName(file);
        if (imageStorageProperties.isLocal()) {
            return uploadImageToLocal(file, objectName);
        }
        return uploadImageToOss(file, objectName);
    }

    private String uploadImageToOss(MultipartFile file, String objectName) {
        try {
            return aliOssUtil.upload(file.getBytes(), objectName);
        } catch (IOException e) {
            log.error("上传图片到OSS失败", e);
            throw new CustomException("上传失败，请稍后再试");
        }
    }

    private String uploadImageToLocal(MultipartFile file, String objectName) {
        if (!StringUtils.hasText(imageStorageProperties.getLocalPath())) {
            throw new CustomException("本地图片存储路径未配置");
        }

        try {
            Path storageDir = Paths.get(imageStorageProperties.getLocalPath()).toAbsolutePath().normalize();
            Files.createDirectories(storageDir);

            Path savePath = storageDir.resolve(objectName).normalize();
            if (!savePath.startsWith(storageDir)) {
                throw new CustomException("无效的图片文件名");
            }

            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
            return buildLocalImageUrl(objectName);
        } catch (IOException e) {
            log.error("上传图片到本地失败", e);
            throw new CustomException("上传失败，请稍后再试");
        }
    }

    private String buildLocalImageUrl(String objectName) {
        String prefix = imageStorageProperties.getLocalUrlPrefix();
        if (!StringUtils.hasText(prefix)) {
            prefix = "/api/web/common/image";
        }
        return prefix.replaceAll("/$", "") + "/" + objectName;
    }

    private String generateImageObjectName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new CustomException("无效的图片文件名");
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            throw new CustomException("图片文件缺少扩展名");
        }

        extension = "." + extension.toLowerCase();
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw new CustomException("仅支持 jpg、jpeg、png、gif、webp、bmp 格式图片");
        }

        return UUID.randomUUID() + extension;
    }
}
