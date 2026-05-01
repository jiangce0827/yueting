import request from './request';
import { toRaw } from 'vue';

// 分页查询轮播图
export function apiPageBanners(pageForm) {
	const params = toRaw(pageForm);
	const searchParams = new URLSearchParams(params);
	return request.get(`/banner/list?${searchParams.toString()}`);
}

// 获取轮播图详情
export function apiGetBannerById(bannerId) {
	return request.get(`/banner/${bannerId}`);
}

// 新增轮播图
export function apiAddBanner(banner) {
	return request.post('/banner/add', banner);
}

// 更新轮播图
export function apiUpdateBanner(banner) {
	return request.put('/banner/update', banner);
}

// 删除轮播图
export function apiDeleteBanner(bannerId) {
	return request.delete(`/banner/${bannerId}`);
}

// 更新轮播图状态
export function apiUpdateBannerStatus(bannerId, status) {
	return request.put(`/banner/status/${bannerId}?status=${status}`);
}

export function apiSearchBannerTargets(targetType, keyword) {
	const searchParams = new URLSearchParams();
	searchParams.append('targetType', targetType);
	searchParams.append('keyword', keyword);
	return request.get(`/banner/target-options?${searchParams.toString()}`);
}

export function apiGetBannerTargetOption(targetType, targetId) {
	return request.get(`/banner/target-options/${targetType}/${targetId}`);
}
