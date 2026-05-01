package com.jiangce.yueting.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongPlaylist {
    private Long playlistId;
    private Long songId;
    private LocalDateTime addedAt;
}
