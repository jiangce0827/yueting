package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.po.Banner;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.BannerAdminVO;
import com.jiangce.yueting.domain.vo.BannerTargetOptionVO;
import com.jiangce.yueting.domain.vo.BannerVO;

import java.util.List;

/**
 * 轮播图 Service
 */
public interface BannerService {

    /**
     * 分页查询轮播图列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult pageBanners(Integer pageNum, Integer pageSize);

    /**
     * 获取已启用的轮播图列表
     *
     * @return 轮播图列表
     */
    List<BannerVO> getEnabledBanners();

    /**
     * 根据ID获取轮播图详情
     *
     * @param bannerId 轮播图ID
     * @return 轮播图详情
     */
    BannerAdminVO getBannerById(Long bannerId);

    /**
     * 新增轮播图
     *
     * @param banner 轮播图信息
     */
    void addBanner(Banner banner);

    /**
     * 更新轮播图
     *
     * @param banner 轮播图信息
     */
    void updateBanner(Banner banner);

    /**
     * 删除轮播图
     *
     * @param bannerId 轮播图ID
     */
    void deleteBanner(Long bannerId);

    /**
     * 更新轮播图状态
     *
     * @param bannerId 轮播图ID
     * @param status   状态：0-禁用，1-启用
     */
    void updateStatus(Long bannerId, Integer status);

    /**
     * 根据跳转类型和关键字搜索可选跳转目标
     */
    List<BannerTargetOptionVO> searchBannerTargets(Integer targetType, String keyword);

    /**
     * 获取指定跳转目标的摘要信息
     */
    BannerTargetOptionVO getBannerTargetOption(Integer targetType, String targetId);
}
