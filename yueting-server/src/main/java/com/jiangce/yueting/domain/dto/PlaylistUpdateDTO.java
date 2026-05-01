package com.jiangce.yueting.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiangce.yueting.domain.po.Playlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistUpdateDTO {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long playlistId;
    private Long userId;
    private String playlistName;
    private List<Integer> tagIds;
    private String coverUrl;
    private String description;
    @TableField("is_public")
    private Boolean isPublic;
    @TableField("is_default")
    private Boolean isDefault;
    private Integer songCount;
    private Long playCount;
    private Integer collectCount;

    public Playlist toPlaylist() {
        return Playlist.builder()
                .playlistId(playlistId)
                .userId(userId)
                .playlistName(playlistName)
                .coverUrl(coverUrl)
                .description(description)
                .isPublic(isPublic)
                .isDefault(isDefault)
                .songCount(songCount)
                .playCount(playCount)
                .collectCount(collectCount)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
