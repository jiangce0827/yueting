package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongBaseVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long songId; // 歌曲id
    private String songName; // 歌曲名称
    private String artistIds; // 歌手id
    private String artistNames; // 歌手名称
    private Integer duration; // 时长
    private String coverUrl; // 封面
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long albumId; // 专辑id
    private String albumName; // 专辑名称
    private Long playCount; //播放次数
    private LocalDateTime createAt; // 发布日期
    private Integer status;
}
