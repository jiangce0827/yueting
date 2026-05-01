package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistBaseVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long artistId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId; // 关联的用户ID
    private String artistName;
    private String artistAvatarUrl;
    private String artistCoverUrl;
    private String artistBio; // 艺人简介
    private String artistDescription; // 艺人详情
}
