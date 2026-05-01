package com.jiangce.yueting.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.po.SongApplication;
import com.jiangce.yueting.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongUploadDTO {
    private Boolean isNewAlbum; // 是否新专辑
    private Long albumId;
    private String albumName; // 专辑名称
    private String albumCoverUrl; // 专辑封面
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate; // 发布日期
    private String description; // 描述
    private List<SongInfo> songs;

    public Album getAlbum() {
        Album album = new Album();
        album.setAlbumName(albumName);
        album.setCoverUrl(albumCoverUrl);
        album.setReleaseDate(releaseDate);
        album.setDescription(description);
        return album;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static
    class SongInfo {
        private Integer songType;
        private String songName;
        private Integer language;
        private Integer duration; // 时长
        private List<Integer> tagIds;
        private List<Long> singerIds;
        private List<Long> lyricistIds;
        private List<Long> composerIds;
        private String songUrl;
        private String lyrics;

        public SongApplication getSongApplication() {
            SongApplication songApplication = new SongApplication();
            songApplication.setSongType(songType);
            songApplication.setSongName(songName);
            songApplication.setLanguage(language);
            songApplication.setDuration(duration);
            songApplication.setTagIds(StringUtils.listToString(tagIds));
            songApplication.setSingerIds(StringUtils.listToString(singerIds));
            songApplication.setLyricistIds(StringUtils.listToString(lyricistIds));
            songApplication.setComposerIds(StringUtils.listToString(composerIds));
            songApplication.setSongUrl(songUrl);
            songApplication.setLyrics(lyrics);
            return songApplication;
        }
    }
}
