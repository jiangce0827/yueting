package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员角色关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("employee_role")
public class EmployeeRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    private Long employeeId;

    /**
     * 角色ID
     */
    private Long roleId;
}