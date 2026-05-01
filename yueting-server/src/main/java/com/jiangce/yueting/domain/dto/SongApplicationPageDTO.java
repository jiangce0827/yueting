package com.jiangce.yueting.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SongApplicationPageDTO {
    @Schema(description = "艺人ID")
    private Long artistId;

    @Schema(description = "歌曲名")
    private String songName;

    @Schema(description = "审核状态")
    private Integer status;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "每页数量")
    private Integer pageSize;
}
