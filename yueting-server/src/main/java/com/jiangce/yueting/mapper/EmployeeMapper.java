package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
