<template>
	<div class="container">
		<div class="auth-card">
			<h1 class="title">作词人身份认证</h1>

			<!-- 表单区域 -->
			<el-form label-width="100px" label-position="left">
				<el-form-item label="歌词" required>
					<el-input
						v-model="fileContent"
						type="textarea"
						:rows="10"
						placeholder="请输入歌词"
						clearable
						maxlength="1000"
						show-word-limit
					></el-input>
				</el-form-item>


				<el-form-item label="歌词名：" required>
					<el-input
						v-model="songName"
						placeholder="请输入歌曲名称"
						clearable
						maxlength="50"
						show-word-limit
					></el-input>
				</el-form-item>

				<el-form-item label="歌词简介：" required>
					<el-input
						v-model="introduction"
						type="textarea"
						:rows="4"
						placeholder="请输入歌词作品简介"
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
	import { ElMessage } from 'element-plus';
	import { apiApplyArtistIdentity } from '@/api/frontend/artist';
	import { apiUploadMusicFile } from '@/api/frontend/common';

	// 响应式数据
	const fileContent = ref('');
	const songName = ref('');
	const introduction = ref('');
	const isSubmitting = ref(false);

	const canSubmit = computed(() => {
		return fileContent.value && songName.value.trim() && introduction.value.trim();
	});

	// 提交处理
	const handleSubmit = async () => {
		try {
			isSubmitting.value = true;
			const res = await apiApplyArtistIdentity({
				identityType: 2,
				fileName: songName.value.trim(),
				fileContent: fileContent.value,
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
				max-width: 600px;
			}
			:deep(.el-textarea) {
				max-width: 600px;

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
