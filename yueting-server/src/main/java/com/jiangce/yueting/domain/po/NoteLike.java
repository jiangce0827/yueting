package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔记点赞表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("note_like")
public class NoteLike {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键

    private Long noteId; // 笔记ID

    private Long userId; // 点赞用户ID

    private LocalDateTime createdAt; // 点赞时间
}