<!-- @/views/frontend/user/MyFollowing.vue -->
<template>
	<div class="user-home-container">
		<!-- 基本信息区域 -->
		<UserProfileSection
			:user-info="authStore.user"
			@go-to-notes="goToMyNotes"
			@go-to-following="goToMyFollowing"
			@go-to-followers="goToMyFollowers"
		/>

		<!-- 关注区域 -->
		<div class="following-section">
			<div class="section-header">
				<h2 class="section-title">我关注的用户</h2>
			</div>
			<div class="following-grid" v-if="followingUsers.length > 0">
				<div v-for="user in followingUsers" :key="user.userId" class="following-item">
					<UserListItem :user-info="user">
						<template #actions>
							<el-button class="message-btn" size="small" @click.stop="goToMessage(user)">
								<el-icon><Message /></el-icon> 发私信
							</el-button>
						</template>
					</UserListItem>
				</div>
			</div>
			<div v-else class="empty-following">还没有关注任何用户</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted } from 'vue';
	import { useRouter } from 'vue-router';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { apiGetFollowingList } from '@/api/frontend/user';
	import { apiGetMyNotes } from '@/api/frontend/note';
	import { Message } from '@element-plus/icons-vue';
	import UserProfileSection from '@/components/frontend/user/UserProfileSection.vue';
	import UserListItem from '@/components/frontend/user/UserListItem.vue';

	const router = useRouter();
	const authStore = useAuthStore();

	const followingUsers = ref([]);
	const noteCount = ref(0);

	// 加载关注列表
	const loadFollowingUsers = async () => {
		try {
			const res = await apiGetFollowingList(authStore.user.userId);
			if (res.data.code === 1) {
				followingUsers.value = res.data.data || [];
			}
		} catch (error) {
			console.error('获取关注列表失败:', error);
		}
	};

	// 加载我的笔记数量
	const loadMyNotesCount = async () => {
		try {
			const res = await apiGetMyNotes();
			if (res.data.code === 1) {
				noteCount.value = res.data.data?.length || 0;
			}
		} catch (error) {
			console.error('获取笔记数量失败:', error);
		}
	};

	// 跳转用户主页
	const goToUserHome = (userId) => {
		router.push(`/user/home?id=${userId}`);
	};

	// 跳转我的笔记页面
	const goToMyNotes = () => {
		router.push('/my-notes');
	};

	// 跳转我的关注页面
	const goToMyFollowing = () => {
		router.push('/my-following');
	};

	// 跳转我的粉丝页面
	const goToMyFollowers = () => {
		router.push('/my-followers');
	};

	// 跳转私信页面
	const goToMessage = (user) => {
		router.push(`/user/message?userId=${user.userId}&nickname=${encodeURIComponent(user.nickname)}`);
	};

	onMounted(() => {
		loadFollowingUsers();
		loadMyNotesCount();
	});
</script>

<style lang="scss" scoped>
	.user-home-container {
		width: 950px;
		margin: 0 auto;
		background: white;

		.following-section {
			background: white;
			padding: 20px 30px;
			margin-top: 20px;
			box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

			.section-header {
				margin-bottom: 20px;

				.section-title {
					font-size: 18px;
					color: #333;
					margin: 0;
				}
			}

			.following-grid {
				display: grid;
				grid-template-columns: repeat(2, 1fr);
				gap: 16px;

				.following-item {
					:deep(.user-list-item) {
						border: none;
						padding: 0;

						&:hover {
							background: transparent;
							border-color: transparent;
						}
					}

					.message-btn {
						margin-left: 12px;
						flex-shrink: 0;
					}
				}
			}
		}

		.empty-following {
			text-align: center;
			padding: 60px 0;
			color: #999;
			font-size: 14px;
		}
	}
</style>
