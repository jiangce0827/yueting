<!-- @/views/frontend/user/settings/basic.vue -->
<template>
	<div class="settings-container">
		<div class="settings-content" v-if="!avatarUploadVisible">
			<!-- 左侧表单区域 -->
			<div class="form-section">
				<el-form :model="formData" label-width="60px">
					<!-- 专辑名 -->
					<el-form-item label="专辑名">
						<el-input v-model="formData.albumName" maxlength="20" show-word-limit />
					</el-form-item>

					<!-- 介绍 -->
					<el-form-item label="介绍">
						<el-input
							v-model="formData.description"
							type="textarea"
							:rows="6"
							maxlength="100"
							show-word-limit
						/>
					</el-form-item>
					<!-- 保存按钮 -->
					<el-form-item class="save-button">
						<el-button type="danger" @click="handleDelete">删除专辑</el-button>
						<el-button
							type="primary"
							@click="handleSave"
							style="margin-left: 40px"
							:loading="isSaving"
						>
							保存
						</el-button>
					</el-form-item>
				</el-form>
			</div>

			<!-- 右侧头像区域 -->
			<div class="avatar-section">
				<div class="avatar-wrapper">
					<img :src="formData.coverUrl" class="user-avatar" />
					<button @click="avatarUploadVisible = true" class="change-avatar-btn">修改封面</button>
				</div>
			</div>
		</div>
		<!-- 头像上传区域（当avatarUploadVisible为true时显示） -->
		<div v-else class="avatar-upload-content">
			<div class="header"></div>
			<ImageCropper
				:current-avatar="formData.coverUrl"
				@onSuccess="handleAvatarSuccess"
				@close="avatarUploadVisible = false"
			/>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, onMounted, watch } from 'vue';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import {
		apiGetAlbumBasicByAlbumId,
		apiUpdateAlbumCover,
		apiUpdateAlbumBasic,
		apiDeleteAlbum,
	} from '@/api/frontend/album';
	import ImageCropper from '@/components/frontend/ImageCropper.vue';
	import { useRoute, useRouter } from 'vue-router';
	const route = useRoute();
	const router = useRouter();
	const authStore = useAuthStore();
	const avatarUploadVisible = ref(false); // 控制上传弹窗的显示与隐藏
	const isSaving = ref(false);

	// 表单数据初始化
	const formData = ref({
		albumId: '',
		albumName: '',
		description: '',
		coverUrl: '',
	});

	// 从store初始化表单数据
	onMounted(() => {
		apiGetAlbumBasicByAlbumId(route.params.albumId).then((res) => {
			formData.value = res.data.data;
		});
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

	// 标签
	const tagOptions = ref([]);
	const showTagPanel = ref(false);
	// 获取标签数据
	const loadTags = async () => {
		try {
			const res = await apiGetAllTags();
			tagOptions.value = res.data.data;
		} catch (error) {
			ElMessage.error('标签加载失败');
		}
	};
	const toggleTag = (song, tagId) => {
		const index = song.tags.indexOf(tagId);
		if (index === -1) {
			if (song.tags.length < 3) {
				song.tags.push(tagId);
			} else {
				ElMessage.warning('最多只能选择3个标签');
			}
		} else {
			song.tags.splice(index, 1);
		}
	};

	// 头像上传成功处理
	const handleAvatarSuccess = (newAvatarUrl) => {
		apiUpdateAlbumCover(formData.value.albumId, newAvatarUrl).then((res) => {
			formData.value.coverUrl = newAvatarUrl;
		});
	};

	// 保存处理
	const handleSave = async () => {
		try {
			isSaving.value = true;

			// 调用更新接口
			const res = await apiUpdateAlbumBasic(formData.value);
			if (res.data.code === 1) {
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
	// 新增删除处理方法
	const handleDelete = async () => {
		try {
			await ElMessageBox.confirm(
				'专辑内音乐会随之一起被删除，请慎重选择。确认要删除这个专辑吗？',
				'删除确认',
				{
					confirmButtonText: '确认删除',
					cancelButtonText: '取消',
					type: 'warning',
					center: true,
				}
			);

			// 调用删除接口（假设接口为 apiDeleteAlbum）
			const res = await apiDeleteAlbum(formData.value.albumId);

			if (res.data.code === 1) {
				ElMessage.success('专辑删除成功');
				// 删除成功后跳转到专辑列表页（根据实际路由调整）
				router.push('artist');
			} else {
				ElMessage.error(res.data.msg || '删除失败');
			}
		} catch (error) {
			if (error !== 'cancel') {
				ElMessage.error('删除操作已取消');
			}
		}
	};
</script>

<style lang="scss" scoped>
	.settings-container {
		max-width: 1200px;
		margin: 0 auto;
		padding: 50px;

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
				.save-button {
					margin-top: 30px;
					display: flex;
					gap: 10px;
				}
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
