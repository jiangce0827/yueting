package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户播放历史VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPlayHistoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 播放记录ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recordId;

    /**
     * 歌曲ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long songId;

    /**
     * 歌曲名称
     */
    private String songName;

    /**
     * 歌曲封面
     */
    private String coverUrl;

    /**
     * 歌手名称（多个用/分隔）
     */
    private String artistNames;

    /**
     * 歌手ID（多个用,分隔）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String artistIds;

    /**
     * 播放时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playTime;
}