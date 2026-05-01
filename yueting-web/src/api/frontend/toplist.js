import request from './request';

export function apiGetTopList(id) {
	return request.get(`/toplist/${id}`);
}