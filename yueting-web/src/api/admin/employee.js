import request from './request';
import { toRaw } from 'vue';

export function apiLogin(username, password) {
	const params = new URLSearchParams();
	params.append('username', username);
	params.append('password', password);
	return request.post('/employee/login', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}

/**
 * 分页查询员工列表
 */
export function apiPageEmployee(current, size, realName) {
	let url = `/employee/list?current=${current}&size=${size}`;
	if (realName) {
		url += `&realName=${encodeURIComponent(realName)}`;
	}
	return request.get(url);
}

/**
 * 获取员工详情
 */
export function apiEmployeeDetail(id) {
	return request.get(`/employee/${id}`);
}

/**
 * 创建员工
 */
export function apiCreateEmployee(form) {
	return request.post('/employee', toRaw(form));
}

/**
 * 更新员工
 */
export function apiUpdateEmployee(form) {
	return request.put('/employee', toRaw(form));
}

/**
 * 删除员工
 */
export function apiDeleteEmployee(id) {
	return request.delete(`/employee/${id}`);
}