<template>
	<div class="container">
		<div class="auth-card">
			<h1 class="title">作曲人身份认证</h1>

			<!-- 音乐上传区域 -->
			<el-upload
				class="upload-section"
				drag
				action="#"
				:auto-upload="false"
				:show-file-list="false"
				:before-upload="beforeUpload"
				:on-change="handleFileChange"
				:disabled="isUploading"
			>
				<div v-if="!fileUrl" class="upload-wrapper">
					<i class="el-icon-upload"></i>
					<div class="status-area">
						<div class="el-upload__text">
							{{ uploadStatus }}
							<em>点击上传或拖拽文件到这里</em>
						</div>
						<el-progress
							v-if="isUploading"
							:percentage="uploadProgress"
							:stroke-width="6"
							:show-text="false"
						></el-progress>
					</div>
				</div>

				<div v-else class="file-info">
					<div class="file-details">
						<div class="file-name">
							<i class="el-icon-document"></i>
							{{ fileName }}
						</div>
						<div class="file-duration">
							<i class="el-icon-time"></i>
							{{ formattedDuration }}
						</div>
					</div>
					<el-button type="text" icon="el-icon-refresh" @click.stop="handleReplaceFile">
						删除
					</el-button>
				</div>
			</el-upload>

			<!-- 表单区域 -->
			<el-form label-width="100px" label-position="left">

				<el-form-item label="曲子名称：" required>
					<el-input
						v-model="songName"
						placeholder="请输入曲子名称"
						clearable
						maxlength="50"
						show-word-limit
					></el-input>
				</el-form-item>

				<el-form-item label="曲子简介：" required>
					<el-input
						v-model="introduction"
						type="textarea"
						:rows="4"
						placeholder="请输入曲子简介"
						clearable
						maxlength="200"
						show-word-limit
					></el-input>
				</el-form-item>

				<el-form-item>
					<el-button
						type="primary"
						:loading="isSubmitting"
						:disabled="!canSubmit"
						@click="handleSubmit"
					>
						{{ isSubmitting ? '提交中...' : '提交认证申请' }}
					</el-button>
				</el-form-item>
			</el-form>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed } from 'vue';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { apiApplyArtistIdentity } from '@/api/frontend/artist';
	import { apiUploadMusicFile } from '@/api/frontend/common';
	import { getMusicTime } from '@/utils/musicUtils';

	// 响应式数据
	const uploadStatus = ref('点击上传音乐文件 (仅支持MP3格式)');
	const isUploading = ref(false);
	const uploadProgress = ref(0);
	const fileUrl = ref('');
	const fileName = ref('');
	const duration = ref(0);
	const songName = ref('');
	const introduction = ref('');
	const isSubmitting = ref(false);

	// 计算属性
	const formattedDuration = computed(() => {
		const minutes = Math.floor(duration.value / 60);
		const seconds = Math.floor(duration.value % 60);
		return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
	});

	const canSubmit = computed(() => {
		return (
			fileUrl.value && songName.value.trim() && introduction.value.trim()
		);
	});

	// 文件上传处理
	const beforeUpload = (file) => {
		const isMP3 = file.type === 'audio/mpeg';
		const isLt50M = file.size / 1024 / 1024 < 50;

		if (!isMP3) {
			ElMessage.error('仅支持上传MP3格式文件');
			return false;
		}
		if (!isLt50M) {
			ElMessage.error('文件大小不能超过50MB');
			return false;
		}
		return true;
	};

	const handleFileChange = async (file) => {
		try {
			isUploading.value = true;
			uploadStatus.value = '正在上传...';

			const formData = new FormData();
			formData.append('file', file.raw);

			// 模拟上传进度
			const progressInterval = setInterval(() => {
				if (uploadProgress.value < 90) {
					uploadProgress.value += 10;
				}
			}, 200);

			const response = await apiUploadMusicFile(formData);

			fileUrl.value = response.data.data;
			fileName.value = file.name;
			duration.value = await getMusicTime(file.raw);

			uploadStatus.value = '上传成功';
			uploadProgress.value = 100;
			clearInterval(progressInterval);
		} catch (error) {
			ElMessage.error('文件上传失败');
			resetUpload();
		} finally {
			isUploading.value = false;
			setTimeout(() => (uploadProgress.value = 0), 500);
		}
	};

	const handleReplaceFile = () => {
		ElMessageBox.confirm('确认要删除当前文件吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		}).then(() => {
			resetUpload();
		});
	};

	// 提交处理
	const handleSubmit = async () => {
		try {
			isSubmitting.value = true;
			const res = await apiApplyArtistIdentity({
				identityType: 3,
				fileName: songName.value.trim(),
				fileUrl: fileUrl.value,
				introduction: introduction.value.trim(),
			});
			if (res.data.code == 1) {
				ElMessage.success('提交成功，请等待审核');
			} else {
				ElMessage.error(res.data.msg || '提交失败，请稍后重试');
			}
		} catch (error) {
			ElMessage.error('提交失败，请稍后重试');
		} finally {
			isSubmitting.value = false;
		}
	};

	// 重置逻辑
	const resetUpload = () => {
		fileUrl.value = '';
		fileName.value = '';
		duration.value = 0;
		uploadStatus.value = '点击上传音乐文件 (仅支持MP3格式)';
	};
</script>

<style scoped lang="scss">
	.container {
		width: 100%;

		.auth-card {
			padding: 30px;
			box-shadow: 0px 0px 12px rgba(0, 0, 0, 0);
		}

		.title {
			text-align: center;
			margin-bottom: 30px;
			color: #303133;
			font-size: 24px;
		}

		:deep(.upload-section) {
			margin-bottom: 30px;

			.el-upload {
				width: 100%;
				display: block;
			}

			.el-upload-dragger {
				width: 100%;
				padding: 40px;
			}

			.upload-wrapper {
				text-align: center;

				.el-icon-upload {
					font-size: 60px;
					color: #c0c4cc;
					margin-bottom: 20px;
				}

				.el-upload__text {
					color: #606266;
					em {
						color: #409eff;
						font-style: normal;
					}
				}
			}

			.file-info {
				display: flex;
				align-items: center;
				justify-content: space-between;
				padding: 15px;
				background: #f5f7fa;

				.file-details {
					flex: 1;
					margin-right: 20px;

					.file-name {
						font-size: 14px;
						color: #303133;
						margin-bottom: 8px;

						.el-icon-document {
							margin-right: 8px;
							color: #409eff;
						}
					}

					.file-duration {
						font-size: 12px;
						color: #909399;

						.el-icon-time {
							margin-right: 5px;
						}
					}
				}
			}
		}

		.el-form {
			margin-top: 30px;

			.el-radio-group {
				.el-radio {
					margin-right: 20px;
				}
			}

			.el-input {
				max-width: 400px;
			}
			:deep(.el-textarea) {
				max-width: 400px;

				.el-textarea__inner {
					font-size: 14px;
					line-height: 1.5;
					padding: 12px;
					resize: vertical;
				}
			}
			.el-button {
				width: 200px;
				height: 40px;
			}
		}
	}
</style>
