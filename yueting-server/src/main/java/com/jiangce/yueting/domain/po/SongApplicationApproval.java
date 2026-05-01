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
public class SongApplicationApproval {
    @TableId(type = IdType.AUTO)
    private Long approvalId;
    private Long applicationId;
    private Long artistId;
    private LocalDateTime notifiedAt;
    private LocalDateTime respondedAt;
    private Integer status;
}
