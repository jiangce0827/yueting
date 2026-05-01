package com.jiangce.yueting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistPageDTO {
    private String artistName;
    private String idCard;
    private String realName;
    private Integer language;
    private Integer gender;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
