package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.EmployeeRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员角色关联 Mapper
 */
@Mapper
public interface EmployeeRoleMapper extends BaseMapper<EmployeeRole> {
}