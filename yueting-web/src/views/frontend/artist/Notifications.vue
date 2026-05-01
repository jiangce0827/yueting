<template>
	<div class="artist-notification-page">
		<div class="page-header">
			<div>
				<h1>音乐人通知</h1>
				<p>这里仅展示与你的作品审核、音乐人申请和身份申请相关的结果通知。</p>
			</div>
			<div class="header-actions">
				<el-button @click="loadNotifications" :loading="loading">刷新</el-button>
				<el-button type="primary" plain @click="markAllRead" :disabled="!hasUnread">
					全部已读
				</el-button>
			</div>
		</div>

		<div class="panel-card">
			<div v-if="loading" class="state-block">
				<el-skeleton :rows="6" animated />
			</div>

			<el-empty v-else-if="!notifications.length" description="暂无音乐人通知" />

			<div v-else class="notification-list">
				<button
					v-for="item in notifications"
					:key="item.notificationId"
					type="button"
					class="notification-item"
					:class="{ unread: !item.isRead }"
					@click="openNotification(item)"
				>
					<div class="item-leading">
						<div class="item-icon">
							<el-icon><Bell /></el-icon>
						</div>
						<div class="item-main">
							<div class="item-header">
								<span class="item-type">{{ getTypeLabel(item.type) }}</span>
								<span class="item-time">{{ formatTime(item.createdAt) }}</span>
							</div>
							<div class="item-content">{{ item.content }}</div>
						</div>
					</div>
					<span v-if="!item.isRead" class="item-status">未读</span>
				</button>
			</div>
		</div>
	</div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Bell } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

import {
	apiGetArtistNotificationList,
	apiMarkAllArtistNotificationsRead,
	apiMarkArtistNotificationRead,
} from '@/api/frontend/artistNotification';

const router = useRouter();
const loading = ref(false);
const notifications = ref([]);

const hasUnread = computed(() => notifications.value.some((item) => !item.isRead));

const loadNotifications = async () => {
	loading.value = true;
	try {
		const res = await apiGetArtistNotificationList();
		if (res.data.code === 1) {
			notifications.value = Array.isArray(res.data.data) ? res.data.data : [];
		}
	} catch (error) {
		console.error('加载音乐人通知失败:', error);
		ElMessage.error('加载通知失败，请稍后重试');
	} finally {
		loading.value = false;
	}
};

const markAllRead = async () => {
	try {
		const res = await apiMarkAllArtistNotificationsRead();
		if (res.data.code === 1) {
			notifications.value = notifications.value.map((item) => ({ ...item, isRead: true }));
			ElMessage.success('已将全部音乐人通知标记为已读');
		}
	} catch (error) {
		console.error('标记全部已读失败:', error);
		ElMessage.error('操作失败，请稍后重试');
	}
};

const openNotification = async (item) => {
	if (!item.isRead) {
		try {
			const res = await apiMarkArtistNotificationRead(item.notificationId);
			if (res.data.code === 1) {
				item.isRead = true;
			}
		} catch (error) {
			console.error('标记通知已读失败:', error);
		}
	}

	if (item.targetType === 7) {
		router.push('/musician/artist/workManage?tab=reviews');
		return;
	}
	if (item.targetType === 8) {
		router.push('/musician/artist/apply/info');
		return;
	}
	if (item.targetType === 9) {
		router.push('/musician/artist/apply/role');
	}
};

const getTypeLabel = (type) => {
	const labelMap = {
		9: '歌曲审核通过',
		10: '歌曲审核驳回',
		13: '身份申请通过',
		14: '身份申请驳回',
	};
	return labelMap[type] || '平台通知';
};

const formatTime = (time) => {
	if (!time) return '';
	const date = new Date(time);
	return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

onMounted(() => {
	loadNotifications();
});
</script>

<style lang="scss" scoped>
.artist-notification-page {
	padding: 28px;
	background: linear-gradient(180deg, #fff7ef 0%, #ffffff 260px);
	min-height: 100%;
}

.page-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 16px;
	margin-bottom: 24px;

	h1 {
		margin: 0 0 8px;
		font-size: 28px;
		color: #1f2a37;
	}

	p {
		margin: 0;
		color: #6b7280;
		font-size: 14px;
	}
}

.header-actions {
	display: flex;
	gap: 12px;
}

.panel-card {
	background: rgba(255, 255, 255, 0.92);
	backdrop-filter: blur(8px);
	border: 1px solid #f4e2cf;
	border-radius: 20px;
	box-shadow: 0 16px 40px rgba(15, 23, 42, 0.06);
	padding: 20px;
}

.state-block {
	padding: 12px 0 20px;
}

.notification-list {
	display: flex;
	flex-direction: column;
	gap: 14px;
}

.notification-item {
	width: 100%;
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 16px;
	padding: 18px;
	border-radius: 18px;
	border: 1px solid #eef1f6;
	background: #fff;
	cursor: pointer;
	text-align: left;
	transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;

	&:hover {
		transform: translateY(-1px);
		box-shadow: 0 14px 28px rgba(245, 143, 56, 0.12);
		border-color: #f2b37a;
	}

	&.unread {
		background: linear-gradient(180deg, #fffaf5 0%, #ffffff 100%);
	}
}

.item-leading {
	display: flex;
	align-items: flex-start;
	gap: 14px;
	flex: 1;
	min-width: 0;
}

.item-icon {
	width: 42px;
	height: 42px;
	border-radius: 14px;
	background: linear-gradient(135deg, #ffddb4, #ffb46a);
	display: flex;
	align-items: center;
	justify-content: center;
	color: #7c3900;
	flex-shrink: 0;
}

.item-main {
	flex: 1;
	min-width: 0;
}

.item-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 8px;
}

.item-type {
	font-size: 13px;
	font-weight: 700;
	color: #c46a00;
}

.item-time {
	font-size: 12px;
	color: #94a3b8;
}

.item-content {
	font-size: 14px;
	line-height: 1.7;
	color: #1f2937;
}

.item-status {
	padding: 4px 10px;
	border-radius: 999px;
	background: #ec4141;
	color: #fff;
	font-size: 12px;
	flex-shrink: 0;
}

@media (max-width: 768px) {
	.page-header {
		flex-direction: column;
	}

	.header-actions {
		width: 100%;
	}

	.notification-item,
	.item-header {
		flex-direction: column;
		align-items: flex-start;
	}
}
</style>
