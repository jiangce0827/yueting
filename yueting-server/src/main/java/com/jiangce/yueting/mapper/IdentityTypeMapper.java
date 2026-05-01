package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.IdentityType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 身份类型 Mapper
 */
@Mapper
public interface IdentityTypeMapper extends BaseMapper<IdentityType> {

    /**
     * 根据身份类型ID查询身份名称
     *
     * @param identityTypeId 身份类型ID
     * @return 身份名称
     */
    default String getNameById(Integer identityTypeId) {
        IdentityType result = selectOne(new LambdaQueryWrapper<IdentityType>()
                .eq(IdentityType::getIdentityTypeId, identityTypeId));
        return result != null ? result.getName() : null;
    }
}
