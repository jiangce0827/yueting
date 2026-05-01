package com.jiangce.yueting.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建笔记DTO
 */
@Data
public class NoteCreateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 图片URL列表
     */
    private List<String> imageUrls;

    /**
     * 转发的源笔记ID（不为空表示是转发生成的笔记）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long forwardSourceId;
}