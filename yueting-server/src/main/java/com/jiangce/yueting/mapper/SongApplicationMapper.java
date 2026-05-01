package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.SongApplicationPageDTO;
import com.jiangce.yueting.domain.po.SongApplication;
import com.jiangce.yueting.domain.vo.SongApplicationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 歌曲申请 Mapper
 */
@Mapper
public interface SongApplicationMapper extends BaseMapper<SongApplication> {

    /**
     * 批量插入歌曲申请记录
     *
     * @param songApplications 歌曲申请列表
     */
    void insertBatch(List<SongApplication> songApplications);

    /**
     * 分页查询歌曲审核列表
     *
     * @param songApplicationPageDTO 查询条件
     * @param page 分页参数
     * @return 歌曲申请分页列表
     */
    IPage<SongApplicationVO> pageMusicReview(SongApplicationPageDTO songApplicationPageDTO, Page<SongApplicationVO> page);
}
