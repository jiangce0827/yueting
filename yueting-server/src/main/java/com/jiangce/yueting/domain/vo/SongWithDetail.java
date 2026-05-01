package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongWithDetail {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long songId; // 歌曲id
    private String songName; // 歌曲名称
    private String artistIds; // 歌手id
    private String artistNames; // 歌手名称
    private Integer duration; // 时长
    private String coverUrl; // 封面
    private String lyrics;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long albumId; // 专辑id
    private String albumName; // 专辑名称
    private String composerIds; // 新增作曲id（role_type=2）
    private String composerNames; // 新增作曲人（role_type=2）
    private String lyricistIds; //  作词人id
    private String lyricistNames; // 新增作词人（role_type=1）
    private Long likeCount;
    private Long playCount; //播放次数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt; // 发布日期
    private Integer status;
}
