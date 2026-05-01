package com.jiangce.yueting.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistIdentity {
    private Long artistId;
    private Integer identityTypeId;
}
