package com.jiangce.yueting.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistApplicationPageDTO {
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    private Integer applyType;
    private Integer pageNum;
    private Integer pageSize;
}
