package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String nickname;

    private String avatarUrl;

    private String signature;

    private LocalDateTime createdAt;
}