package com.jiangce.yueting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistApplicationIdentityPageDTO {
    private String artistName;
    private Integer identityType;
    private Integer status;
    private Integer pageNum;
    private Integer pageSize;
}
