package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("song_daily_play_count")
public class SongDailyPlayCount {
    private Long songId;
    private LocalDate playDate;
    private Integer playCount;
}
