package com.jiangce.yueting.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistInfoVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long artistId; // 艺人id
    private String artistName; // 艺人名
    private String artistAvatarUrl; // 艺人头像
    private String artistCoverUrl; // 艺人封面
    private Integer gender; // 艺人性别
    private Integer language; // 艺人语言
    private String artistBio; // 艺人简介
    private String artistDescription; // 艺人详情
    private List<String> identities; // 艺人身份
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer status;
}

