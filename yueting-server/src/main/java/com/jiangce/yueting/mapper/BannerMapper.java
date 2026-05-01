package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.po.Banner;
import com.jiangce.yueting.domain.vo.BannerVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 轮播图 Mapper
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 分页查询轮播图列表
     *
     * @param page 分页参数
     * @return 轮播图分页列表
     */
    IPage<Banner> pageBanners(Page<Banner> page);

    /**
     * 获取已启用的轮播图列表（按排序顺序）
     *
     * @return 轮播图列表
     */
    List<BannerVO> selectEnabledBanners();
}