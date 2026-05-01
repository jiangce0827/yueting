package com.jiangce.yueting.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yueting.image")
public class ImageStorageProperties {
    public static final String STORAGE_TYPE_OSS = "oss";
    public static final String STORAGE_TYPE_LOCAL = "local";

    private String storageType = STORAGE_TYPE_OSS;
    private String localPath;
    private String localUrlPrefix = "/api/web/common/image";

    public boolean isLocal() {
        return STORAGE_TYPE_LOCAL.equalsIgnoreCase(storageType);
    }
}
