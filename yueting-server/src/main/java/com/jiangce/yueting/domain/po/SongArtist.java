package com.jiangce.yueting.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongArtist {
    private Long songId;
    private Long artistId;
    private Integer roleType;
}
