<!-- @/views/frontend/user/settings/basic.vue -->
<template>
	<div class="settings-container">
		<div class="settings-content" v-if="!avatarUploadVisible">
			<!-- 左侧表单区域 -->
			<div class="form-section">
				<el-form :model="formData" label-width="40px">
					<!-- 昵称 -->
					<el-form-item label="昵称">
						<el-input v-model="formData.nickname" maxlength="20" show-word-limit />
					</el-form-item>

					<!-- 简介 -->
					<el-form-item label="简介">
						<el-input
							v-model="formData.signature"
							type="textarea"
							:rows="6"
							maxlength="100"
							show-word-limit
						/>
					</el-form-item>

					<!-- 性别 -->
					<el-form-item label="性别">
						<el-radio-group v-model="formData.gender">
							<el-radio :value="0">保密</el-radio>
							<el-radio :value="1">男</el-radio>
							<el-radio :value="2">女</el-radio>
						</el-radio-group>
					</el-form-item>

					<!-- 生日 -->
					<div class="birthday-picker">
						<div class="label">生日：</div>
						<div class="selectors">
							<!-- 年份选择 -->
							<el-select v-model="selectedYear" placeholder="选择年份" @change="updateDays">
								<el-option v-for="year in years" :key="year" :label="`${year}年`" :value="year" />
							</el-select>

							<!-- 月份选择 -->
							<el-select v-model="selectedMonth" placeholder="选择月份" @change="updateDays">
								<el-option v-for="month in 12" :key="month" :label="`${month}月`" :value="month" />
							</el-select>

							<!-- 日期选择 -->
							<el-select v-model="selectedDay" placeholder="选择日期">
								<el-option v-for="day in daysInMonth" :key="day" :label="`${day}日`" :value="day" />
							</el-select>
						</div>
					</div>
					<!-- 保存按钮 -->
					<el-form-item class="save-button">
						<el-button type="primary" @click="handleSave" :loading="isSaving"> 保存 </el-button>
					</el-form-item>
				</el-form>
			</div>

			<!-- 右侧头像区域 -->
			<div class="avatar-section">
				<div class="avatar-wrapper">
					<img :src="authStore.user.avatarUrl" class="user-avatar" />
					<button @click="avatarUploadVisible = true" class="change-avatar-btn">修改头像</button>
				</div>
			</div>
		</div>
		<!-- 头像上传区域（当avatarUploadVisible为true时显示） -->
		<div v-else class="avatar-upload-content">
			<div class="header"></div>
			<ImageCropper
				:current-avatar="authStore.user.avatarUrl"
				@onSuccess="handleAvatarSuccess"
				@close="avatarUploadVisible = false"
			/>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, onMounted, watch } from 'vue';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { ElMessage } from 'element-plus';
	import { apiUserBasicUpdate } from '@/api/frontend/user';
	import ImageCropper from '@/components/frontend/ImageCropper.vue';
	import { apiUpdateAvatarUrl } from '@/api/frontend/user';

	const authStore = useAuthStore();
	const avatarUploadVisible = ref(false); // 控制上传弹窗的显示与隐藏
	const isSaving = ref(false);

	// 表单数据初始化
	const formData = ref({
		nickname: '',
		signature: '',
		gender: 0,
		birthday: '',
	});

	// 从store初始化表单数据
	onMounted(() => {
		const user = authStore.user;
		
		formData.value = {
			nickname: user.nickname || '',
			signature: user.signature || '',
			gender: user.gender || 0,
			birthday: user.birthday || '',
		};
		
	});

	// 生成年份选项（从1900到当前年份）
	const currentYear = new Date().getFullYear();
	const years = Array.from({ length: currentYear - 1900 + 1 }, (_, i) => currentYear - i);
	// 响应式数据
	const selectedYear = ref(null);
	const selectedMonth = ref(null);
	const selectedDay = ref(null);
	// 监听formData.birthday变化来更新选择器
	watch(
		() => formData.value.birthday,
		(newVal) => {
			if (newVal) {
				const [y, m, d] = newVal.split('-');
				selectedYear.value = parseInt(y);
				selectedMonth.value = parseInt(m);
				selectedDay.value = parseInt(d);
			}
		},
		{ immediate: true } // 立即执行一次
	);

	// 计算当月天数
	const daysInMonth = computed(() => {
		if (!selectedYear.value || !selectedMonth.value) return 31;

		const date = new Date(selectedYear.value, selectedMonth.value, 0);
		return date.getDate();
	});

	// 更新日期选项
	const updateDays = () => {
		selectedDay.value = null; // 清空已选日期
	};

	// 监听所有变化并更新父组件
	watch([selectedYear, selectedMonth, selectedDay], ([y, m, d]) => {
		if (y && m && d) {
			const formattedDate = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
			formData.value.birthday = formattedDate;
		}
	});

	// 头像上传成功处理
	const handleAvatarSuccess = (newAvatarUrl) => {
		apiUpdateAvatarUrl(newAvatarUrl).then((res) => {
			authStore.updateUser(res.data.data);
		});
	};

	// 保存处理
	const handleSave = async () => {
		try {
			isSaving.value = true;

			// 调用更新接口
			const res = await apiUserBasicUpdate(formData.value);
			if (res.data.code === 1) {
				// 更新store数据

				await authStore.updateUser(res.data.data);
				
				ElMessage.success('修改保存成功');
			} else {
				ElMessage.error(res.data.msg);
			}
		} catch (error) {
			ElMessage.error(error.message || '保存失败');
		} finally {
			isSaving.value = false;
		}
	};
</script>

<style lang="scss" scoped>
	.settings-container {
		max-width: 1200px;
		margin: 0 auto;

		.settings-content {
			display: flex;
			gap: 60px;
		}

		.form-section {
			flex: 1;

			:deep(.el-form-item__label) {
				font-weight: bold;
			}
			.birthday-picker {
				display: flex;
				align-items: center;
				gap: 10px;
				.label {
					font-size: 14px;
					color: #606266;
				}

				.selectors {
					display: flex;
					gap: 10px;

					:deep(.el-select) {
						width: 120px;
					}
				}
			}
			.save-button {
				margin-top: 30px;
			}
		}

		.avatar-section {
			width: 300px;

			.avatar-wrapper {
				position: relative;
				width: 200px;
				margin: 0 auto;

				.user-avatar {
					width: 100%;
					height: 200px;
					border: 2px solid #eee;
				}

				.change-avatar-btn {
					position: absolute;
					bottom: 10px;
					left: 50%;
					transform: translateX(-50%);
					padding: 6px 15px;
					background: rgba(0, 0, 0, 0.1);
					color: white;
					border-radius: 20px;
					transition: all 0.3s;

					&:hover {
						background: rgba(0, 0, 0, 0.8);
					}
				}
			}
		}
	}
</style>
