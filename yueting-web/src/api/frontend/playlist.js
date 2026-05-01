import request from './request';
import { toRaw } from 'vue';
export function apiGetCreatePlayListsByUserId(userId) {
	return request.get(`/playlist/create/${userId}`);
}

export function apiGetCollectPlayListsByUserId(userId) {
	return request.get(`/playlist/collect/${userId}`);
}

export function apiCreatePlaylist(playlistName) {
	const params = new URLSearchParams();
	params.append('playlistName', playlistName);
	return request.post('/playlist/me/create', params);
}

export function getPlayListDetail(playlistId) {
	return request.get(`/playlist/me/${playlistId}/withSongs`);
}

/**添加歌曲到歌单 */
export function apiAddSongToPlaylist(playlistId, songId) {
	return request.post(`/playlist/me/${playlistId}/add/${songId}`);
}

export function apiDeleteSongFromPlaylist(playlistId, songId) {
	return request.post(`/playlist/me/${playlistId}/delete/${songId}`);
}

export function apiUpdatePlaylist(form) {
	return request.put(`/playlist/me/${form.playlistId}`, form);
}

export function apiUpdatePlaylistCover(id, url) {
	const params = new URLSearchParams();
	params.append('coverUrl', url);
	return request.put(`/playlist/me/${id}/cover`, params);
}

export function apiGetPlaylistWithUser(playlistId) {
	return request.get(`/playlist/me/${playlistId}/withUser`);
}

export function apiCollectPlaylist(playlistId) {
	return request.post(`/playlist/me/${playlistId}/collect`);
}

export function apiDeletePlaylist(playlistId) {
	return request.delete(`/playlist/me/${playlistId}`);
}

export function apiCancelCollectPlaylist(playlistId) {
	return request.delete(`/playlist/me/${playlistId}/collect`);
}

export function apiGetHotPlaylistsByPlayCount() {
	return request.get('/playlist/hot/playCount');
}

export function getSystemTopLists() {
	return request.get('/playlist/toplist');
}

export function apiSearchPlaylistByTag(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/playlist/search/tag?${searchParams.toString()}`);
}
export function apiSearchPlaylistByKeyword(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/playlist/search/keyword?${searchParams.toString()}`);
}
