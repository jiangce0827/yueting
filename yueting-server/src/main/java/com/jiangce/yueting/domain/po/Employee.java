package com.jiangce.yueting.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @TableId(type = IdType.AUTO)
    private Long employeeId;
    private String username;
    private String password;
    private String realName;
    private Integer isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
