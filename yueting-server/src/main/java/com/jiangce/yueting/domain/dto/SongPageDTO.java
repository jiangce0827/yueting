package com.jiangce.yueting.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongPageDTO {
    private String songName; // 音乐名
    private String artistName; // 歌手名
    private Integer status; // 状态
    private Integer vip; // 是否vip
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNum;
    @Schema(description = "每页数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;
}
