<template>
	<div class="privacy-settings">
		<!-- 私信设置 -->
		<!-- <div class="setting-section">
			<div class="setting-header">
				<div class="setting-title">私信</div>
				<div class="setting-description">接收私信提醒</div>
			</div>
			<el-radio-group v-model="form.privacyAllowMessages" class="radio-group">
				<el-radio :value="0" class="radio-item">所有人</el-radio>
				<el-radio :value="1" class="radio-item">我关注的人</el-radio>
			</el-radio-group>
		</div> -->

		<!-- 分割线 -->
		<!-- <el-divider /> -->

		<!-- 听歌排行设置 -->
		<div class="setting-section">
			<div class="setting-header">
				<div class="setting-title">听歌排行</div>
			</div>
			<el-radio-group v-model="form.privacyShowListeningHabits" class="radio-group">
				<el-radio :value="0" class="radio-item">所有人可见</el-radio>
				<el-radio :value="1" class="radio-item">仅自己可见</el-radio>
			</el-radio-group>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, watch } from 'vue';
	import { useAuthStore } from '@/stores/frontend/auth';
    import { apiUserPrivacyUpdate } from '@/api/frontend/user';
	import { ElMessage } from 'element-plus';

	const authStore = useAuthStore();

	// 从 store 中获取隐私设置
	const privacySettings = computed(() => ({
		privacyAllowMessages: authStore.user?.privacyAllowMessages || 0,
		privacyShowListeningHabits: authStore.user?.privacyShowListeningHabits || 0,
	}));

	// 本地表单数据
	const form = ref({
		privacyAllowMessages: privacySettings.value.privacyAllowMessages,
		privacyShowListeningHabits: privacySettings.value.privacyShowListeningHabits,
	});

	// 自动保存设置
	watch(
		form,
		async (newVal) => {
			try {
                const res = await apiUserPrivacyUpdate(newVal);
				await authStore.updateUser(res.data.data);                
				ElMessage.success('设置已保存');
			} catch (error) {
				ElMessage.error('保存失败，请重试');
			}
		},
		{ deep: true }
	);
</script>

<style scoped lang="scss">
	$primary-color: #333;
	$secondary-color: #999;
	$spacing: 8px;

	.privacy-settings {
		padding: 20px;

		.setting-section {
			margin-bottom: 30px;

			.setting-header {
				display: flex;
				align-items: center;
				gap: 10px;
				margin-bottom: 10px;

				.setting-title {
					font-size: 16px;
					color: $primary-color;
				}

				.setting-description {
					font-size: 12px;
					color: $secondary-color;
				}
			}

			.radio-group {
				margin-top: 10px;

				::v-deep(.radio-item) {
					display: block;
					width: 100%;
					padding: $spacing 12px;
					margin-bottom: $spacing;

					&:last-child {
						margin-bottom: 0;
					}

					.el-radio__label {
						font-size: 14px;
					}

					.el-radio__input {
						margin-right: $spacing;
					}
				}
			}
		}

		.el-divider--horizontal {
			margin: 20px 0;
		}
	}
</style>
