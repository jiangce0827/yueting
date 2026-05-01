import request from './request';

/**
 * 获取私信列表（按会话分组，每个会话显示最新消息）
 */
export const apiGetPrivateMessageList = () => {
	return request.get('/private-message/list');
};

/**
 * 获取与某用户的聊天记录
 * @param {number} userId - 对方用户ID
 */
export const apiGetChatHistory = (userId) => {
	return request.get(`/private-message/chat/${userId}`);
};

/**
 * 发送私信
 * @param {Object} data - 私信数据
 * @param {number} data.receiverId - 接收者ID（可选，与receiverName二选一）
 * @param {string} data.receiverName - 接收者用户名（可选，与receiverId二选一）
 * @param {string} data.content - 私信内容
 */
export const apiSendPrivateMessage = (data) => {
	return request.post('/private-message/send', data);
};

/**
 * 删除私信
 * @param {number} messageId - 私信ID
 */
export const apiDeletePrivateMessage = (messageId) => {
	return request.delete(`/private-message/${messageId}`);
};

/**
 * 获取通知列表
 * @param {Object} params - 查询参数
 * @param {string} params.type - 通知类型，多个类型用逗号分隔，如'1,2,3'
 */
export const apiGetNotificationList = (params) => {
	return request.get('/notification/list', { params });
};

/**
 * 删除通知
 * @param {number} notificationId - 通知ID
 */
export const apiDeleteNotification = (notificationId) => {
	return request.delete(`/notification/${notificationId}`);
};

/**
 * 获取未读消息数量
 */
export const apiGetUnreadCount = () => {
	return request.get('/message/unread-count');
};

/**
 * 一键标记所有消息为已读
 */
export const apiMarkAllAsRead = () => {
	return request.post('/message/mark-all-read');
};

/**
 * 标记私信为已读
 * @param {number} senderId - 发送者ID
 */
export const apiMarkPrivateMessageRead = (senderId) => {
	return request.post(`/private-message/read/${senderId}`);
};

/**
 * 清除与某用户的对话
 * @param {number} targetUserId - 对方用户ID
 */
export const apiClearConversation = (targetUserId) => {
	return request.delete(`/private-message/clear/${targetUserId}`);
};

/**
 * 标记通知为已读
 * @param {number} notificationId - 通知ID
 */
export const apiMarkNotificationRead = (notificationId) => {
	return request.post(`/notification/read/${notificationId}`);
};
