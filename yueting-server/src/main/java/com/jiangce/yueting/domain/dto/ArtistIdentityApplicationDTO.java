package com.jiangce.yueting.domain.dto;

import com.jiangce.yueting.common.ArtistApplicationContent;
import com.jiangce.yueting.domain.po.ArtistIdentityApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistIdentityApplicationDTO {
    private Integer identityType;
    private Integer fileType;
    private String fileName;
    private String fileUrl;
    private String fileContent;
    private String introduction;

    public ArtistIdentityApplication toArtistIdentityApplication() {
        return ArtistIdentityApplication.builder()
                .identityType(identityType)
                .fileType(fileType)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileContent(fileContent)
                .introduction(introduction)
                .appliedAt(LocalDateTime.now())
                .status(ArtistApplicationContent.STATUS_UNREVIEWED)
                .build();
    }
}
