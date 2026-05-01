import request from './request';

export function apiGetEnabledBanners() {
	return request.get('/banner/list');
}