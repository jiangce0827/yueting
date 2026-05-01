package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistIdentityApplication {
    @TableId(type = IdType.AUTO)
    private Long artistIdentityApplicationId;
    private Long artistId;
    private Integer identityType;
    private Integer fileType;
    private String fileName;
    private String fileUrl;
    private String fileContent;
    private String introduction;
    private String rejectionReason;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime appliedAt;
    private Integer status;
}
