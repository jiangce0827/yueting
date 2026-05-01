package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPlayRankingVO implements Serializable {
    private Integer rank; // 排名

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long songId; // 歌曲ID

    private String songName; // 歌曲名
    private String coverUrl; // 歌曲封面
    private String artistNames; // 歌手名（多个用/分隔）

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String artistIds; // 歌手ID（多个用,分隔）

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long playCount; // 播放次数
}
