package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiangce.yueting.domain.po.Playlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistWithSongs {
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
    private List<SongBaseVO> songs;

    public void setPlaylist(PlaylistWithUser playlist) {
        this.playlistId = playlist.getPlaylistId();
        this.playlistName = playlist.getPlaylistName();
        this.tagIds = playlist.getTagIds();
        this.tagNames = playlist.getTagNames();
        this.userId = playlist.getUserId();
        this.userName = playlist.getUserName();
        this.coverUrl = playlist.getCoverUrl();
        this.description = playlist.getDescription();
        this.createdAt = playlist.getCreatedAt();
    }
}
