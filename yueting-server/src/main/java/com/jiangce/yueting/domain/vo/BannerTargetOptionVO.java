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
public class BannerTargetOptionVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long targetId;
    private String title;
    private String subtitle;
    private String coverUrl;
}
