import request from './request';
import { toRaw } from 'vue';
//分页查询音乐
export function apiPageSong(pageForm) {
	// 将reactive对象转化为普通对象
	const params = toRaw(pageForm);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/song/pageSong?${searchParams.toString()}`);
}

//更新音乐
export function apiUpdateSong(musicId, status) {
	return request.post(`/song/updateSong/${songId}/${status}`);
}

// 分页查询音乐审核
export function apiPageSongApplication(pageForm) {
	// 将reactive对象转化为普通对象
	const params = toRaw(pageForm);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/song/pageSongApplication?${searchParams.toString()}`);
}

//音乐审核通过
export function apiApprove(id) {
	return request.put(`/song/${id}/approve`);
}

//音乐审核不通过
export function apiReject(id, reason) {
	const params = new URLSearchParams();
	params.append('rejectionReason', reason);
	return request.put(`/song/${id}/reject`, params);
}
