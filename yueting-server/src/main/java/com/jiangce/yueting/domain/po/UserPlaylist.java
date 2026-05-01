package com.jiangce.yueting.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPlaylist {
    private Long userId;
    private Long playlistId;
    private Integer relationType;
    private LocalDateTime createdAt;
}
