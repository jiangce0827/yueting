package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播图响应VO（用于C端展示）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long bannerId;
    private String title;
    private String imageUrl;
    /**
     * 跳转类型：0-无跳转，1-歌曲，2-专辑，3-歌单，4-外部链接
     */
    private Integer targetType;
    /**
     * 跳转目标ID（歌曲/专辑/歌单ID）
     */
    private String targetId;
    /**
     * 外部链接地址
     */
    private String targetUrl;
}