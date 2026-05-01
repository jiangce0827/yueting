package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.BannerVO;
import com.jiangce.yueting.service.BannerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图 C端 Controller
 */
@Slf4j
@RestController
@RequestMapping("/web/banner")
@Tag(name = "轮播图相关接口")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 获取已启用的轮播图列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取已启用的轮播图列表")
    public Result<List<BannerVO>> getEnabledBanners() {
        log.info("获取已启用的轮播图列表");
        List<BannerVO> banners = bannerService.getEnabledBanners();
        return Result.success(banners);
    }
}