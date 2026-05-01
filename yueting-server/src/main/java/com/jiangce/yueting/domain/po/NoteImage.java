package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔记图片表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("note_image")
public class NoteImage {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private Long noteId; // 笔记ID

    private String imageUrl; // 图片URL

    private Integer sortOrder; // 排序顺序

    private LocalDateTime createdAt; // 创建时间
}