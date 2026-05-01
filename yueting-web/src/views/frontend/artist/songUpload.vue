<template>
	<div class="upload-container">
		<!-- 专辑部分 -->
		<div style="padding: 10px; background-color: #fafafa">
			<el-form label-width="100px">
				<el-form-item label="选择专辑">
					<div class="album-container">
						<div class="album-select-wrapper">
							<el-autocomplete
								v-model="albumQuery"
								:fetch-suggestions="searchAlbum"
								placeholder="搜索专辑或创建新专辑"
								@select="handleAlbumSelect"
								class="album-autocomplete"
							>
								<template #default="{ item }">
									<div v-if="item.isNew" class="album-option new-album">
										<i class="el-icon-plus"></i> 创建新专辑
									</div>
									<div v-else class="album-option">
										<img :src="item.cover" class="album-cover" />
										<span>{{ item.name }}</span>
									</div>
								</template>
							</el-autocomplete>
						</div>

						<!-- 新专辑表单 -->
						<div v-if="isNewAlbum" class="new-album-form">
							<el-form-item label="专辑封面">
								<img :src="newAlbum.cover" @click="coverShowCropper = true" class="cover-preview" />
								<ImageCropperDialog
									v-model:visible="coverShowCropper"
									:current-avatar="newAlbum.cover"
									:fixed-number="[1, 1]"
									@success="handleCoverUploadSuccess"
									@close="coverShowCropper = false"
								/>
							</el-form-item>
							<el-form-item label="专辑名称" required>
								<el-input v-model="newAlbum.name" placeholder="请输入专辑名称" />
							</el-form-item>

							<el-form-item label="发行日期" required>
								<el-date-picker
									v-model="newAlbum.releaseDate"
									type="date"
									placeholder="选择日期"
								/>
							</el-form-item>

							<el-form-item label="专辑描述">
								<el-input
									v-model="newAlbum.description"
									type="textarea"
									:rows="4"
									placeholder="请输入专辑描述"
								/>
							</el-form-item>
						</div>
					</div>
				</el-form-item>
			</el-form>
		</div>
		<!-- 歌曲上传部分 -->
		<div class="song-upload-section">
			<div v-for="(song, index) in songs" :key="index" class="song-item">
				<div class="song-header">
					<span class="song-filename">{{ song.fileName }}</span>
					<el-button @click="removeSong(index)">删除</el-button>
				</div>
				<!-- 歌曲表单 -->
				<el-form :model="song" label-width="100px">
					<el-form-item label="歌曲类型" required>
						<el-radio-group v-model="song.type">
							<el-radio :value="0">原创</el-radio>
							<el-radio :value="1">翻唱</el-radio>
						</el-radio-group>
					</el-form-item>

					<el-form-item label="歌曲名称" required>
						<el-input v-model="song.name" placeholder="请输入歌曲名称" />
					</el-form-item>
					<!-- 在歌曲表单中添加歌手选择器 -->
					<el-form-item label="演唱歌手" required>
						<el-select
							v-model="song.artists"
							multiple
							filterable
							remote
							:remote-method="searchArtist"
							:loading="artistLoading"
							placeholder="搜索并选择歌手"
						>
							<el-option
								v-for="artist in artistOptions"
								:key="artist.artistId"
								:label="artist.artistName"
								:value="artist.artistId"
								class="artist-el-option"
							>
								<div class="artist-option">
									<img :src="artist.artistAvatarUrl" class="artist-avatar" />
									<span class="artist-name">{{ artist.artistName }}</span>
								</div>
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="语种" required>
						<el-select v-model="song.language" placeholder="请选择语种">
							<el-option label="华语" :value="1"></el-option>
							<el-option label="欧美" :value="2"></el-option>
							<el-option label="日文" :value="3"></el-option>
							<el-option label="韩文" :value="4"></el-option>
							<el-option label="其他" :value="0"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="标签">
						<el-select
							v-model="song.tags"
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
									:class="{ selected: song.tags.includes(tag.tagId) }"
									@click="toggleTag(song, tag.tagId)"
								>
									{{ tag.name }}
								</div>
							</div>
						</el-select>
					</el-form-item>
					<el-form-item label="作词人" v-if="song.type === 0">
						<el-select
							v-model="song.lyricists"
							multiple
							filterable
							remote
							:remote-method="(query) => searchLyricist(query, song)"
							:loading="lyricistLoading"
							placeholder="搜索并选择作词人"
						>
							<el-option
								v-for="artist in artistOptions"
								:key="artist.artistId"
								:label="artist.artistName"
								:value="artist.artistId"
								class="artist-el-option"
							>
								<div class="artist-option">
									<img :src="artist.artistAvatarUrl" class="artist-avatar" />
									<span class="artist-name">{{ artist.artistName }}</span>
								</div>
							</el-option>
						</el-select>
					</el-form-item>

					<el-form-item label="作曲人" v-if="song.type === 0">
						<el-select
							v-model="song.composers"
							multiple
							filterable
							remote
							:remote-method="(query) => searchComposer(query, song)"
							:loading="composerLoading"
							placeholder="搜索并选择作曲人"
						>
							<el-option
								v-for="artist in artistOptions"
								:key="artist.artistId"
								:label="artist.artistName"
								:value="artist.artistId"
								class="artist-el-option"
							>
								<div class="artist-option">
									<img :src="artist.artistAvatarUrl" class="artist-avatar" />
									<span class="artist-name">{{ artist.artistName }}</span>
								</div>
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="歌词">
						<el-input v-model="song.lyrics" type="textarea" :rows="8" placeholder="请输入歌词" />
					</el-form-item>
				</el-form>
			</div>
		</div>
		<el-upload
			multiple
			drag
			action="#"
			:auto-upload="false"
			:file-list="songFiles"
			:on-change="handleSongUpload"
			:show-file-list="false"
			accept=".mp3,.wav"
		>
			<i class="el-icon-upload"></i>
			<div class="upload-tip">点击或拖拽音乐文件到此上传</div>
			<div class="upload-requirement">支持格式：MP3/WAV</div>
		</el-upload>
		<!-- 提交 -->
		<div class="submit-wrapper">
			<el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
				提交全部
			</el-button>
		</div>
	</div>
</template>

<script setup>
	import { ref, reactive, onMounted } from 'vue';
	import { apiSearchMyAlbums } from '@/api/frontend/album';
	import { searchArtistBasic } from '@/api/frontend/artist';
	import { ElMessage } from 'element-plus';
	import { apiGetAllTags } from '@/api/frontend/tag';
	import ImageCropperDialog from '@/components/frontend/ImageCropperDialog.vue';
	import { apiUploadMusicFile } from '@/api/frontend/common';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { apiUploadMusic } from '../../../api/frontend/song';
	import {getMusicTime} from '@/utils/musicUtils'
	const authStore = useAuthStore();

	const allAlbums = ref([]); // 新增：存储所有专辑数据
	const albumQuery = ref(''); // 专辑搜索关键字
	const selectedAlbum = ref(null); // 选中的专辑id
	const isNewAlbum = ref(false); // 是否创建新专辑
	const coverShowCropper = ref(false); // 控制封面裁剪对话框的显示与隐藏
	// 新专辑数据
	const newAlbum = reactive({
		cover: '/src/assets/upload.png',
		name: '',
		releaseDate: '',
		description: '',
	});
	// 搜索专辑
	const searchAlbum = async (query, cb) => {

		// 本地过滤逻辑
		const filtered = allAlbums.value.filter((album) =>
			album.albumName.toLowerCase().includes(query.toLowerCase())
		);

		// 创建选项结构
		const options = filtered.map((album) => ({
			value: album.albumId,
			name: album.albumName,
			cover: album.coverUrl,
			isNew: false,
		}));

		// 添加创建新专辑选项
		options.unshift({
			value: '新建专辑',
			name: query,
			isNew: true,
		});

		cb(options);
	};

	// 选中专辑
	const handleAlbumSelect = (item) => {
		if (item.isNew) {
			isNewAlbum.value = true;
			newAlbum.name = item.name;
		} else {
			selectedAlbum.value = item.value; // 存储专辑ID
			albumQuery.value = item.name; // 关键修改：设置输入框显示专辑名称
			isNewAlbum.value = false;
		}
	};
	// 上传封面
	const handleCoverUploadSuccess = (newAvatar) => {
		newAlbum.cover = newAvatar;
		coverShowCropper.value = false;
	};
	// 歌曲相关
	// 在原有代码基础上添加删除方法
	const removeSong = (index) => {
		songs.splice(index, 1);
		songFiles.value.splice(index, 1);
	};
	const songFiles = ref([]);
	const songs = reactive([]);
	const handleSongUpload = async (file) => {
		try {

			const formData = new FormData();
			formData.append('file', file.raw);

			const response = await apiUploadMusicFile(formData);
			songs.push({
				fileName: file.name,
				fileUrl: response.data.data,
				type: 0,
				name: '',
				artists: [authStore.artist.artistId],
				language: 1,
				duration: await getMusicTime(file.raw),
				tags: [],
				lyricists: [authStore.artist.artistId],
				composers: [authStore.artist.artistId],
				lyrics: '',
			});
			// 确保当前艺术家在选项列表中
			if (!artistOptions.value.some((a) => a.id === authStore.artist.artistId)) {
				artistOptions.value.unshift({
					artistId: authStore.artist.artistId,
					artistName: authStore.artist.artistName,
					artistAvatarUrl: authStore.artist.artistAvatarUrl,
				});
			}
		} catch (error) {
			ElMessage.error('文件上传失败');
		} finally {
		}
	};

	const artistLoading = ref(false);
	const artistOptions = ref([]);

	// 搜索歌手方法
	const searchArtist = async (query) => {
		if (!query) {
			artistOptions.value = [];
			return;
		}

		artistLoading.value = true;
		try {
			const res = await searchArtistBasic(query);
			artistOptions.value = res.data.data.records.map((item) => ({
				artistId: item.artistId,
				artistName: item.artistName,
				artistAvatarUrl: item.artistAvatarUrl || '/default-avatar.png',
			}));
		} finally {
			artistLoading.value = false;
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
	// ####################作词作曲#################
	// 新增状态
	const lyricistLoading = ref(false);
	const composerLoading = ref(false);

	// 新增搜索方法
	const searchLyricist = async (query, song) => {
		if (!query) {
			artistOptions.value = [];
			return;
		}

		lyricistLoading.value = true;
		try {
			const res = await searchArtistBasic(query);
			artistOptions.value = res.data.data.records
				.filter((a) => !song.lyricists.includes(a.artistId))
				.map(formatArtist);
		} finally {
			lyricistLoading.value = false;
		}
	};

	const searchComposer = async (query, song) => {
		if (!query) {
			artistOptions.value = [];
			return;
		}

		composerLoading.value = true;
		try {
			const res = await searchArtistBasic(query);
			artistOptions.value = res.data.data.records
				.filter((a) => !song.composers.includes(a.artistId))
				.map(formatArtist);
		} finally {
			composerLoading.value = false;
		}
	};

	const formatArtist = (item) => ({
		artistId: item.artistId,
		artistName: item.artistName,
		artistAvatarUrl: item.artistAvatarUrl || '/default-avatar.png',
	});

	// ##################提交################
	// 在原有script基础上添加以下内容
	const submitting = ref(false);

	const handleSubmit = async () => {
		// 表单验证
		if (!validateForm()) return;

		submitting.value = true;
		try {
			const payload = prepareSubmitData();

			const res = await apiUploadMusic(payload);
			if (res.data.code === 1) {
				ElMessage.success('提交成功');
			} else {
				ElMessage.error(res.data.msg || '请稍后再试');
			}
		} catch (error) {
			ElMessage.error('提交失败：' + error.message);
		} finally {
			submitting.value = false;
		}
	};

	// 表单验证方法
	const validateForm = () => {
		// 验证专辑信息
		if (isNewAlbum.value) {
			if (!newAlbum.name || !newAlbum.releaseDate) {
				ElMessage.error('请填写完整的专辑信息');
				return false;
			}
		} else if (!selectedAlbum.value) {
			ElMessage.error('请选择专辑');
			return false;
		}

		// 验证歌曲信息
		if (songs.length === 0) {
			// ElMessage.error('请至少上传一首歌曲');
			// return false;
			return true;
		}

		for (const [index, song] of songs.entries()) {
			if (!song.name || song.type === undefined || song.artists.length === 0) {
				ElMessage.error(`请完善第${index + 1}首歌曲的信息`);
				return false;
			}
		}

		for (const [index, song] of songs.entries()) {
			if (song.type === 0 && song.lyricists.length === 0) {
				// 原创歌曲需要作词人
				ElMessage.error(`第${index + 1}首原创歌曲需要填写作词人`);
				return false;
			}
			if (song.type === 0 && song.composers.length === 0) {
				// 原创歌曲需要作曲人
				ElMessage.error(`第${index + 1}首原创歌曲需要填写作曲人`);
				return false;
			}
		}

		return true;
	};

	// 准备提交数据
	const prepareSubmitData = () => {
		const albumData = isNewAlbum.value
			? {
					albumName: newAlbum.name,
					albumCoverUrl: newAlbum.cover,
					releaseDate: new Date(newAlbum.releaseDate).toISOString().substring(0, 10),
					description: newAlbum.description,
			  }
			: { albumId: selectedAlbum.value };

		return {
			isNewAlbum: isNewAlbum.value,
			...albumData,
			songs: songs.map((song) => ({
				songType: song.type,
				songName: song.name,
				singerIds: song.artists,
				songUrl: song.fileUrl,
				tagIds: song.tags,
				lyricistIds: song.lyricists,
				composerIds: song.composers,
				lyrics: song.lyrics,
				language: song.language,
				duration: Math.round(song.duration),
				// 添加其他字段
			})),
		};
	};
	// 初始化加载标签
	onMounted(async () => {
		loadTags();
		try {
			const res = await apiSearchMyAlbums('');
			allAlbums.value = res.data.data;
		} catch (error) {
			ElMessage.error('专辑加载失败');
		}
	});
</script>

<style lang="scss" scoped>
	.upload-container {
		max-width: 1200px;
		margin: 2rem auto;
		padding: 0 1.5rem;
	}

	.album-container {
		width: 100%;
		max-width: 600px; // 保持与选择框同宽

		.album-select-wrapper {
			width: 100%;
			.album-autocomplete {
				width: 100% !important; // 让选择框占满容器

				/* 专辑选择优化 */
				.album-option {
					display: flex;
					align-items: center;
					padding: 12px;
					transition: background 0.3s ease;

					&:hover {
						background: #f5f7fa;
					}
				}
			}
		}

		/* 调整新专辑表单位置 */
		.new-album-form {
			margin-top: 20px;
			border-top: 1px solid #eee;
			padding-top: 20px;
			// 保持原有样式
			.el-form-item {
				margin-bottom: 1.5rem;
			}
			.cover-preview {
				width: 100px;
				height: 100px;
				cursor: pointer;
				border: 2px solid #eee;
			}
		}
	}

	.album-cover {
		width: 40px;
		height: 40px;
		margin-right: 12px;
		border-radius: 4px;
		object-fit: cover;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	}

	/* 歌曲上传区块 */
	.song-upload-section {
		margin-top: 3rem;
		.song-item {
			margin-bottom: 2rem;
			background: #fafafa;
			border-radius: 12px;
			box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
			transition: transform 0.2s ease;

			&:hover {
				transform: translateY(-2px);
			}

			.song-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 1rem 1.5rem;
				background: #f8fafc;
				border-bottom: 1px solid #e4e7ed;

				.song-filename {
					font-size: 0.95rem;
					color: #606266;
					max-width: 70%;
				}

				.el-button {
					padding: 8px 12px;
				}
			}
		}
	}
	/* 上传区域美化 */
	.upload-area {
		height: 100px;
		width: 200px;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border: 2px dashed var(--el-border-color);
		border-radius: 8px;
		background: #fafafa;
		transition: border-color 0.3s;

		&:hover {
			border-color: var(--el-color-primary);

			.el-icon-upload {
				color: var(--el-color-primary);
			}
		}

		.el-icon-upload {
			font-size: 2.5rem;
			color: #c0c4cc;
			margin-bottom: 1rem;
			transition: color 0.3s;
		}

		.preview-image {
			max-height: 160px;
			max-width: 90%;
			margin-top: 1rem;
			border-radius: 4px;
			box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
		}
	}
	.artist-el-option {
		height: 60px;

		/* 歌手选项样式 */
		.artist-option {
			display: flex;
			align-items: center;
			padding: 8px;
			min-width: 120px;

			.artist-avatar {
				width: 50px;
				height: 50px;
				border-radius: 50%;
				margin-bottom: 8px;
				object-fit: cover;
				box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
			}

			.artist-name {
				font-size: 14px;
				text-align: center;
				line-height: 1.4;
				word-break: break-all;
				margin-left: 30px;
			}
		}
	}
	.submit-wrapper {
		margin-top: 2rem;
		text-align: center;

		.el-button {
			padding: 12px 40px;
			font-size: 1.1rem;
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

	/* 调整选择框样式 */
	:deep(.el-select__tags) {
		max-width: 100%;
		display: flex;
		flex-wrap: wrap;
	}

	:deep(.el-tag) {
		margin: 2px;
		max-width: 120px;
		@apply truncate;
	}

	:deep(.el-select .el-input__inner) {
		cursor: pointer;
	}
</style>
