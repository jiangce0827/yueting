package com.jiangce.yueting.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分页查询参数")
public class UserPageDTO {
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户手机号")
    private String phone;
    @Schema(description = "用户邮箱")
    private String email;
    @Schema(description = "用户性别: 0:女，1:男，2:保密")
    private Integer gender;
    @Schema(description = "用户状态: 0:禁用，1:启用")
    private Integer status;
    @Schema(description = "用户是否为vip: 0:否，1:是")
    private Integer vip;
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNum;
    @Schema(description = "每页数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;
}
