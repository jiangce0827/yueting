package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.AlbumArtist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专辑-艺人关联 Mapper
 */
@Mapper
public interface AlbumArtistMapper extends BaseMapper<AlbumArtist> {
}
