package com.jiangce.yueting.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoginVO {
    private String employeeId;
    private String realName;
    private Integer isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private String empToken;
    private List<SysMenuTreeVO> menus;
}
