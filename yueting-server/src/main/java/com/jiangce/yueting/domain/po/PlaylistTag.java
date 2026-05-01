package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTag {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId
    private Long playlistId;
    private Integer tagId;
}
