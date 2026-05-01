<template>
	<div class="user-list">
		<div v-for="(item, index) in data" :key="index" class="user-item">
			<img :src="item.avatarUrl" class="avatar" @click="goToUserHome(item.userId)" />
			<div class="user-info">
				<div class="username" @click="goToUserHome(item.userId)">{{ item.nickname }}</div>
				<div class="signature">{{ item.signature || '暂无签名' }}</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { useRouter } from 'vue-router';
	const router = useRouter();
	defineProps({
		data: {
			type: Array,
			default: () => [],
		},
	});
	const goToUserHome = (userId) => {
		router.push(`/user/home?id=${userId}`);
	};
</script>

<style lang="scss" scoped>
	.user-list {
		display: flex;
		flex-direction: column;
		gap: 16px;

		.user-item {
			display: flex;
			align-items: flex-start;
			width: 100%;
			padding: 12px;
			background: #fff;

			.avatar {
				width: 60px;
				height: 60px;
				border-radius: 8px;
				margin-right: 16px;
				flex-shrink: 0;
				cursor: pointer;
			}

			.user-info {
				flex: 1;
				min-width: 0; /* 允许文本截断 */

				.username {
					font-size: 16px;
					font-weight: 500;
					margin-bottom: 4px;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
					cursor: pointer;
					&:hover {
						color: #409eff;
					}
				}

				.signature {
					font-size: 14px;
					color: #666;
					line-height: 1.4;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}
			}
		}
	}
</style>
