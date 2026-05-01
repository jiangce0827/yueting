package com.jiangce.yueting.domain.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistIdentityApplicationVO {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long artistIdentityApplicationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long artistId;
    private String  artistName;
    private Integer identityType;
    private Integer fileType;
    private String fileName;
    private String fileUrl;
    private String fileContent;
    private String introduction;
    private String rejectionReason;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long reviewedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedAt;
    private Integer status;
}
