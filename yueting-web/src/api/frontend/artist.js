// @/api/frontend/artist.js
import request from './request';
import { toRaw } from 'vue';

// 查询用户绑定艺人状态
export function apiGetArtistStatus() {
	return request.get('/artist/me/getArtistStatus');
}

// 搜索艺人
export function searchArtistBasic(keyword) {
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams();
	searchParams.append('keyword', keyword);
	return request.get(`/artist/search?${searchParams.toString()}`);
}
// 申请艺人
export function applyArtist(form) {
	return request.post('/artist/me/apply/artist', form);
}

// 获取我的艺人基本信息
export function apiGetMyArtistBasic() {
	return request.get('/artist/me/info');
}

// 提交艺人身份认证
export function apiApplyArtistIdentity(form) {
	return request.post('/artist/me/apply/artistIdentity', form);
}

// 修改艺人信息
export function apiUpdateArtistInfo(form) {
	return request.put('/artist/me/info', form);
}

//查询艺人基本信息
export function apiGetArtistBasicByArtistId(artistId) {
	return request.get(`/artist/${artistId}/basic`);
}

//根据用户ID查询艺人基本信息
export function apiGetArtistBasicByUserId(userId) {
	return request.get(`/artist/user/${userId}/basic`);
}

//搜索艺人
export function apiSearchArtist(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/artist/search?${searchParams.toString()}`);
}
