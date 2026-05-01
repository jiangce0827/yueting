<!-- 侧边栏用户卡片组件 -->
<template>
	<div class="user-profile-card">
		<img :src="userInfo.avatarUrl || defaultAvatar" class="profile-avatar" />
		<div class="profile-nickname">{{ userInfo.nickname }}</div>
		<div class="profile-signature">{{ userInfo.signature || '暂无签名' }}</div>
		<div class="profile-stats">
			<div class="stat-item clickable" @click="$emit('go-to-notes')">
				<div class="stat-value">{{ stats.noteCount }}</div>
				<div class="stat-label">笔记</div>
			</div>
			<div class="stat-item clickable" @click="$emit('go-to-following')">
				<div class="stat-value">{{ stats.followingCount }}</div>
				<div class="stat-label">关注</div>
			</div>
			<div class="stat-item clickable" @click="$emit('go-to-followers')">
				<div class="stat-value">{{ stats.followerCount }}</div>
				<div class="stat-label">粉丝</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	defineProps({
		userInfo: {
			type: Object,
			required: true,
		},
		stats: {
			type: Object,
			default: () => ({
				noteCount: 0,
				followingCount: 0,
				followerCount: 0,
			}),
		},
		defaultAvatar: {
			type: String,
			default: 'https://cube.elemecdn.com/3/7c/4ea1f28e1d89defd2b7bdbDb3b1acpng.png',
		},
	});

	defineEmits(['go-to-notes', 'go-to-following', 'go-to-followers']);
</script>

<style lang="scss" scoped>
	.user-profile-card {
		background: #fff;
		border-radius: 8px;
		padding: 20px;
		text-align: center;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
		margin-bottom: 16px;

		.profile-avatar {
			width: 80px;
			height: 80px;
			border-radius: 50%;
			margin-bottom: 12px;
		}

		.profile-nickname {
			font-size: 18px;
			font-weight: bold;
			color: #333;
			margin-bottom: 8px;
		}

		.profile-signature {
			font-size: 14px;
			color: #999;
			margin-bottom: 16px;
		}

		.profile-stats {
			display: flex;
			justify-content: space-around;

			.stat-item {
				.stat-value {
					font-size: 18px;
					font-weight: bold;
					color: #333;
				}

				.stat-label {
					font-size: 12px;
					color: #999;
				}

				&.clickable {
					cursor: pointer;
					transition: opacity 0.2s;

					&:hover {
						opacity: 0.7;

						.stat-value {
							color: #409eff;
						}
					}
				}
			}
		}
	}
</style>
