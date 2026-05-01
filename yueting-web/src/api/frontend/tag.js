// @/api/frontend/user.js
import request from './request';

// 获取所有标签
export function apiGetAllTags (form) {
	return request.get('/tag/all', form);
}

