package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @TableId(type = IdType.AUTO)
    private Long songId; // 歌曲id
    private String songName; // 歌曲名称
    private String songUrl; // 音频地址
    private Integer songType; //0：原唱，1：翻唱
    private Integer duration; // 时长
    private String lyrics; // 歌词
    private Long albumId; // 专辑id
    private Long playCount; //播放次数
    private Long likeCount; //喜欢次数
    private LocalDateTime createAt; // 发布日期
    private Integer status;
}
