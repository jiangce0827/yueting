package com.jiangce.yueting.domain.dto;

import com.jiangce.yueting.domain.po.Artist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistUpdateDTO {
    private String artistName; // 艺人名
    private String artistAvatarUrl; // 艺人头像
    private String artistCoverUrl; // 艺人页头图
    private Integer gender; // 性别
    private Integer language; // 语言
    private String artistBio; // 艺人简介
    private String artistDescription; // 艺人描述

    public Artist toArtist() {
        return Artist.builder()

                .artistName(artistName)
                .artistAvatarUrl(artistAvatarUrl)
                .artistCoverUrl(artistCoverUrl)
                .gender(gender)
                .language(language)
                .artistBio(artistBio)
                .artistDescription(artistDescription)
                .build();
    }
}