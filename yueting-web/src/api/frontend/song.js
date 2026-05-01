// @/api/frontend/user.js
import request from './request';
import { toRaw } from 'vue';

// 上传歌曲
export function apiUploadMusic(form) {
	return request.post('/song/me/upload', form);
}

// 获取艺人热门歌曲
export function apiGetHotSongs(artistId) {
	return request.get(`/song/hot/${artistId}`);
}

// 删除歌曲
export function apiDeleteSong(songId) {
	return request.delete(`/song/me/${songId}`);
}

// 查询歌曲详细信息
export function apiGetSongDetail(songId) {
	return request.get(`/song/${songId}/detail`);
}

export function apiGetMySongApplications(form) {
	const params = toRaw(form);
	const searchParams = new URLSearchParams(params);
	return request.get(`/song/me/applications?${searchParams.toString()}`);
}

export function apiSearchSongByKeyword(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/song/search?${searchParams.toString()}`);
}

export function apiSearchSongByLyrics(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/song/search/lyrics?${searchParams.toString()}`);
}

export function apiIncrementPlay(songId) {
	return request.post(`/song/increment-play/${songId}`);
}
