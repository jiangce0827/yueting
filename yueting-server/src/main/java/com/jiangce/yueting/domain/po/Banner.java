package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 轮播图实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @TableId(type = IdType.AUTO)
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
    /**
     * 排序顺序（数字越小越靠前）
     */
    private Integer sortOrder;
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
