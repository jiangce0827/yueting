package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_follow")
public class UserFollow {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键

    private Long followerId; // 关注者用户ID

    private Long followingId; // 被关注者用户ID

    private LocalDateTime createdAt; // 关注时间
}