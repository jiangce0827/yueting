// @/api/frontend/note.js
import request from './request';

/**
 * 发布笔记
 */
export function apiCreateNote(form) {
	return request.post('/note', form);
}

/**
 * 删除笔记
 */
export function apiDeleteNote(noteId) {
	return request.delete(`/note/${noteId}`);
}

/**
 * 获取关注用户的笔记列表
 */
export function apiGetFollowingNotes() {
	return request.get('/note/following');
}

/**
 * 获取我的笔记列表
 */
export function apiGetMyNotes() {
	return request.get('/note/my');
}

/**
 * 点赞笔记
 */
export function apiLikeNote(noteId) {
	return request.post(`/note/${noteId}/like`);
}

/**
 * 取消点赞笔记
 */
export function apiUnlikeNote(noteId) {
	return request.delete(`/note/${noteId}/like`);
}

/**
 * 评论笔记
 */
export function apiCommentNote(noteId, data) {
	return request.post(`/note/${noteId}/comment`, data);
}

/**
 * 删除评论
 */
export function apiDeleteComment(noteId, commentId) {
	return request.delete(`/note/${noteId}/comment/${commentId}`);
}

/**
 * 转发笔记
 */
export function apiForwardNote(noteId) {
	return request.post(`/note/${noteId}/forward`);
}

/**
 * 获取笔记详情
 */
export function apiGetNoteDetail(noteId) {
	return request.get(`/note/${noteId}`);
}

/**
 * 获取热门用户
 */
export function apiGetPopularUsers(limit = 10) {
	return request.get(`/note/popular-users?limit=${limit}`);
}