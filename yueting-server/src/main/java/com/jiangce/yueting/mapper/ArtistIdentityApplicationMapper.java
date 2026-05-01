package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.ArtistApplicationIdentityPageDTO;
import com.jiangce.yueting.domain.po.ArtistIdentityApplication;
import com.jiangce.yueting.domain.vo.ArtistIdentityApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 艺人身份申请 Mapper
 */
@Mapper
public interface ArtistIdentityApplicationMapper extends BaseMapper<ArtistIdentityApplication> {

    /**
     * 分页查询艺人身份申请列表
     *
     * @param artistApplicationIdentityPageDTO 查询条件
     * @param page 分页参数
     * @return 艺人身份申请分页列表
     */
    IPage<ArtistIdentityApplicationVO> pageArtistIdentityApplication(ArtistApplicationIdentityPageDTO artistApplicationIdentityPageDTO, Page<ArtistIdentityApplicationVO> page);
}
