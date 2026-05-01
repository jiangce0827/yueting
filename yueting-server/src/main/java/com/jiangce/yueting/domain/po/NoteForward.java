package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔记转发表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("note_forward")
public class NoteForward {
    @TableId(type = IdType.AUTO)
    private Long id; // 转发ID

    private Long noteId; // 原笔记ID

    private Long userId; // 转发用户ID

    private LocalDateTime createdAt; // 转发时间
}