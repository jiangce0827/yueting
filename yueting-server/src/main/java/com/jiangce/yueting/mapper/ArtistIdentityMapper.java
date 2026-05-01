package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.ArtistIdentity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 艺人身份关联 Mapper
 */
@Mapper
public interface ArtistIdentityMapper extends BaseMapper<ArtistIdentity> {
}
