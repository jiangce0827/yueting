package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {
    @TableId(type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long playlistId;
    private Long userId;
    private String playlistName;
    private String coverUrl;
    private String description;
    @TableField("is_public")
    private Boolean isPublic;
    @TableField("is_default")
    private Boolean isDefault;
    @TableField("is_system")
    private Boolean isSystem;
    private Integer songCount;
    private Long playCount;
    private Integer collectCount;
    private LocalDateTime createdAt;
}
