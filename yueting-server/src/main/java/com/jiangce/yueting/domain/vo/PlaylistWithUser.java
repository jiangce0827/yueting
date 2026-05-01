package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PlaylistWithUser  implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long playlistId;
    private String playlistName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId; // 作者id
    private String userName; // 作者名称
    private String tagIds;
    private String tagNames;
    private String coverUrl;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
