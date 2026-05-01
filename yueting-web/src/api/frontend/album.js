// @/api/frontend/artist.js
import request from './request';
import { toRaw } from 'vue';
// 查询我的专辑
export function apiSearchMyAlbums() {
	return request.get('/album/me/albums');
}

// 查询艺人的专辑
export function apiGetAlbumsByArtistId(form) {
	return request.get(
		`/album/artist/${form.artistId}/albums?pageNum=${form.pageNum}&pageSize=${form.pageSize}`
	);
}

// 查询专辑及歌曲信息
export function apiGetAlbumWithSongsByAlbumId(albumId) {
	return request.get(`/album/${albumId}/albumWithSongs`);
}

export function apiGetMyAlbumWithSongsByAlbumId(albumId) {
	return request.get(`/album/me/${albumId}/albumWithSongs`);
}

// 查询专辑基本信息
export function apiGetAlbumBasicByAlbumId(albumId) {
	return request.get(`/album/${albumId}/basic`);
}

//更改专辑封面
export function apiUpdateAlbumCover(albumId, cover) {
	const params = new URLSearchParams();
	params.append('coverUrl', cover);
	return request.put(`/album/${albumId}/cover`, params);
}

// 修改专辑基本信息
export function apiUpdateAlbumBasic(form) {
	return request.put(`/album/${form.albumId}/basic`, form);
}

// 删除专辑
export function apiDeleteAlbum(albumId) {
	return request.delete(`/album/${albumId}`);
}

export function apiSearchAlbumByKeyword(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/album/search?${searchParams.toString()}`);
}
