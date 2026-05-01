<!-- 用户列表项组件 - 带操作按钮 -->
<template>
	<div class="user-list-item">
		<img :src="userInfo.avatarUrl || defaultAvatar" class="user-avatar" @click="$emit('click-user', userInfo.userId)" />
		<div class="user-info" @click="$emit('click-user', userInfo.userId)">
			<div class="user-name">{{ userInfo.nickname }}</div>
			<div class="user-stats">
				<span>笔记 {{ userInfo.noteCount || 0 }}</span>
				<span class="separator">|</span>
				<span>关注 {{ userInfo.followingCount || 0 }}</span>
				<span class="separator">|</span>
				<span>粉丝 {{ userInfo.followerCount || 0 }}</span>
			</div>
			<div class="user-signature">{{ userInfo.signature || defaultSignature }}</div>
		</div>
		<slot name="actions"></slot>
	</div>
</template>

<script setup>
	defineProps({
		userInfo: {
			type: Object,
			required: true,
		},
		defaultAvatar: {
			type: String,
			default: 'https://cube.elemecdn.com/3/7c/4ea1f28e1d89defd2b7bdbDb3b1acpng.png',
		},
		defaultSignature: {
			type: String,
			default: '这个人很懒，什么都没有留下~',
		},
	});

	defineEmits(['click-user']);
</script>

<style lang="scss" scoped>
	.user-list-item {
		display: flex;
		align-items: center;
		padding: 16px;
		border: 1px solid #eee;
		border-radius: 8px;
		cursor: pointer;
		transition: all 0.3s;

		&:hover {
			background: #fafafa;
			border-color: #ddd;
		}

		.user-avatar {
			width: 60px;
			height: 60px;
			border-radius: 50%;
			margin-right: 12px;
			flex-shrink: 0;
		}

		.user-info {
			flex: 1;
			min-width: 0;

			.user-name {
				font-size: 15px;
				font-weight: bold;
				color: #333;
				margin-bottom: 4px;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.user-stats {
				font-size: 12px;
				color: #666;
				margin-bottom: 4px;

				.separator {
					margin: 0 6px;
					color: #ddd;
				}
			}

			.user-signature {
				font-size: 12px;
				color: #999;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}
		}
	}
</style>
