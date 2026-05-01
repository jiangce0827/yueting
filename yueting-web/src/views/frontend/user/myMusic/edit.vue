<!-- @/views/frontend/user/myMusic/edit.vue -->
<template>
	<div class="edit-container">
		<div class="edit-content" v-if="!coverUploadVisible">
			<!-- 左侧表单区域 -->
			<div class="form-section">
				<el-form :model="form" label-width="60px">
					<!-- 专辑名 -->
					<el-form-item label="专辑名">
						<el-input v-model="form.playlistName" maxlength="20" show-word-limit />
					</el-form-item>
					<el-form-item label="标签">
						<el-select
							v-model="form.tagIds"
							multiple
							:multiple-limit="3"
							placeholder="点击选择标签（最多3个）"
							@focus="showTagPanel = true"
							@blur="showTagPanel = false"
						>
							<el-option
								v-for="tag in tagOptions"
								:key="tag.tagId"
								:label="tag.name"
								:value="tag.tagId"
								style="display: none"
							/>
							<div class="tag-panel">
								<div
									v-for="tag in tagOptions"
									:key="tag.tagId"
									class="tag-item"
									:class="{ selected: (form.tagIds || []).includes(tag.tagId) }"
									@click="toggleTag(tag.tagId)"
								>
									{{ tag.name }}
								</div>
							</div>
						</el-select>
					</el-form-item>
					<!-- 介绍 -->
					<el-form-item label="介绍">
						<el-input
							v-model="form.description"
							type="textarea"
							:rows="6"
							maxlength="1000"
							show-word-limit
						/>
					</el-form-item>
					<el-form-item class="save-button">
						<el-button type="primary" @click="handleSubmit" :loading="isSaving"> 保存 </el-button>
						<el-button type="danger" style="margin-left: 40px" @click="handleCancel"
							>取消</el-button
						>
					</el-form-item>
				</el-form>
			</div>
			<!-- 右侧封面区域 -->
			<div class="cover-section">
				<div class="cover-wrapper">
					<img :src="form.coverUrl" class="user-cover" />
					<button @click="coverUploadVisible = true" class="change-cover-btn">修改封面</button>
				</div>
			</div>
		</div>
		<div v-else class="avatar-upload-content">
			<div class="header"></div>
			<ImageCropper
				:current-avatar="form.coverUrl"
				@onSuccess="handleAvatarSuccess"
				@close="coverUploadVisible = false"
			/>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { ElMessage } from 'element-plus';
	import {
		apiGetPlaylistWithUser,
		apiUpdatePlaylist,
		apiUpdatePlaylistCover,
	} from '@/api/frontend/playlist';
	import { apiGetAllTags } from '@/api/frontend/tag';
	import { useAuthStore } from '@/stores/frontend/auth';
	import ImageCropper from '@/components/frontend/ImageCropper.vue';

	const route = useRoute();
	const router = useRouter();
	const authStore = useAuthStore();
	const coverUploadVisible = ref(false); // 控制上传弹窗的显示与隐藏
	const isSaving = ref(false);
	const formRef = ref(null);
	const form = ref({
		playlistId: '',
		playlistName: '',
		tagNames: [],
		tagIds: [],
		description: '',
		isPublic: true,
		coverUrl: '',
	});

	// 加载原始数据
	const loadData = async () => {
		try {
			const res = await apiGetPlaylistWithUser(route.query.id);
			if (res.data.data.userId !== authStore.user?.userId) {
				ElMessage.error('无权限编辑该歌单');
				router.go(-1);
				return;
			}
			// 确保tags存在且为数组
			const playlistData = res.data.data;
			form.value = {
				...playlistData,
				tagIds: playlistData.tagIds ? playlistData.tagIds.split(',').map(Number) : [],
			};
		} catch (error) {
			ElMessage.error('获取歌单信息失败');
			router.go(-1);
		}
	};
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
	const toggleTag = (tagId) => {
		const currentTags = form.value.tagIds || [];
		const index = currentTags.indexOf(tagId);

		if (index === -1) {
			if (currentTags.length < 3) {
				form.value.tagIds = [...currentTags, tagId];
			} else {
				ElMessage.warning('最多只能选择3个标签');
			}
		} else {
			form.value.tagIds = currentTags.filter((id) => id !== tagId);
		}
	};
	// 提交表单
	const handleSubmit = async () => {
		try {

			await apiUpdatePlaylist(form.value);

			ElMessage.success('更新成功');
			router.push(`/my/playlist?id=${form.value.playlistId}`);
		} catch (error) {
			ElMessage.error(error.response?.data?.message || '更新失败');
		}
	};

	// 取消编辑
	const handleCancel = () => {
		router.push(`/my/playlist?id=${form.value.playlistId}`);
	};

	onMounted(async () => {
		await loadData();

		if (!authStore.isLoggedIn || authStore.user.userId != form.value.userId) {
			router.push('/');
			return;
		}
		loadTags();
	});

	// ImageCropper成功回调
	const handleAvatarSuccess = (newAvatarUrl) => {
		apiUpdatePlaylistCover(form.value.playlistId, newAvatarUrl).then((res) => {
			form.value.coverUrl = newAvatarUrl;
		});
	};
</script>

<style scoped lang="scss">
	.edit-container {
		padding: 20px 0;
		max-width: 1200px;
		margin: 0 auto;

		.edit-content {
			display: flex;
			gap: 20px;
		}

		.form-section {
			flex: 1;
		}
		.cover-section {
			width: 200px;

			.cover-wrapper {
				position: relative;
				width: 200px;
				margin: 0 auto;

				.user-cover {
					width: 100%;
					height: 200px;
					border: 2px solid #eee;
				}

				.change-cover-btn {
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

	// 添加标签相关样式
	.tag-panel {
		padding: 10px;
		display: grid;
		grid-template-columns: repeat(3, 1fr); /* 3列布局 */
		gap: 8px;
		max-height: 200px;
		overflow-y: auto;

		.tag-item {
			padding: 8px 12px;
			border-radius: 4px;
			background: #f5f7fa;
			cursor: pointer;
			transition: all 0.3s;
			text-align: center;

			&:hover {
				background: #e4e7ed;
			}

			&.selected {
				background: var(--el-color-primary);
				color: white;
			}
		}
	}
</style>
