<!-- @/views/admin/system/Dashboard.vue -->
<template>
	<div class="dashboard-container">
		<!-- 员工信息卡片 -->
		<div class="profile-section">
			<div class="profile-card">
				<div class="profile-left">
					<div class="avatar-wrapper">
						<el-icon :size="40"><User /></el-icon>
					</div>
					<div class="profile-info">
						<h2 class="real-name">{{ employeeInfo.realName }}</h2>
						<div class="roles">
							<el-tag v-for="roleName in employeeInfo.roleNames" :key="roleName" type="info" size="small" class="role-tag">
								{{ roleName }}
							</el-tag>
						</div>
					</div>
				</div>
				<div class="profile-right">
					<div class="info-item">
						<span class="label">账号</span>
						<span class="value">{{ employeeInfo.username }}</span>
					</div>
					<div class="info-item">
						<span class="label">状态</span>
						<el-tag :type="employeeInfo.isActive === 1 ? 'success' : 'danger'" size="small">
							{{ employeeInfo.isActive === 1 ? '启用' : '禁用' }}
						</el-tag>
					</div>
					<div class="info-item">
						<span class="label">创建时间</span>
						<span class="value">{{ employeeInfo.createdAt }}</span>
					</div>
					<div class="info-item">
						<span class="label">最后登录</span>
						<span class="value">{{ employeeInfo.lastLoginAt || '暂无' }}</span>
					</div>
				</div>
			</div>
		</div>

		<!-- 统计卡片 -->
		<div class="stats-grid">
			<div class="stat-card">
				<div class="stat-icon user-icon">
					<el-icon :size="30"><User /></el-icon>
				</div>
				<div class="stat-info">
					<span class="stat-value">{{ stats.userCount }}</span>
					<span class="stat-label">用户总数</span>
				</div>
			</div>
			<div class="stat-card">
				<div class="stat-icon artist-icon">
					<el-icon :size="30"><UserFilled /></el-icon>
				</div>
				<div class="stat-info">
					<span class="stat-value">{{ stats.artistCount }}</span>
					<span class="stat-label">艺人总数</span>
				</div>
			</div>
			<div class="stat-card">
				<div class="stat-icon song-icon">
					<el-icon :size="30"><VideoPlay /></el-icon>
				</div>
				<div class="stat-info">
					<span class="stat-value">{{ stats.songCount }}</span>
					<span class="stat-label">歌曲总数</span>
				</div>
			</div>
			<div class="stat-card">
				<div class="stat-icon playlist-icon">
					<el-icon :size="30"><List /></el-icon>
				</div>
				<div class="stat-info">
					<span class="stat-value">{{ stats.playlistCount }}</span>
					<span class="stat-label">歌单总数</span>
				</div>
			</div>
		</div>

		<!-- 待处理事项 -->
		<div class="pending-section" v-if="hasPendingItems">
			<h2 class="section-title">待处理事项</h2>
			<div class="pending-grid">
				<div class="pending-card" v-if="hasMenu('/admin/song-review')" @click="goToPage('/admin/song-review')">
					<div class="pending-icon song-icon">
						<el-icon :size="24"><DocumentChecked /></el-icon>
					</div>
					<div class="pending-info">
						<span class="pending-value">{{ stats.pendingSongReview }}</span>
						<span class="pending-label">歌曲待审批</span>
					</div>
				</div>
				<div class="pending-card" v-if="hasMenu('/admin/artist-review')" @click="goToPage('/admin/artist-review')">
					<div class="pending-icon artist-icon">
						<el-icon :size="24"><Stamp /></el-icon>
					</div>
					<div class="pending-info">
						<span class="pending-value">{{ stats.pendingArtistReview }}</span>
						<span class="pending-label">艺人待审批</span>
					</div>
				</div>
				<div class="pending-card" v-if="hasMenu('/admin/artistIdentity-review')" @click="goToPage('/admin/artistIdentity-review')">
					<div class="pending-icon identity-icon">
						<el-icon :size="24"><Medal /></el-icon>
					</div>
					<div class="pending-info">
						<span class="pending-value">{{ stats.pendingIdentityReview }}</span>
						<span class="pending-label">身份待审批</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, onMounted } from 'vue';
	import { useRouter } from 'vue-router';
	import { useAdminStore } from '@/stores/admin/admin';
	import { User, UserFilled, VideoPlay, List, DocumentChecked, Stamp, Medal } from '@element-plus/icons-vue';
	import { apiGetDashboardStats } from '@/api/admin/dashboard';
	import { apiEmployeeDetail } from '@/api/admin/employee';

	const router = useRouter();
	const adminStore = useAdminStore();
	const stats = ref({
		userCount: 0,
		artistCount: 0,
		songCount: 0,
		playlistCount: 0,
		pendingSongReview: 0,
		pendingArtistReview: 0,
		pendingIdentityReview: 0,
	});

	const employeeInfo = ref({
		realName: '',
		username: '',
		isActive: 1,
		createdAt: '',
		lastLoginAt: '',
		roleNames: [],
	});

	// 检查用户是否有指定菜单权限
	const hasMenu = (path) => {
		const menus = adminStore.menus || [];
		const checkMenu = (menuList) => {
			for (const menu of menuList) {
				if (menu.path === path) return true;
				if (menu.children && menu.children.length > 0) {
					if (checkMenu(menu.children)) return true;
				}
			}
			return false;
		};
		return checkMenu(menus);
	};

	// 是否有待处理事项（至少有一个菜单权限）
	const hasPendingItems = computed(() => {
		return hasMenu('/admin/song-review') || hasMenu('/admin/artist-review') || hasMenu('/admin/artistIdentity-review');
	});

	const goToPage = (path) => {
		router.push(path);
	};

	const loadStats = async () => {
		try {
			const res = await apiGetDashboardStats();
			if (res.data.code === 1) {
				stats.value = res.data.data;
			}
		} catch (error) {
			console.error('获取统计数据失败:', error);
		}
	};

	const loadEmployeeInfo = async () => {
		try {
			const res = await apiEmployeeDetail(adminStore.emp.employeeId);
			if (res.data.code === 1) {
				employeeInfo.value = res.data.data;
			}
		} catch (error) {
			console.error('获取员工信息失败:', error);
		}
	};

	onMounted(() => {
		loadStats();
		loadEmployeeInfo();
	});
</script>

<style scoped>
	.dashboard-container {
		padding: 20px;

		.profile-section {
			margin-bottom: 20px;

			.profile-card {
				background: white;
				border-radius: 8px;
				padding: 20px 30px;
				display: flex;
				justify-content: space-between;
				align-items: center;
				box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

				.profile-left {
					display: flex;
					align-items: center;

					.avatar-wrapper {
						width: 70px;
						height: 70px;
						border-radius: 50%;
						background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
						display: flex;
						align-items: center;
						justify-content: center;
						color: white;
						margin-right: 20px;
					}

					.profile-info {
						.real-name {
							font-size: 20px;
							color: #333;
							margin: 0 0 8px 0;
						}

						.roles {
							display: flex;
							gap: 8px;

							.role-tag {
								border-radius: 12px;
							}
						}
					}
				}

				.profile-right {
					display: flex;
					gap: 40px;

					.info-item {
						display: flex;
						flex-direction: column;
						align-items: center;

						.label {
							font-size: 12px;
							color: #999;
							margin-bottom: 4px;
						}

						.value {
							font-size: 14px;
							color: #333;
						}
					}
				}
			}
		}

		.stats-grid {
			display: grid;
			grid-template-columns: repeat(4, 1fr);
			gap: 20px;
			margin-bottom: 20px;

			.stat-card {
				background: white;
				border-radius: 8px;
				padding: 20px;
				display: flex;
				align-items: center;
				box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

				.stat-icon {
					width: 60px;
					height: 60px;
					border-radius: 8px;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 16px;
					color: white;

					&.user-icon {
						background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
					}

					&.artist-icon {
						background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
					}

					&.song-icon {
						background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
					}

					&.playlist-icon {
						background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
					}
				}

				.stat-info {
					display: flex;
					flex-direction: column;

					.stat-value {
						font-size: 28px;
						font-weight: bold;
						color: #333;
					}

					.stat-label {
						font-size: 14px;
						color: #999;
					}
				}
			}
		}

		.pending-section {
			background: white;
			border-radius: 8px;
			padding: 20px;
			box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

			.section-title {
				font-size: 16px;
				color: #333;
				margin: 0 0 16px 0;
			}

			.pending-grid {
				display: grid;
				grid-template-columns: repeat(3, 1fr);
				gap: 20px;

				.pending-card {
					display: flex;
					align-items: center;
					padding: 16px;
					border: 1px solid #eee;
					border-radius: 8px;
					cursor: pointer;
					transition: all 0.3s;

					&:hover {
						border-color: #409eff;
						box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
					}

					.pending-icon {
						width: 48px;
						height: 48px;
						border-radius: 8px;
						display: flex;
						align-items: center;
						justify-content: center;
						margin-right: 12px;
						color: white;

						&.song-icon {
							background: #409eff;
						}

						&.artist-icon {
							background: #67c23a;
						}

						&.identity-icon {
							background: #e6a23c;
						}
					}

					.pending-info {
						display: flex;
						flex-direction: column;

						.pending-value {
							font-size: 24px;
							font-weight: bold;
							color: #333;
						}

						.pending-label {
							font-size: 14px;
							color: #999;
						}
					}
				}
			}
		}
	}

	@media (max-width: 1200px) {
		.stats-grid {
			grid-template-columns: repeat(2, 1fr);
		}
	}

	@media (max-width: 768px) {
		.stats-grid {
			grid-template-columns: 1fr;
		}

		.pending-grid {
			grid-template-columns: 1fr;
		}

		.profile-section {
			.profile-card {
				flex-direction: column;
				gap: 20px;

				.profile-right {
					gap: 20px;
				}
			}
		}
	}
</style>