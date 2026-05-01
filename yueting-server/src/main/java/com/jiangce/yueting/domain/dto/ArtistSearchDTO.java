package com.jiangce.yueting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSearchDTO {
    private String keyword;
    private Integer gender;
    private Integer language;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
