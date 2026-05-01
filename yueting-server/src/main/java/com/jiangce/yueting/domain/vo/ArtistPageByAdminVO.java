package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistPageByAdminVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long artistId; // 艺人id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId; // 用户id
    private String userName;
    private String artistName; // 艺人名
    private String artistAvatarUrl; // 艺人头像
    private String artistCoverUrl; // 艺人封面
    private Integer gender; // 艺人性别
    private Integer language; // 艺人语言
    private String artistBio; // 艺人简介
    private String artistDescription; // 艺人详情
    private String realName;
    private String idCard;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer status;
    private List<String> identities; // 身份列表

}
