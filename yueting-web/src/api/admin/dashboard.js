import request from './request';

/**
 * 获取控制台统计数据
 */
export function apiGetDashboardStats() {
    return request.get('/dashboard/stats');
}