package com.jiangce.yueting.controller.admin;

import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 控制台控制器
 */
@Slf4j
@RestController("AdminDashboardController")
@RequestMapping("/admin/dashboard")
@Tag(name = "控制台")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取控制台统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return Result.success(stats);
    }
}