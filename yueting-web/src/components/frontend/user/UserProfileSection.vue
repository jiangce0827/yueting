<!-- 完整个人信息区域组件 - 用于用户主页 -->
<template>
	<div class="profile-section">
		<div class="basic-info">
			<!-- 左侧头像 -->
			<img :src="userInfo.avatarUrl || defaultAvatar" class="user-avatar" />

			<!-- 右侧信息 -->
			<div class="user-details">
				<!-- 第一行：昵称 + 操作按钮 -->
				<div class="name-section">
					<h1 class="nickname">
						{{ userInfo.nickname }}
						<span v-if="userInfo.gender" class="gender-icon">
							<i v-if="userInfo.gender === 1" class="icon-male">♂</i>
							<i v-if="userInfo.gender === 2" class="icon-female">♀</i>
						</span>
					</h1>
					<div class="button-group">
						<slot name="actions"></slot>
					</div>
				</div>
				<div class="divider"></div>

				<!-- 第二行：数据统计 -->
				<div class="stats-section">
					<div class="stat-item clickable" @click="$emit('go-to-notes')">
						<span class="stat-number">{{ userInfo.noteCount || 0 }}</span>
						<span class="stat-label">笔记</span>
					</div>
					<div class="stat-divider">|</div>
					<div class="stat-item clickable" @click="$emit('go-to-following')">
						<span class="stat-number">{{ userInfo.followingCount || 0 }}</span>
						<span class="stat-label">关注</span>
					</div>
					<div class="stat-divider">|</div>
					<div class="stat-item clickable" @click="$emit('go-to-followers')">
						<span class="stat-number">{{ userInfo.followerCount || 0 }}</span>
						<span class="stat-label">粉丝</span>
					</div>
				</div>

				<!-- 第三行：年龄 -->
				<div class="age-section">年龄：{{ ageGroup }}</div>

				<!-- 第四行：签名 -->
				<div v-if="userInfo.signature" class="signature-section">
					个人简介: {{ userInfo.signature || defaultSignature }}
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { computed } from 'vue';

	const props = defineProps({
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

	defineEmits(['go-to-notes', 'go-to-following', 'go-to-followers']);

	// 计算年龄分组
	const ageGroup = computed(() => {
		if (!props.userInfo.birthday) return '未知';
		const birthYear = new Date(props.userInfo.birthday).getFullYear();
		const decade = Math.floor((birthYear % 100) / 10) * 10;
		return `${decade}后`;
	});
</script>

<style lang="scss" scoped>
	.profile-section {
		background: white;
		padding: 30px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

		.basic-info {
			display: flex;
			gap: 40px;

			.user-avatar {
				width: 200px;
				height: 200px;
				border: 2px solid #eee;
			}

			.user-details {
				flex: 1;

				.name-section {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 10px;

					.nickname {
						font-size: 25px;
						margin: 0;

						.gender-icon {
							display: inline-flex;

							i {
								font-style: normal;
								font-size: 20px;
								padding: 2px;
								border-radius: 50%;
								line-height: 1;
								display: inline-flex;
								align-items: center;
								justify-content: center;
							}

							.icon-male {
								color: #3b87f5;
								background-color: rgba(59, 135, 245, 0.1);
							}

							.icon-female {
								color: #ff4081;
								background-color: rgba(255, 64, 129, 0.1);
							}
						}
					}

					.button-group {
						display: flex;
						gap: 10px;
					}
				}

				.divider {
					height: 1px;
					background: #eee;
					margin: 10px 0;
				}

				.stats-section {
					display: flex;
					align-items: center;
					gap: 30px;
					margin-bottom: 10px;

					.stat-item {
						display: flex;
						flex-direction: column;
						align-items: center;

						.stat-number {
							font-size: 20px;
							font-weight: bold;
							color: #333;
						}

						.stat-label {
							font-size: 14px;
							color: #666;
						}

						&.clickable {
							cursor: pointer;
							transition: opacity 0.2s;

							&:hover {
								opacity: 0.7;

								.stat-number {
									color: #409eff;
								}
							}
						}
					}

					.stat-divider {
						color: #ddd;
						margin: 0 -15px;
					}
				}

				.age-section {
					color: #666;
					font-size: 14px;
					margin-bottom: 10px;
				}

				.signature-section {
					color: #444;
					font-size: 14px;
					line-height: 1.6;
					border-radius: 4px;
				}
			}
		}
	}
</style>
