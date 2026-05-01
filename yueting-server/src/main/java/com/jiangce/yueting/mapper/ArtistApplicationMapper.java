package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.ArtistApplicationPageDTO;
import com.jiangce.yueting.domain.po.ArtistApplication;
import com.jiangce.yueting.domain.vo.ArtistApplicationVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 艺人申请 Mapper
 */
@Mapper
public interface ArtistApplicationMapper extends BaseMapper<ArtistApplication> {

    /**
     * 分页查询艺人申请列表
     *
     * @param artistApplicationPageDTO 查询条件
     * @param page 分页参数
     * @return 艺人申请分页列表
     */
    IPage<ArtistApplicationVO> pageArtistApplication(ArtistApplicationPageDTO artistApplicationPageDTO, Page<ArtistApplicationVO> page);
}
