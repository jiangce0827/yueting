package com.jiangce.yueting.controller.admin;

import com.jiangce.yueting.domain.po.Banner;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.BannerAdminVO;
import com.jiangce.yueting.domain.vo.BannerTargetOptionVO;
import com.jiangce.yueting.service.BannerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理端 Controller
 */
@Slf4j
@RestController("adminBannerController")
@RequestMapping("/admin/banner")
@Tag(name = "轮播图管理相关接口")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 分页查询轮播图列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询轮播图列表")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult> pageBanners(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("分页查询轮播图列表: pageNum={}, pageSize={}", pageNum, pageSize);
        PageResult pageResult = bannerService.pageBanners(pageNum, pageSize);
        return Result.success(pageResult);
    }

    @GetMapping("/target-options")
    @Operation(summary = "搜索轮播图跳转目标")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<BannerTargetOptionVO>> searchBannerTargets(
            @RequestParam("targetType") Integer targetType,
            @RequestParam("keyword") String keyword) {
        log.info("搜索轮播图跳转目标，targetType={}, keyword={}", targetType, keyword);
        return Result.success(bannerService.searchBannerTargets(targetType, keyword));
    }

    @GetMapping("/target-options/{targetType}/{targetId}")
    @Operation(summary = "获取轮播图跳转目标摘要")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<BannerTargetOptionVO> getBannerTargetOption(
            @PathVariable Integer targetType,
            @PathVariable String targetId) {
        log.info("获取轮播图跳转目标摘要，targetType={}, targetId={}", targetType, targetId);
        return Result.success(bannerService.getBannerTargetOption(targetType, targetId));
    }

    /**
     * 获取轮播图详情
     */
    @GetMapping("/{bannerId}")
    @Operation(summary = "获取轮播图详情")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<BannerAdminVO> getBannerById(@PathVariable Long bannerId) {
        log.info("获取轮播图详情: bannerId={}", bannerId);
        BannerAdminVO banner = bannerService.getBannerById(bannerId);
        return Result.success(banner);
    }

    /**
     * 新增轮播图
     */
    @PostMapping("/add")
    @Operation(summary = "新增轮播图")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addBanner(@RequestBody Banner banner) {
        log.info("新增轮播图: {}", banner);
        bannerService.addBanner(banner);
        return Result.success("新增成功");
    }

    /**
     * 更新轮播图
     */
    @PutMapping("/update")
    @Operation(summary = "更新轮播图")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateBanner(@RequestBody Banner banner) {
        log.info("更新轮播图: {}", banner);
        bannerService.updateBanner(banner);
        return Result.success("更新成功");
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{bannerId}")
    @Operation(summary = "删除轮播图")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteBanner(@PathVariable Long bannerId) {
        log.info("删除轮播图: bannerId={}", bannerId);
        bannerService.deleteBanner(bannerId);
        return Result.success("删除成功");
    }

    /**
     * 更新轮播图状态
     */
    @PutMapping("/status/{bannerId}")
    @Operation(summary = "更新轮播图状态")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateStatus(
            @PathVariable Long bannerId,
            @RequestParam("status") Integer status) {
        log.info("更新轮播图状态: bannerId={}, status={}", bannerId, status);
        bannerService.updateStatus(bannerId, status);
        return Result.success("状态更新成功");
    }
}
