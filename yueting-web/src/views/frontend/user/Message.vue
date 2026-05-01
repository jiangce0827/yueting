<template>
	<div class="message-container">
		<!-- 左侧菜单 -->
		<div class="left-sidebar">
			<div class="sidebar-title">我的消息</div>
			<div class="menu-list">
				<div class="menu-item" :class="{ active: activeMenu === 'private' }" @click="switchMenu('private')">
					<i class="iconfont icon-message"></i>
					<span>私信</span>
					<span v-if="unreadCount.private > 0" class="badge">{{ unreadCount.private }}</span>
				</div>
				<div class="menu-item" :class="{ active: activeMenu === 'comment' }" @click="switchMenu('comment')">
					<i class="iconfont icon-comment"></i>
					<span>评论</span>
					<span v-if="unreadCount.comment > 0" class="badge">{{ unreadCount.comment }}</span>
				</div>
				<div class="menu-item" :class="{ active: activeMenu === 'notification' }" @click="switchMenu('notification')">
					<i class="iconfont icon-notification"></i>
					<span>通知</span>
					<span v-if="unreadCount.notification > 0" class="badge">{{ unreadCount.notification }}</span>
				</div>
			</div>
			<div class="read-all-btn" @click="markAllAsRead">一键已读</div>
		</div>

		<!-- 右侧内容区 -->
		<div class="right-content">
			<!-- 右侧头部 -->
			<div class="content-header">
				<!-- 私信头部 -->
				<template v-if="activeMenu === 'private'">
					<template v-if="currentChatUser">
						<div class="chat-header">
							<span class="breadcrumb" @click="backToList">私信</span>
							<span class="breadcrumb-separator">></span>
							<span class="current-user">{{ currentChatUser.senderName }}</span>
						</div>
						<el-button link @click="clearConversation">
							<el-icon><Delete /></el-icon> 清除对话
						</el-button>
					</template>
					<template v-else>
						<span class="header-title">私信</span>
						<el-button class="new-message-btn" type="primary" size="small" @click="showNewMessageDialog = true">
							<el-icon><Plus /></el-icon> 发新私信
						</el-button>
					</template>
				</template>
				<!-- 评论头部 -->
				<template v-else-if="activeMenu === 'comment'">
					<span class="header-title">评论</span>
				</template>
				<!-- 通知头部 -->
				<template v-else-if="activeMenu === 'notification'">
					<span class="header-title">通知</span>
				</template>
			</div>

			<!-- 右侧内容 -->
			<div class="content-body">
				<!-- 私信会话列表 -->
				<template v-if="activeMenu === 'private' && !currentChatUser">
					<div v-if="privateMessageList.length > 0" class="list-content">
						<div
							v-for="item in privateMessageList"
							:key="item.messageId"
							class="message-item"
							@click="selectConversation(item)"
						>
							<div class="message-avatar">
								<img :src="getAvatarUrl(item.senderAvatar)" alt="头像" @error="handleAvatarError" />
								<span v-if="hasUnread(item)" class="unread-dot"></span>
							</div>
							<div class="message-info">
								<div class="message-header">
									<span class="sender-name">{{ item.senderName }}</span>
								</div>
								<div class="message-preview">{{ item.content }}</div>
							</div>
							<div class="message-meta">
								<span class="message-time">{{ formatTime(item.createdAt) }}</span>
							</div>
						</div>
					</div>
					<div v-else class="empty-content">
						<el-empty description="暂无私信" />
					</div>
				</template>

				<!-- 私信聊天详情 -->
				<template v-if="activeMenu === 'private' && currentChatUser">
					<div class="chat-messages" ref="chatMessagesRef">
						<div class="date-divider">今天 {{ formatChatTime(new Date()) }}</div>
						<div
							v-for="msg in currentChatMessages"
							:key="msg.messageId"
							class="chat-message"
							:class="{ 'self': String(msg.senderId) === String(authStore.user.userId) }"
						>
							<img
								:src="getAvatarUrl(String(msg.senderId) === String(authStore.user.userId) ? authStore.user.avatarUrl : currentChatUser.senderAvatar)"
								class="chat-avatar"
								@error="handleAvatarError"
							/>
							<div class="chat-content">
								<div class="chat-bubble">{{ msg.content }}</div>
							</div>
						</div>
					</div>
					<div class="chat-input-area">
						<el-input
							v-model="chatInput"
							type="textarea"
							:rows="4"
							placeholder="输入消息..."
							resize="none"
							maxlength="200"
							show-word-limit
						/>
						<div class="input-actions">
							<el-button type="primary" @click="sendChatMessage" :disabled="!chatInput.trim()">
								发送
							</el-button>
						</div>
					</div>
				</template>

				<!-- 评论列表 -->
				<template v-if="activeMenu === 'comment'">
					<div v-if="commentList.length > 0" class="list-content">
						<div
							v-for="item in commentList"
							:key="item.notificationId"
							class="message-item"
							@click="handleNotificationClick(item)"
						>
							<div class="message-avatar">
								<img :src="getAvatarUrl(item.senderAvatar)" alt="头像" @error="handleAvatarError" />
								<span v-if="!item.isRead" class="unread-dot"></span>
							</div>
							<div class="message-info">
								<div class="message-header">
									<span class="sender-name">{{ item.senderName || '系统' }}</span>
									<span class="action-text">{{ getNotificationText(item) }}</span>
								</div>
								<div class="message-target" v-if="item.targetName">{{ item.targetName }}</div>
								<div class="message-preview">
									<span class="reply-prefix">{{ item.senderName }}：</span>{{ item.content }}
								</div>
							</div>
							<div class="message-meta">
								<span class="message-time">{{ formatTime(item.createdAt) }}</span>
								<span class="reply-btn-text" @click.stop="handleReply(item)">回复</span>
								<el-icon class="delete-icon" @click.stop="deleteNotification(item.notificationId)"><CircleClose /></el-icon>
							</div>
						</div>
					</div>
					<div v-else class="empty-content">
						<el-empty description="暂无评论通知" />
					</div>
				</template>

				<!-- 通知列表 -->
				<template v-if="activeMenu === 'notification'">
					<div v-if="notificationList.length > 0" class="list-content">
						<div
							v-for="item in notificationList"
							:key="item.notificationId"
							class="message-item"
							@click="handleNotificationClick(item)"
						>
							<div class="message-avatar">
								<img :src="getAvatarUrl(item.senderAvatar)" alt="头像" @error="handleAvatarError" />
								<span v-if="!item.isRead" class="unread-dot"></span>
							</div>
							<div class="message-info">
								<div class="message-header">
									<span class="sender-name">{{ item.senderName || '系统' }}</span>
									<span class="action-text">{{ getNotificationText(item) }}</span>
								</div>
								<div class="message-preview">{{ item.content }}</div>
							</div>
							<div class="message-meta">
								<span class="message-time">{{ formatTime(item.createdAt) }}</span>
								<el-icon class="delete-icon" @click.stop="deleteNotification(item.notificationId)"><CircleClose /></el-icon>
							</div>
						</div>
					</div>
					<div v-else class="empty-content">
						<el-empty description="暂无通知" />
					</div>
				</template>
			</div>
		</div>
	</div>

	<!-- 发新私信对话框 -->
	<el-dialog v-model="showNewMessageDialog" title="发新私信" width="500px">
		<el-form :model="newMessageForm" label-width="80px">
			<el-form-item label="收件人">
				<el-input v-model="newMessageForm.receiverName" placeholder="请输入用户名" />
			</el-form-item>
			<el-form-item label="内容">
				<el-input
					v-model="newMessageForm.content"
					type="textarea"
					:rows="4"
					placeholder="请输入私信内容"
					maxlength="500"
					show-word-limit
				/>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="showNewMessageDialog = false">取消</el-button>
			<el-button type="primary" @click="sendNewMessage">发送</el-button>
		</template>
	</el-dialog>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/frontend/auth';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, CircleClose, Delete } from '@element-plus/icons-vue';
import defaultAvatar from '@/assets/user_default_avatar.png';
import {
	apiGetPrivateMessageList,
	apiGetNotificationList,
	apiDeleteNotification,
	apiMarkAllAsRead,
	apiSendPrivateMessage,
	apiGetChatHistory,
	apiGetUnreadCount,
	apiMarkPrivateMessageRead,
	apiMarkNotificationRead,
	apiClearConversation
} from '@/api/frontend/message';
import { messageWebSocket } from '@/services/websocket';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
// 当前选中的菜单
const activeMenu = ref('private');

const getAvatarUrl = (avatarUrl) => avatarUrl || defaultAvatar;

const handleAvatarError = (event) => {
	if (event.target.src !== defaultAvatar) {
		event.target.src = defaultAvatar;
	}
};

// 未读消息数
const unreadCount = ref({
	private: 0,
	comment: 0,
	notification: 0
});

// 消息列表
const privateMessageList = ref([]);
const commentList = ref([]);
const notificationList = ref([]);

// 对话框状态
const showNewMessageDialog = ref(false);
const newMessageForm = ref({
	receiverName: '',
	content: ''
});

// 当前聊天
const currentChatUser = ref(null);
const currentChatMessages = ref([]);
const chatInput = ref('');
const chatMessagesRef = ref(null);

// 判断是否有未读消息
const hasUnread = (item) => {
	return !item.isRead && String(item.receiverId) === String(authStore.user.userId);
};

// 切换菜单
const switchMenu = (menu) => {
	activeMenu.value = menu;
	// 退出聊天状态
	currentChatUser.value = null;
	currentChatMessages.value = [];

	if (menu === 'private') {
		getPrivateMessageList();
	} else if (menu === 'comment') {
		getCommentList();
	} else if (menu === 'notification') {
		getNotificationList();
	}
};

// 返回列表
const backToList = () => {
	currentChatUser.value = null;
	currentChatMessages.value = [];
	getPrivateMessageList();
};

// 获取未读消息数
const getUnreadCount = async () => {
	try {
		const res = await apiGetUnreadCount();
		if (res.data.code === 1) {
			unreadCount.value = res.data.data;
		}
	} catch (error) {
		console.error('获取未读消息数失败:', error);
	}
};

// 获取私信列表
const getPrivateMessageList = async () => {
	try {
		const res = await apiGetPrivateMessageList();
		if (res.data.code === 1) {
			privateMessageList.value = res.data.data || [];
		}
	} catch (error) {
		console.error('获取私信列表失败:', error);
	}
};

// 获取评论通知列表
const getCommentList = async () => {
	try {
		const res = await apiGetNotificationList({ type: '3,6,7,8' }); // 3-回复评论, 6-评论歌单, 7-评论歌曲, 8-评论专辑
		if (res.data.code === 1) {
			commentList.value = res.data.data || [];
		}
	} catch (error) {
		console.error('获取评论通知失败:', error);
	}
};

// 获取系统通知列表
const getNotificationList = async () => {
	try {
		const res = await apiGetNotificationList({ type: '5,1,2,4,11,12' }); // 系统公告、点赞、关注、音乐人申请等
		if (res.data.code === 1) {
			notificationList.value = res.data.data || [];
		}
	} catch (error) {
		console.error('获取通知列表失败:', error);
	}
};

// 选择会话
const selectConversation = async (item) => {
	currentChatUser.value = item;
	try {
		// 使用 otherUserId 查询聊天记录
		const targetUserId = item.otherUserId || item.senderId;
		const res = await apiGetChatHistory(targetUserId);
		if (res.data.code === 1) {
			currentChatMessages.value = res.data.data || [];
			// 标记为已读
			await apiMarkPrivateMessageRead(targetUserId);
			// 更新未读数
			getUnreadCount();
			// 更新列表中的未读状态
			const index = privateMessageList.value.findIndex(msg => msg.otherUserId === targetUserId);
			if (index !== -1) {
				privateMessageList.value[index].isRead = true;
			}
			// 滚动到底部
			nextTick(() => {
				scrollToBottom();
			});
		}
	} catch (error) {
		console.error('获取聊天历史失败:', error);
	}
};

// 滚动到底部
const scrollToBottom = () => {
	if (chatMessagesRef.value) {
		chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
	}
};

// 发送聊天消息
const sendChatMessage = async () => {
	if (!chatInput.value.trim() || !currentChatUser.value) return;
	try {
		// 使用 otherUserId 发送消息
		const targetUserId = currentChatUser.value.otherUserId || currentChatUser.value.senderId;
		const res = await apiSendPrivateMessage({
			receiverId: targetUserId,
			content: chatInput.value
		});
		if (res.data.code === 1) {
			currentChatMessages.value.push({
				messageId: Date.now(),
				senderId: String(authStore.user.userId),
				content: chatInput.value,
				createdAt: new Date().toISOString()
			});
			chatInput.value = '';
			nextTick(() => {
				scrollToBottom();
			});
			// 刷新会话列表
			getPrivateMessageList();
		}
	} catch (error) {
		console.error('发送消息失败:', error);
	}
};

// 清除对话
const clearConversation = async () => {
	if (!currentChatUser.value) return;
	try {
		await ElMessageBox.confirm('确定清除与该用户的所有对话记录吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		});
		const targetUserId = currentChatUser.value.otherUserId || currentChatUser.value.senderId;
		const res = await apiClearConversation(targetUserId);
		if (res.data.code === 1) {
			ElMessage.success('对话已清除');
			currentChatUser.value = null;
			currentChatMessages.value = [];
			getPrivateMessageList();
		}
	} catch (error) {
		if (error !== 'cancel') {
			console.error('清除对话失败:', error);
		}
	}
};

// 删除通知
const deleteNotification = async (notificationId) => {
	try {
		await ElMessageBox.confirm('确定删除这条通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		});
		const res = await apiDeleteNotification(notificationId);
		if (res.data.code === 1) {
			ElMessage.success('删除成功');
			getCommentList();
			getNotificationList();
			getUnreadCount();
		}
	} catch (error) {
		if (error !== 'cancel') {
			console.error('删除通知失败:', error);
		}
	}
};

// 一键已读
const markAllAsRead = async () => {
	try {
		const res = await apiMarkAllAsRead();
		if (res.data.code === 1) {
			ElMessage.success('已全部标记为已读');
			getUnreadCount();
			// 刷新当前列表
			if (activeMenu.value === 'private') {
				getPrivateMessageList();
			} else if (activeMenu.value === 'comment') {
				getCommentList();
			} else if (activeMenu.value === 'notification') {
				getNotificationList();
			}
		}
	} catch (error) {
		console.error('标记已读失败:', error);
	}
};

// 发送新私信
const sendNewMessage = async () => {
	if (!newMessageForm.value.receiverName.trim()) {
		ElMessage.warning('请输入收件人用户名');
		return;
	}
	if (!newMessageForm.value.content.trim()) {
		ElMessage.warning('请输入私信内容');
		return;
	}
	try {
		const res = await apiSendPrivateMessage({
			receiverName: newMessageForm.value.receiverName,
			content: newMessageForm.value.content
		});
		if (res.data.code === 1) {
			ElMessage.success('发送成功');
			showNewMessageDialog.value = false;
			newMessageForm.value = { receiverName: '', content: '' };
			getPrivateMessageList();
		}
	} catch (error) {
		console.error('发送私信失败:', error);
	}
};

// 处理通知点击
const handleNotificationClick = async (item) => {
	if (!item.isRead) {
		try {
			const res = await apiMarkNotificationRead(item.notificationId);
			if (res.data.code === 1) {
				item.isRead = true;
				getUnreadCount();
			}
		} catch (error) {
			console.error('标记通知已读失败:', error);
		}
	}

	// 评论相关通知和点赞通知：targetType 表示被评论对象的类型 (1-歌曲, 2-歌单, 3-专辑)
	if (item.type === 1 || item.type === 3 || item.type === 6 || item.type === 7 || item.type === 8) {
		if (item.targetType === 1 && item.targetSubId) {
			// 歌曲评论/点赞
			router.push(`/song?id=${item.targetSubId}`);
		} else if (item.targetType === 2 && item.targetSubId) {
			// 歌单评论/点赞
			router.push(`/playlist?id=${item.targetSubId}`);
		} else if (item.targetType === 3 && item.targetSubId) {
			// 专辑评论/点赞
			router.push(`/album?id=${item.targetSubId}`);
		}
	} else if (item.targetType === 6 && item.targetId) {
		// 关注通知
		router.push(`/user/home?id=${item.targetId}`);
	}
};

// 处理评论回复
const handleReply = (item) => {
	// 跳转到对应的歌曲/歌单页面
	if (item.targetType === 1 && item.targetSubId) {
		router.push(`/song?id=${item.targetSubId}`);
	} else if (item.targetType === 2 && item.targetSubId) {
		router.push(`/playlist?id=${item.targetSubId}`);
	} else if (item.targetType === 3 && item.targetSubId) {
		router.push(`/album?id=${item.targetSubId}`);
	}
};

// 格式化时间
const formatTime = (time) => {
	if (!time) return '';
	const date = new Date(time);
	const now = new Date();
	const diff = now - date;

	if (diff < 60 * 60 * 1000) {
		const minutes = Math.floor(diff / (60 * 1000));
		return minutes <= 0 ? '刚刚' : `${minutes}分钟前`;
	}
	if (diff < 24 * 60 * 60 * 1000) {
		const hours = Math.floor(diff / (60 * 60 * 1000));
		return `${hours}小时前`;
	}
	if (date.getFullYear() === now.getFullYear()) {
		return `${date.getMonth() + 1}月${date.getDate()}日`;
	}
	return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
};

// 格式化聊天时间
const formatChatTime = (date) => {
	const hours = date.getHours().toString().padStart(2, '0');
	const minutes = date.getMinutes().toString().padStart(2, '0');
	return `${hours}:${minutes}`;
};

// 获取通知文本
const getNotificationText = (item) => {
	const texts = {
		1: '赞了你的评论',
		2: '赞了你的笔记',
		3: '回复了你的评论',
		4: '关注了你',
		5: '',
		6: '评论了你的歌单',
		7: '评论了你的歌曲',
		8: '评论了你的专辑',
		11: '音乐人申请已通过',
		12: '音乐人申请未通过'
	};
	return texts[item.type] || '';
};

onMounted(() => {
	getUnreadCount();
	getPrivateMessageList();
	getCommentList();
	getNotificationList();

	// 连接 WebSocket
	if (authStore.user && authStore.token) {
		connectWebSocket();
	}

	// 检查 URL 参数，如果有 userId 则自动打开与该用户的聊天
	const { userId, nickname } = route.query;
	if (userId) {
		// 等待私信列表加载完成
		setTimeout(() => {
			// 在私信列表中查找该用户
			const existingConversation = privateMessageList.value.find(
				msg => String(msg.otherUserId) === String(userId) || String(msg.senderId) === String(userId)
			);
			if (existingConversation) {
				selectConversation(existingConversation);
			} else {
				// 如果没有现有会话，创建一个新的聊天对象
				currentChatUser.value = {
					otherUserId: userId,
					senderId: userId,
					senderName: nickname || '用户',
					senderAvatar: defaultAvatar,
					content: ''
				};
				// 获取聊天历史
				apiGetChatHistory(userId).then(res => {
					if (res.data.code === 1) {
						currentChatMessages.value = res.data.data || [];
					}
				});
			}
		}, 500);
	}
});

// 连接 WebSocket
const connectWebSocket = () => {
	messageWebSocket.connect(authStore.user.userId, authStore.token);

	// 设置消息接收回调
	messageWebSocket.onMessage((message) => {
		console.log('[Message] Received WebSocket message:', message);

		// 如果当前正在与发送者聊天，添加消息到聊天列表
		if (currentChatUser.value &&
			String(message.senderId) === String(currentChatUser.value.otherUserId)) {
			currentChatMessages.value.push(message);
			nextTick(() => {
				scrollToBottom();
			});
			// 标记为已读
			apiMarkPrivateMessageRead(message.senderId);
		} else {
			// 刷新会话列表
			getPrivateMessageList();
		}

		// 更新未读数
		getUnreadCount();
	});

	// 设置连接成功回调
	messageWebSocket.onConnect(() => {
		console.log('[Message] WebSocket connected');
	});

	// 设置断开连接回调
	messageWebSocket.onDisconnect(() => {
		console.log('[Message] WebSocket disconnected');
	});
};

// 组件卸载时断开 WebSocket
onUnmounted(() => {
	messageWebSocket.disconnect();
});
</script>

<style lang="scss" scoped>
.message-container {
	width: 950px;
	margin: 20px auto;
	display: flex;
	background: #fff;
	min-height: 600px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

	.left-sidebar {
		width: 180px;
		background: #f9f9f9;
		border-right: 1px solid #e0e0e0;
		padding: 20px 0;
		flex-shrink: 0;

		.sidebar-title {
			font-size: 18px;
			font-weight: bold;
			padding: 0 20px 20px;
			border-bottom: 1px solid #e0e0e0;
			margin-bottom: 10px;
		}

		.menu-list {
			.menu-item {
				display: flex;
				align-items: center;
				padding: 12px 20px;
				cursor: pointer;
				transition: background 0.3s;
				position: relative;

				&:hover,
				&.active {
					background: #e8e8e8;
				}

				i {
					font-size: 16px;
					margin-right: 10px;
					color: #666;
				}

				span {
					font-size: 14px;
					color: #333;
				}

				.badge {
					position: absolute;
					right: 20px;
					background: #ff4d4f;
					color: #fff;
					font-size: 12px;
					padding: 2px 8px;
					border-radius: 10px;
				}
			}
		}

		.read-all-btn {
			margin: 20px;
			padding: 10px;
			text-align: center;
			border: 1px solid #ddd;
			border-radius: 20px;
			font-size: 14px;
			color: #666;
			cursor: pointer;
			transition: all 0.3s;

			&:hover {
				border-color: #999;
				color: #333;
			}
		}
	}

	.right-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		min-width: 0;

		.content-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 15px 20px;
			border-bottom: 2px solid #ec4141;
			flex-shrink: 0;

			.header-title {
				font-size: 16px;
				font-weight: bold;
				color: #333;
			}

			.chat-header {
				display: flex;
				align-items: center;
				gap: 8px;

				.breadcrumb {
					font-size: 14px;
					color: #409eff;
					cursor: pointer;

					&:hover {
						text-decoration: underline;
					}
				}

				.breadcrumb-separator {
					font-size: 14px;
					color: #666;
				}

				.current-user {
					font-size: 14px;
					font-weight: 500;
					color: #333;
				}
			}

			.el-button {
				color: #999;
				font-size: 13px;

				&:hover {
					color: #666;
				}
			}

			.new-message-btn {
				color: #fff;
				background: #ec4141;
				border-color: #ec4141;
				font-weight: 500;

				&:hover,
				&:focus {
					color: #fff;
					background: #d9363e;
					border-color: #d9363e;
				}

				&:active {
					color: #fff;
					background: #c92f37;
					border-color: #c92f37;
				}
			}
		}

		.content-body {
			flex: 1;
			display: flex;
			flex-direction: column;
			overflow: hidden;

			.list-content {
				flex: 1;
				overflow-y: auto;

				.message-item {
					display: flex;
					align-items: center;
					padding: 15px 20px;
					border-bottom: 1px solid #f0f0f0;
					cursor: pointer;
					transition: background 0.3s;

					&:hover {
						background: #f5f5f5;
					}

					.message-avatar {
						position: relative;
						margin-right: 12px;
						flex-shrink: 0;

						img {
							width: 45px;
							height: 45px;
							border-radius: 50%;
							object-fit: cover;
						}

						.unread-dot {
							position: absolute;
							top: 0;
							right: 0;
							width: 10px;
							height: 10px;
							background: #ff4d4f;
							border-radius: 50%;
							border: 2px solid #fff;
						}
					}

					.message-info {
						flex: 1;
						overflow: hidden;
						min-width: 0;

						.message-header {
							display: flex;
							align-items: center;
							margin-bottom: 5px;

							.sender-name {
								font-size: 14px;
								font-weight: 500;
								color: #333;
							}

							.action-text {
								font-size: 13px;
								color: #666;
								margin-left: 5px;
							}
						}

						.message-target {
							font-size: 13px;
							color: #333;
							margin-bottom: 4px;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}

						.message-preview {
							font-size: 13px;
							color: #999;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;

							.reply-prefix {
								color: #999;
							}
						}
					}

					.message-meta {
						display: flex;
						flex-direction: column;
						align-items: flex-end;
						gap: 5px;
						flex-shrink: 0;

						.message-time {
							font-size: 12px;
							color: #999;
						}

						.reply-btn-text {
							font-size: 13px;
							color: #409eff;
							cursor: pointer;

							&:hover {
								text-decoration: underline;
							}
						}

						.delete-icon {
							font-size: 16px;
							color: #ccc;
							cursor: pointer;
							opacity: 0;
							transition: opacity 0.3s, color 0.3s;

							&:hover {
								color: #ff4d4f;
							}
						}
					}

					&:hover .delete-icon {
						opacity: 1;
					}
				}
			}

			.empty-content {
				flex: 1;
				display: flex;
				align-items: center;
				justify-content: center;
			}

			// 聊天详情
			.chat-messages {
				flex: 1;
				overflow-y: auto;
				padding: 20px;
				background: #fafafa;
				max-height: 450px;

				.date-divider {
					text-align: center;
					font-size: 12px;
					color: #999;
					margin-bottom: 20px;
				}

				.chat-message {
					display: flex;
					align-items: flex-start;
					margin-bottom: 20px;

					&.self {
						flex-direction: row-reverse;

						.chat-content {
							margin-left: 0;
							margin-right: 12px;
							align-items: flex-end;

							.chat-bubble {
								background: #95ec69;
								border-radius: 12px 12px 2px 12px;
							}
						}
					}

					.chat-avatar {
						width: 40px;
						height: 40px;
						border-radius: 50%;
						object-fit: cover;
						flex-shrink: 0;
					}

					.chat-content {
						display: flex;
						flex-direction: column;
						margin-left: 12px;
						max-width: 70%;

						.chat-bubble {
							background: #fff;
							padding: 10px 15px;
							border-radius: 12px 12px 12px 2px;
							word-break: break-word;
							box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
							font-size: 14px;
							color: #333;
							line-height: 1.5;
						}
					}
				}
			}

			.chat-input-area {
				padding: 15px 20px;
				background: #fff;
				border-top: 1px solid #e0e0e0;
				flex-shrink: 0;

				:deep(.el-textarea__inner) {
					background: #f5f5f5;
					border: none;
					resize: none;
				}

				.input-actions {
					display: flex;
					justify-content: flex-end;
					margin-top: 10px;

					.el-button {
						padding: 8px 20px;
					}
				}
			}
		}
	}
}
</style>
