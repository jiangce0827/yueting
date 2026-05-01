package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jiangce.yueting.common.SongContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongApplication {
    @TableId(type = IdType.AUTO)
    private Long applicationId;
    private Long artistId; // 申请艺人id
    private Integer songType; //0：原唱，1：翻唱
    private String songName; // 歌曲名称
    private String songUrl; // 音频地址
    private Integer language; // 语言
    private String tagIds; // 标签id
    private String singerIds; // 歌手id
    private String lyricistIds; // 作词id
    private String composerIds; // 作曲id
    private Integer duration; // 时长
    private String lyrics; // 歌词
    private Long albumId; // 专辑id
    private Long reviewedBy; // 审核人id
    private String rejectionReason; //  拒绝理由
    private LocalDateTime appliedAt; // 申请时间
    private LocalDateTime reviewedAt; // 审核时间
    private Integer status;

    public Song toSong() {
        return new Song(null, songName, songUrl, songType, duration, lyrics, albumId, 0L, 0L, LocalDateTime.now(), SongContent.STATUS_USABLE);
    }
}
