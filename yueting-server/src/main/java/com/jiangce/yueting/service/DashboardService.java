package com.jiangce.yueting.service;

import java.util.Map;

/**
 * 控制台服务接口
 */
public interface DashboardService {

    /**
     * 获取控制台统计数据
     */
    Map<String, Object> getDashboardStats();
}