<template>
	<div class="nav-bar">
		<div class="nav-container">
			<div class="log-section" @click="navigate('/')">
				<img src="@/assets/logo.png" class="logo" />
				<span class="brand-name">悦听音乐</span>
			</div>

			<div class="nav-items">
				<router-link v-for="item in navItems" :key="item.path" :to="item.path" class="nav-item">
					{{ item.title }}
				</router-link>
			</div>

			<div class="right-section">
				<template v-if="authStore.isLoggedIn">
					<el-popover
						placement="bottom-end"
						trigger="click"
						width="360"
						popper-class="artist-notification-popover"
						@show="loadNotifications"
					>
						<template #reference>
							<button class="notification-trigger" type="button">
								<el-badge :value="badgeValue" :hidden="!unreadCount" :max="99">
									<el-icon :size="22"><Bell /></el-icon>
								</el-badge>
								<span class="trigger-text">通知</span>
							</button>
						</template>

						<div class="notification-panel">
							<div class="panel-header">
								<div>
									<div class="panel-title">音乐人通知</div>
									<div class="panel-subtitle">仅展示审核与平台处理结果</div>
								</div>
								<el-button
									v-if="notifications.length && unreadCount"
									link
									type="primary"
									@click="markAllRead"
								>
									全部已读
								</el-button>
							</div>

							<div v-if="loadingNotifications" class="panel-loading">
								<el-skeleton :rows="4" animated />
							</div>

							<div v-else-if="notifications.length" class="notification-list">
								<button
									v-for="item in notifications.slice(0, 6)"
									:key="item.notificationId"
									type="button"
									class="notification-item"
									:class="{ unread: !item.isRead }"
									@click="openNotification(item)"
								>
									<div class="item-icon">
										<el-icon><Bell /></el-icon>
									</div>
									<div class="item-content">
										<div class="item-text">{{ item.content }}</div>
										<div class="item-meta">
											<span>{{ getTypeLabel(item.type) }}</span>
											<span>{{ formatTime(item.createdAt) }}</span>
										</div>
									</div>
									<span v-if="!item.isRead" class="unread-dot"></span>
								</button>
							</div>

							<el-empty v-else description="暂无音乐人通知" :image-size="86" />

							<div class="panel-footer">
								<el-button text @click="goToNotificationCenter">查看全部通知</el-button>
							</div>
						</div>
					</el-popover>

					<div
						class="avatar-container"
						@mouseenter="showDropdown = true"
						@mouseleave="showDropdown = false"
					>
						<img :src="authStore.user.avatarUrl" alt="用户头像" class="avatar" />
						<div v-show="showDropdown" class="dropdown-menu">
							<div class="menu-item" @click="navigate(`/user/home?id=${authStore.user.userId}`)">我的主页</div>
							<div class="menu-item" @click="navigate('/user/setting')">个人设置</div>
							<div class="menu-item" @click="logout">退出</div>
						</div>
					</div>
				</template>
				<template v-else>
					<router-link to="/login" class="login-link">登录</router-link>
				</template>
			</div>
		</div>
	</div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Bell } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

import { useAuthStore } from '@/stores/frontend/auth';
import {
	apiGetArtistNotificationList,
	apiGetArtistNotificationUnreadCount,
	apiMarkAllArtistNotificationsRead,
	apiMarkArtistNotificationRead,
} from '@/api/frontend/artistNotification';

const router = useRouter();
const authStore = useAuthStore();

const showDropdown = ref(false);
const loadingNotifications = ref(false);
const notifications = ref([]);
const unreadCount = ref(0);
let refreshTimer = null;

const navItems = ref([{ title: '音乐人', path: '/musician/artist' }]);

const badgeValue = computed(() => (unreadCount.value > 99 ? '99+' : unreadCount.value));

const navigate = (path) => {
	router.push(path);
};

const logout = async () => {
	stopAutoRefresh();
	await router.push('/');
	await authStore.logout();
};

const loadUnreadCount = async () => {
	if (!authStore.isLoggedIn || !authStore.isArtist) {
		return;
	}
	try {
		const res = await apiGetArtistNotificationUnreadCount();
		if (res.data.code === 1) {
			unreadCount.value = Number(res.data.data || 0);
		}
	} catch (error) {
		console.error('加载音乐人通知未读数失败:', error);
	}
};

const loadNotifications = async () => {
	if (!authStore.isLoggedIn || !authStore.isArtist) {
		return;
	}
	loadingNotifications.value = true;
	try {
		const res = await apiGetArtistNotificationList();
		if (res.data.code === 1) {
			notifications.value = Array.isArray(res.data.data) ? res.data.data : [];
		}
		await loadUnreadCount();
	} catch (error) {
		console.error('加载音乐人通知失败:', error);
	} finally {
		loadingNotifications.value = false;
	}
};

const markAllRead = async () => {
	try {
		const res = await apiMarkAllArtistNotificationsRead();
		if (res.data.code === 1) {
			notifications.value = notifications.value.map((item) => ({ ...item, isRead: true }));
			unreadCount.value = 0;
			ElMessage.success('已将音乐人通知全部标记为已读');
		}
	} catch (error) {
		console.error('标记音乐人通知已读失败:', error);
		ElMessage.error('标记已读失败，请稍后重试');
	}
};

const openNotification = async (item) => {
	if (!item.isRead) {
		try {
			const res = await apiMarkArtistNotificationRead(item.notificationId);
			if (res.data.code === 1) {
				item.isRead = true;
				unreadCount.value = Math.max(0, unreadCount.value - 1);
			}
		} catch (error) {
			console.error('标记音乐人通知已读失败:', error);
		}
	}
	handleNotificationJump(item);
};

const goToNotificationCenter = () => {
	router.push('/musician/artist/notifications');
};

const handleNotificationJump = (item) => {
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
		9: '歌曲审核',
		10: '歌曲审核',
		11: '音乐人申请',
		12: '音乐人申请',
		13: '身份申请',
		14: '身份申请',
	};
	return labelMap[type] || '平台通知';
};

const formatTime = (time) => {
	if (!time) return '';
	const date = new Date(time);
	const now = new Date();
	const diff = now - date;

	if (diff < 60 * 1000) return '刚刚';
	if (diff < 60 * 60 * 1000) return `${Math.floor(diff / (60 * 1000))}分钟前`;
	if (diff < 24 * 60 * 60 * 1000) return `${Math.floor(diff / (60 * 60 * 1000))}小时前`;
	return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

const startAutoRefresh = () => {
	stopAutoRefresh();
	refreshTimer = window.setInterval(() => {
		loadUnreadCount();
	}, 60 * 1000);
};

const stopAutoRefresh = () => {
	if (refreshTimer) {
		window.clearInterval(refreshTimer);
		refreshTimer = null;
	}
};

onMounted(() => {
	loadUnreadCount();
	startAutoRefresh();
});

onUnmounted(() => {
	stopAutoRefresh();
});
</script>

<style lang="scss" scoped>
.nav-bar {
	height: 70px;
	background: #242424;
	display: flex;
	justify-content: center;
	min-width: 950px;

	.nav-container {
		width: 100%;
		padding: 0 50px;
		height: 100%;
		margin: 0 auto;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.log-section {
		height: 100%;
		display: flex;
		align-items: center;
		cursor: pointer;

		.logo {
			width: 45px;
			height: 45px;
			border-radius: 50%;
		}

		.brand-name {
			font-size: 24px;
			margin-left: 15px;
			color: #fff;
		}
	}

	.nav-items {
		display: flex;
		height: 100%;
		padding: 0 20px;

		.nav-item {
			position: relative;
			height: 100%;
			padding: 0 30px;
			display: flex;
			align-items: center;
			color: #fff;
			text-decoration: none;
			font-size: 16px;
			transition: background-color 0.3s;

			&::after {
				content: '';
				position: absolute;
				bottom: 3px;
				left: 50%;
				width: 0;
				height: 3px;
				background: #409eff;
				transform: translateX(-50%);
				transition: width 0.3s;
				border-radius: 2px;
			}

			&:hover,
			&.router-link-exact-active {
				background-color: #000;

				&::after {
					width: 60%;
				}
			}
		}
	}

	.right-section {
		margin-left: auto;
		display: flex;
		align-items: center;
		gap: 18px;
	}

	.login-link {
		font-size: 14px;
		color: #fff;
		padding: 8px 16px;
		border-radius: 4px;
		text-decoration: none;

		&:hover {
			color: #b3b3b3;
		}
	}

	.notification-trigger {
		display: flex;
		align-items: center;
		gap: 8px;
		border: none;
		background: transparent;
		color: #fff;
		cursor: pointer;
		padding: 6px 10px;
		border-radius: 999px;
		transition: background 0.25s ease, color 0.25s ease;

		&:hover {
			background: rgba(255, 255, 255, 0.1);
		}
	}

	.trigger-text {
		font-size: 14px;
	}

	.avatar-container {
		position: relative;

		.avatar {
			width: 45px;
			height: 45px;
			border-radius: 50%;
			cursor: pointer;
		}

		.dropdown-menu {
			position: absolute;
			top: 100%;
			left: 50%;
			transform: translateX(-50%);
			background: #2a2a2a;
			border-radius: 4px;
			box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
			width: 120px;
			z-index: 1000;

			.menu-item {
				padding: 12px 16px;
				color: #fff;
				font-size: 14px;
				cursor: pointer;
				transition: background 0.3s;

				&:hover {
					background: #000;
				}
			}
		}
	}
}

:deep(.artist-notification-popover) {
	padding: 0;
	border-radius: 18px;
	overflow: hidden;
}

.notification-panel {
	padding: 18px 18px 12px;
	background: linear-gradient(180deg, #fff7ef 0%, #ffffff 30%);
}

.panel-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 14px;
}

.panel-title {
	font-size: 16px;
	font-weight: 700;
	color: #1f2937;
}

.panel-subtitle {
	font-size: 12px;
	color: #8b95a7;
	margin-top: 4px;
}

.panel-loading {
	padding: 8px 0;
}

.notification-list {
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.notification-item {
	width: 100%;
	display: flex;
	align-items: flex-start;
	gap: 12px;
	padding: 12px;
	border: 1px solid #edf0f5;
	border-radius: 14px;
	background: #fff;
	cursor: pointer;
	text-align: left;
	position: relative;
	transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;

	&:hover {
		border-color: #f2b37a;
		box-shadow: 0 10px 24px rgba(245, 143, 56, 0.12);
		transform: translateY(-1px);
	}

	&.unread {
		background: #fffaf5;
	}
}

.item-icon {
	width: 34px;
	height: 34px;
	border-radius: 12px;
	background: linear-gradient(135deg, #ffddb4, #ffb46a);
	display: flex;
	align-items: center;
	justify-content: center;
	color: #7c3900;
	flex-shrink: 0;
}

.item-content {
	flex: 1;
	min-width: 0;
}

.item-text {
	font-size: 13px;
	line-height: 1.6;
	color: #1f2937;
}

.item-meta {
	margin-top: 6px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	font-size: 12px;
	color: #8b95a7;
}

.unread-dot {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background: #ec4141;
	flex-shrink: 0;
	margin-top: 6px;
}

.panel-footer {
	display: flex;
	justify-content: center;
	padding-top: 10px;
}
</style>
