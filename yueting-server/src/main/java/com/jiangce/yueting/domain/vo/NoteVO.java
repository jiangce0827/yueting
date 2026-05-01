package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 笔记VO
 */
@Data
public class NoteVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 笔记ID
     */
    private Long noteId;

    /**
     * 发布用户ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
     * 发布用户昵称
     */
    private String nickname;

    /**
     * 发布用户头像
     */
    private String avatarUrl;

    /**
     * 笔记内容
     */
    private String content;

    /**
     * 配乐ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long musicId;

    /**
     * 配乐类型：1-单曲，2-歌手，3-专辑，4-歌单
     */
    private Integer musicType;

    /**
     * 配乐名称
     */
    private String musicName;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 图片URL列表
     */
    private java.util.List<String> imageUrls;

    /**
     * 歌曲封面URL
     */
    private String songCoverUrl;

    /**
     * 歌曲名称（当musicType=1时）
     */
    private String songName;

    /**
     * 歌手ID（当musicType=1或2时）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long artistId;

    /**
     * 歌手名称（当musicType=1或2时）
     */
    private String artistName;

    /**
     * 歌手头像（当musicType=2时）
     */
    private String artistAvatarUrl;

    /**
     * 歌手粉丝数（当musicType=2时）
     */
    private Integer artistFollowerCount;

    /**
     * 专辑ID（当musicType=1或3时）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long albumId;

    /**
     * 专辑名称（当musicType=1或3时）
     */
    private String albumName;

    /**
     * 专辑封面（当musicType=3时）
     */
    private String albumCoverUrl;

    /**
     * 专辑作者（当musicType=3时）
     */
    private String albumAuthorName;

    /**
     * 歌单ID（当musicType=4时）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long playlistId;

    /**
     * 歌单名称（当musicType=4时）
     */
    private String playlistName;

    /**
     * 歌单封面（当musicType=4时）
     */
    private String playlistCoverUrl;

    /**
     * 歌单创建者（当musicType=4时）
     */
    private String playlistCreatorName;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 转发的源笔记ID（不为空表示是转发生成的笔记）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long forwardSourceId;

    /**
     * 是否是转发生成的笔记
     */
    private Boolean isForwarded;

    /**
     * 源笔记是否已被删除
     */
    private Boolean sourceNoteDeleted;

    /**
     * 源笔记详情（当 forwardSourceId 不为空时填充）
     */
    private NoteVO sourceNote;

    /**
     * 是否已点赞
     */
    private Boolean liked;

    /**
     * 是否已转发
     */
    private Boolean forwarded;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 评论列表
     */
    private java.util.List<com.jiangce.yueting.domain.vo.NoteCommentVO> comments;
}