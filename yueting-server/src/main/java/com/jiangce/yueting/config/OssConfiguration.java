package com.jiangce.yueting.config;

import com.jiangce.yueting.common.AliOssProperties;
import com.jiangce.yueting.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置oss
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean //只创建一个对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        // 通过AliIssProperties获取oss配置，并创建AliOssUtil对象
        return new AliOssUtil(aliOssProperties.getEndpoint(),aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),aliOssProperties.getBucketName());
    }
}