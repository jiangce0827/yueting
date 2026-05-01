package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔记表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("note")
public class Note {
    @TableId(type = IdType.AUTO)
    private Long id; // 笔记ID

    private Long userId; // 发布用户ID

    private String content; // 笔记内容

    private Long musicId; // 配乐ID

    private Integer musicType; // 配乐类型：1-单曲，2-歌手，3-专辑，4-歌单

    private String coverUrl; // 封面图片URL

    private Integer likeCount; // 点赞数

    private Integer commentCount; // 评论数

    private Integer forwardCount; // 转发数

    private Long forwardSourceId; // 转发的源笔记ID

    private Boolean isForwarded; // 是否转发生成的笔记

    private Integer status; // 状态：0-正常，1-禁用

    private LocalDateTime createdAt; // 创建时间
}