<template>
	<div class="account-settings-page">
		<div class="hero-card">
			<div class="hero-main">
				<img :src="form.artistAvatarUrl || defaultAvatar" class="hero-avatar" />
				<div class="hero-text">
					<h1>账号设置</h1>
					<p>管理音乐人主页展示信息、头像封面和身份状态。</p>
					<div class="hero-tags">
						<el-tag type="primary" effect="light">{{ form.artistName || '未设置艺人名' }}</el-tag>
						<el-tag :type="authStore.isArtistActive ? 'success' : 'warning'" effect="light">
							{{ authStore.isArtistActive ? '身份已激活' : '身份待完善' }}
						</el-tag>
						<el-tag type="info" effect="light">{{ languageText(form.language) }}</el-tag>
					</div>
				</div>
			</div>
			<div class="hero-side">
				<div class="stat-item">
					<div class="stat-label">音乐人 ID</div>
					<div class="stat-value">{{ authStore.artist?.artistId || '--' }}</div>
				</div>
				<div class="stat-item">
					<div class="stat-label">已开通身份</div>
					<div class="stat-value">{{ identityText }}</div>
				</div>
			</div>
		</div>

		<div class="settings-grid">
			<div class="panel-card media-card">
				<div class="panel-header">
					<h2>形象素材</h2>
					<p>头像和主页头图会直接影响你的音乐人主页展示。</p>
				</div>

				<div class="media-section">
					<div class="field-label">艺人头像</div>
					<div class="avatar-block">
						<img :src="form.artistAvatarUrl || defaultAvatar" class="avatar-preview" />
						<el-button type="primary" plain @click="avatarShowCropper = true">更换头像</el-button>
					</div>
				</div>

				<div class="media-section">
					<div class="field-label">主页头图</div>
					<div class="cover-block">
						<img :src="form.artistCoverUrl || defaultCover" class="cover-preview" />
						<el-button type="primary" plain @click="coverShowCropper = true">更换头图</el-button>
					</div>
				</div>
			</div>

			<div class="panel-card info-card">
				<div class="panel-header">
					<h2>基本资料</h2>
					<p>这些信息会展示在音乐人主页和部分作品信息中。</p>
				</div>

				<el-form label-width="92px" class="artist-form">
					<el-form-item label="艺人名称">
						<el-input v-model="form.artistName" maxlength="30" show-word-limit />
					</el-form-item>

					<el-form-item label="性别">
						<el-radio-group v-model="form.gender">
							<el-radio :value="1">男</el-radio>
							<el-radio :value="2">女</el-radio>
							<el-radio :value="0">团体</el-radio>
						</el-radio-group>
					</el-form-item>

					<el-form-item label="语种">
						<el-select v-model="form.language" placeholder="请选择">
							<el-option label="华语" :value="1" />
							<el-option label="欧美" :value="2" />
							<el-option label="日本" :value="3" />
							<el-option label="韩国" :value="4" />
							<el-option label="其他" :value="0" />
						</el-select>
					</el-form-item>

					<el-form-item label="一句简介">
						<el-input
							v-model="form.artistBio"
							type="textarea"
							:rows="4"
							maxlength="1000"
							show-word-limit
							placeholder="介绍你的音乐风格、代表作品或创作特点"
						/>
					</el-form-item>

					<el-form-item label="详细介绍">
						<el-input
							v-model="form.artistDescription"
							type="textarea"
							:rows="8"
							placeholder="可以填写更完整的创作经历、成员信息或成长故事"
						/>
					</el-form-item>

					<el-form-item>
						<div class="form-actions">
							<el-button @click="resetForm">重置</el-button>
							<el-button type="primary" :loading="isSubmitting" @click="handleSave">
								保存设置
							</el-button>
						</div>
					</el-form-item>
				</el-form>
			</div>
		</div>

		<div class="panel-card status-card">
			<div class="panel-header">
				<h2>身份状态</h2>
				<p>查看当前已激活的音乐人身份，并可继续扩展更多能力。</p>
			</div>
			<div class="status-content">
				<div class="identity-list">
					<el-tag
						v-for="identity in authStore.artist?.identities || []"
						:key="identity"
						type="success"
						effect="light"
						size="large"
					>
						{{ identity }}
					</el-tag>
					<span v-if="!(authStore.artist?.identities || []).length" class="empty-identity">
						当前还没有激活身份
					</span>
				</div>
				<el-button type="primary" plain @click="router.push('/musician/artist/apply/role')">
					去管理身份
				</el-button>
			</div>
		</div>

		<ImageCropperDialog
			v-model:visible="avatarShowCropper"
			:current-avatar="form.artistAvatarUrl || defaultAvatar"
			:fixed-number="[1, 1]"
			@success="handleAvatarUploadSuccess"
			@close="avatarShowCropper = false"
		/>
		<ImageCropperDialog
			v-model:visible="coverShowCropper"
			:current-avatar="form.artistCoverUrl || defaultCover"
			:fixed-number="[2, 1]"
			@success="handleCoverUploadSuccess"
			@close="coverShowCropper = false"
		/>
	</div>
</template>

<script setup>
	import { computed, onMounted, reactive, ref } from 'vue';
	import { useRouter } from 'vue-router';
	import { ElMessage } from 'element-plus';

	import ImageCropperDialog from '@/components/frontend/ImageCropperDialog.vue';
	import { apiGetMyArtistBasic, apiUpdateArtistInfo } from '@/api/frontend/artist';
	import { useAuthStore } from '@/stores/frontend/auth';

	const router = useRouter();
	const authStore = useAuthStore();

	const defaultAvatar = '/src/assets/upload.png';
	const defaultCover = '/src/assets/uploadCover.png';
	const avatarShowCropper = ref(false);
	const coverShowCropper = ref(false);
	const isSubmitting = ref(false);

	const createInitialForm = () => ({
		artistName: authStore.artist?.artistName || '',
		artistAvatarUrl: authStore.artist?.artistAvatarUrl || defaultAvatar,
		artistCoverUrl: authStore.artist?.artistCoverUrl || defaultCover,
		gender: authStore.artist?.gender ?? 0,
		language: authStore.artist?.language ?? 1,
		artistBio: authStore.artist?.artistBio || '',
		artistDescription: authStore.artist?.artistDescription || '',
	});

	const form = reactive(createInitialForm());

	const identityText = computed(() => {
		const identities = authStore.artist?.identities || [];
		return identities.length ? identities.join(' / ') : '未开通';
	});

	const fillForm = (artist) => {
		form.artistName = artist?.artistName || '';
		form.artistAvatarUrl = artist?.artistAvatarUrl || defaultAvatar;
		form.artistCoverUrl = artist?.artistCoverUrl || defaultCover;
		form.gender = artist?.gender ?? 0;
		form.language = artist?.language ?? 1;
		form.artistBio = artist?.artistBio || '';
		form.artistDescription = artist?.artistDescription || '';
	};

	const fetchArtist = async () => {
		try {
			const res = await apiGetMyArtistBasic();
			if (res.data.code === 1 && res.data.data) {
				authStore.updateArtist(res.data.data);
				fillForm(res.data.data);
			}
		} catch (error) {
			console.error('获取音乐人资料失败:', error);
		}
	};

	const validateForm = () => {
		if (!form.artistName.trim()) {
			ElMessage.error('请输入艺人名称');
			return false;
		}
		if (!form.artistAvatarUrl) {
			ElMessage.error('请上传艺人头像');
			return false;
		}
		if (!form.artistCoverUrl) {
			ElMessage.error('请上传主页头图');
			return false;
		}
		if (!form.artistBio || form.artistBio.trim().length < 10) {
			ElMessage.error('一句简介至少需要 10 个字');
			return false;
		}
		return true;
	};

	const handleSave = async () => {
		if (isSubmitting.value) return;
		if (!validateForm()) return;

		isSubmitting.value = true;
		try {
			const payload = {
				artistName: form.artistName.trim(),
				artistAvatarUrl: form.artistAvatarUrl,
				artistCoverUrl: form.artistCoverUrl,
				gender: form.gender,
				language: form.language,
				artistBio: form.artistBio,
				artistDescription: form.artistDescription,
			};
			const res = await apiUpdateArtistInfo(payload);
			if (res.data.code !== 1) {
				throw new Error(res.data.msg || '保存失败');
			}
			await fetchArtist();
			ElMessage.success('账号设置已保存');
		} catch (error) {
			ElMessage.error(error.message || '保存失败');
		} finally {
			isSubmitting.value = false;
		}
	};

	const resetForm = () => {
		fillForm(authStore.artist);
	};

	const handleAvatarUploadSuccess = (url) => {
		form.artistAvatarUrl = url;
		avatarShowCropper.value = false;
	};

	const handleCoverUploadSuccess = (url) => {
		form.artistCoverUrl = url;
		coverShowCropper.value = false;
	};

	const languageText = (language) => {
		const map = {
			0: '其他',
			1: '华语',
			2: '欧美',
			3: '日本',
			4: '韩国',
		};
		return map[language] || '未设置';
	};

	onMounted(() => {
		fetchArtist();
	});
</script>

<style lang="scss" scoped>
	.account-settings-page {
		padding: 28px;
		background: linear-gradient(180deg, #f8fafc 0%, #ffffff 220px);
		min-height: 100%;
	}

	.hero-card,
	.panel-card {
		background: #fff;
		border: 1px solid #edf2f7;
		border-radius: 20px;
		box-shadow: 0 14px 34px rgba(15, 23, 42, 0.05);
	}

	.hero-card {
		padding: 24px 28px;
		display: flex;
		justify-content: space-between;
		gap: 20px;
		margin-bottom: 22px;
	}

	.hero-main {
		display: flex;
		gap: 18px;
		align-items: center;
	}

	.hero-avatar {
		width: 88px;
		height: 88px;
		border-radius: 24px;
		object-fit: cover;
		background: #f1f5f9;
	}

	.hero-text h1 {
		margin: 0 0 8px;
		font-size: 28px;
		color: #0f172a;
	}

	.hero-text p {
		margin: 0 0 14px;
		color: #64748b;
		font-size: 14px;
	}

	.hero-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 8px;
	}

	.hero-side {
		display: flex;
		gap: 28px;
		align-items: center;
	}

	.stat-item {
		min-width: 120px;
	}

	.stat-label {
		font-size: 12px;
		color: #94a3b8;
		margin-bottom: 6px;
	}

	.stat-value {
		font-size: 18px;
		font-weight: 700;
		color: #0f172a;
	}

	.settings-grid {
		display: grid;
		grid-template-columns: 360px 1fr;
		gap: 20px;
		margin-bottom: 20px;
	}

	.panel-card {
		padding: 22px;
	}

	.panel-header {
		margin-bottom: 20px;
	}

	.panel-header h2 {
		margin: 0 0 6px;
		font-size: 20px;
		color: #111827;
	}

	.panel-header p {
		margin: 0;
		font-size: 13px;
		color: #6b7280;
	}

	.media-section + .media-section {
		margin-top: 26px;
	}

	.field-label {
		font-size: 13px;
		color: #64748b;
		margin-bottom: 12px;
	}

	.avatar-block {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		gap: 12px;
	}

	.avatar-preview {
		width: 160px;
		height: 160px;
		border-radius: 24px;
		object-fit: cover;
		background: #f1f5f9;
	}

	.cover-block {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		gap: 12px;
	}

	.cover-preview {
		width: 100%;
		aspect-ratio: 2 / 1;
		border-radius: 18px;
		object-fit: cover;
		background: #f1f5f9;
	}

	.artist-form {
		:deep(.el-select) {
			width: 100%;
		}
	}

	.form-actions {
		display: flex;
		gap: 12px;
	}

	.status-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16px;
	}

	.identity-list {
		display: flex;
		flex-wrap: wrap;
		gap: 10px;
	}

	.empty-identity {
		color: #94a3b8;
		font-size: 14px;
	}

	@media (max-width: 1080px) {
		.hero-card,
		.status-content {
			flex-direction: column;
			align-items: flex-start;
		}

		.settings-grid {
			grid-template-columns: 1fr;
		}
	}
</style>
