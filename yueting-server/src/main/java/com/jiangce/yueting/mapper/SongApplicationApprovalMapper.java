package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.SongApplicationApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 歌曲申请审批 Mapper
 */
@Mapper
public interface SongApplicationApprovalMapper extends BaseMapper<SongApplicationApproval> {

    /**
     * 批量插入审批记录
     *
     * @param songApplicationApprovals 审批记录列表
     */
    void insertBatch(List<SongApplicationApproval> songApplicationApprovals);
}
