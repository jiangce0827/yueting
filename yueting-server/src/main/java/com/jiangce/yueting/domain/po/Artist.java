package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jiangce.yueting.domain.vo.ArtistBaseVO;
import com.jiangce.yueting.domain.vo.ArtistInfoVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {
    @TableId(type = IdType.AUTO)
    private Long artistId; // 艺人id
    private Long userId; // 用户id
    private String artistName; // 艺人名
    private String artistAvatarUrl; // 艺人头像
    private String artistCoverUrl; // 艺人封面
    private Integer gender; // 艺人性别
    private Integer language; // 艺人语言
    private String artistBio; // 艺人简介
    private String artistDescription; // 艺人详情
    private Long hot;
    private String realName;
    private String idCard;
    private LocalDateTime createdAt;
    private Integer status;
    private LocalDateTime statusExpiredAt;

    public ArtistBaseVO toArtistBaseVO() {
        return new ArtistBaseVO(artistId, userId, artistName, artistAvatarUrl, artistCoverUrl, artistBio, artistDescription);
    }
}
