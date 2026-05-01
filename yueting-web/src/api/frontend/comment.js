// @/api/frontend/comment.js
import request from './request';

/**
 * 发表评论
 * @param {Object} data - 评论数据
 * @param {number} data.targetType - 目标类型：1-歌曲，2-歌单，3-专辑
 * @param {string|number} data.targetId - 目标ID
 * @param {string} data.content - 评论内容
 * @param {string|number} [data.parentId] - 父评论ID（回复时使用）
 * @param {string|number} [data.replyToUserId] - 回复目标用户ID
 */
export function apiCreateComment(data) {
	return request.post('/comment', data);
}

/**
 * 删除评论
 * @param {string|number} commentId - 评论ID
 */
export function apiDeleteComment(commentId) {
	return request.delete(`/comment/${commentId}`);
}

/**
 * 获取评论列表
 * @param {number} targetType - 目标类型：1-歌曲，2-歌单，3-专辑
 * @param {string|number} targetId - 目标ID
 */
export function apiGetComments(targetType, targetId) {
	return request.get('/comment', {
		params: {
			targetType,
			targetId,
		},
	});
}

/**
 * 点赞评论
 * @param {string|number} commentId - 评论ID
 */
export function apiLikeComment(commentId) {
	return request.post(`/comment/${commentId}/like`);
}

/**
 * 取消点赞评论
 * @param {string|number} commentId - 评论ID
 */
export function apiUnlikeComment(commentId) {
	return request.delete(`/comment/${commentId}/like`);
}
