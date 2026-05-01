<template>
	<!-- 查询条件 -->
	<el-form :model="pageForm" @submit.prevent>
		<el-row>
			<el-form-item label="艺人名" prop="artistName">
				<el-input
					v-model="pageForm.artistName"
					placeholder="输入艺人名"
					clearable
					@keyup.enter="getUserList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
			<el-form-item label="申请身份" style="margin-left: 10px">
				<el-select v-model="pageForm.identityType" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="歌手" value="1" @click="getUserList"></el-option>
					<el-option label="作词" value="2" @click="getUserList"></el-option>
					<el-option label="作曲" value="3" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="审批状态" style="margin-left: 10px">
				<el-select v-model="pageForm.status" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="未审批" value="0" @click="getUserList"></el-option>
					<el-option label="已通过" value="1" @click="getUserList"></el-option>
					<el-option label="已拒绝" value="2" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>

			<el-form-item style="margin-left: 40px">
				<el-button icon="Refresh" @click="handleResetPageForm">重置</el-button>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button type="primary" icon="Search" @click="getUserList">搜索</el-button>
			</el-form-item>
		</el-row>
	</el-form>
	<!-- 表格 -->
	<el-table
		:data="tableData"
		stripe
		border
		:row-style="{ height: '47px' }"
		style="width: 100%; text-align: left; font-size: 16px"
	>
		<el-table-column prop="applicationId" type="index" label="序号" width="60"> </el-table-column>
		<el-table-column prop="identityType" label="申请类型" width="70">
			<template #default="{ row }">
				<el-tag>{{
					row.identityType == 1 ? '歌手' : row.identityType == 2 ? '作词' : '作曲'
				}}</el-tag>
			</template>
		</el-table-column>
		<el-table-column label="作品" min-width="200">
			<template #default="{ row }">
				<div class="file-container">
					<div v-if="row.identityType == 1" class="audio-item">
						<!-- 第一行：播放按钮和文件名 -->
						<div class="audio-header">
							<el-icon :size="20" @click="togglePlay(row)" class="play-icon">
								<VideoPlay v-if="!row.playing" />
								<VideoPause v-else />
							</el-icon>
							<span class="file-name">{{ row.fileName }}</span>
						</div>

						<!-- 第二行：进度条和时间 -->
						<div class="progress-container">
							<span class="time">{{ formatTime(row.currentTime) }}</span>
							<el-slider
								v-model="row.progress"
								:step="0.1"
								:max="100"
								@change="handleSeek(row)"
								@mousedown.native="handleDragStart"
								@mouseup.native="handleDragEnd"
							/>
							<span class="time">{{ formatTime(row.duration) }}</span>
						</div>
					</div>
					<div v-else-if="row.identityType == 3" class="audio-item">
						<!-- 第一行：播放按钮和文件名 -->
						<div class="audio-header">
							<el-icon :size="20" @click="togglePlay(row)" class="play-icon">
								<VideoPlay v-if="!row.playing" />
								<VideoPause v-else />
							</el-icon>
							<span class="file-name">{{ row.fileName }}</span>
						</div>

						<!-- 第二行：进度条和时间 -->
						<div class="progress-container">
							<span class="time">{{ formatTime(row.currentTime) }}</span>
							<el-slider
								v-model="row.progress"
								:step="0.1"
								:max="100"
								@change="handleSeek(row)"
								@mousedown.native="handleDragStart"
								@mouseup.native="handleDragEnd"
							/>
							<span class="time">{{ formatTime(row.duration) }}</span>
						</div>
					</div>
					<div v-else-if="row.identityType == 2" class="singer-review-message">
						<span class="lyrics-link" @click="showLyricsDialog(row)">
							{{ row.fileName }}
						</span>
					</div>
				</div>
			</template>
		</el-table-column>

		<el-table-column prop="artistName" label="申请艺人" min-width="80">
			<template #default="{ row }">
				{{ row.artistName }}
			</template>
		</el-table-column>

		<el-table-column prop="introduction" label="简介" min-width="150" max-width="300">
			<template #default="{ row }">
				<div class="singer-review-message">
					{{ row.introduction }}
				</div>
			</template>
		</el-table-column>

		<el-table-column prop="singerReviewStatus" label="审批状态" width="80">
			<template #default="{ row }">
				<el-tag :type="row.status == 1 ? 'success' : row.status == 2 ? 'danger' : 'warning'">
					{{ row.status == 0 ? '审批中' : row.status == 1 ? '通过' : '拒绝' }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="appliedAt" label="申请时间" min-width="150"> </el-table-column>
		<el-table-column fixed="right" label="操作" width="80" align="center">
			<template #default="{ row }">
				<el-button
					type="success"
					size="small"
					@click="OpenUpdateDialog(row)"
					style="font-size: 16px"
					>编辑</el-button
				>
			</template>
		</el-table-column>
	</el-table>
	<!-- 添加隐藏的音频元素 -->
	<audio
		ref="audioPlayer"
		@timeupdate="handleTimeUpdate"
		@ended="handleAudioEnd"
		style="display: none"
	></audio>
	<!-- 分页插件 -->
	<el-pagination
		layout="prev, pager, next"
		:total="total"
		v-model:page="pageForm.pageNum"
		v-model:limit="pageForm.pageSize"
		@current-change="handlePageNumChange"
		@size-change="handlePageSizeChange"
	/>
	<!-- 审批对话框 -->
	<el-dialog title="审批详情" v-model="updateUserDialog" width="30%">
		<el-form :model="updateForm">
			<el-form-item label="状态" prop="status">
				{{ updateForm.status == 0 ? '审批中' : updateForm.status == 1 ? '已通过' : '已拒绝' }}
			</el-form-item>
			<el-form-item label="简介" prop="introduction">
				{{ updateForm.introduction }}
			</el-form-item>
			<el-form-item label="批复">
				<el-input
					type="textarea"
					v-model="updateForm.reason"
					clearable
					:rows="4"
					style="width: 400px"
					placeholder="输入拒绝理由"
				></el-input>
			</el-form-item>
			<el-form-item v-if="updateForm.singerReviewStatus == 0">
				<el-button type="primary" @click="handleUpdateUser(1)">通过</el-button>
				<el-button type="primary" @click="handleUpdateUser(0)">拒绝</el-button>
			</el-form-item>
		</el-form>
	</el-dialog>
	<!-- 新增歌词内容对话框 -->
	<el-dialog v-model="lyricsDialogVisible" title="歌词内容" width="50%">
		<div class="lyrics-content">
			{{ currentLyricsContent }}
		</div>
		<template #footer>
			<el-button @click="lyricsDialogVisible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>
<script setup>
	import { reactive, onMounted, ref, warn } from 'vue';
	import {
		apiPageArtistIdentityApplication,
		apiRejectArtistIdentity,
		apiApproveArtistIdentity,
	} from '@/api/admin/artist';
	import { ElMessage, ElDialog } from 'element-plus';
	import { VideoPlay, VideoPause } from '@element-plus/icons-vue';
	// 添加音频相关逻辑
	const audioPlayer = ref(null);
	const currentAudio = ref(null);
	const togglePlay = (row) => {
		// 停止其他音频
		if (currentAudio.value && currentAudio.value !== row) {
			currentAudio.value.playing = false;
			currentAudio.value.progress = 0;
		}

		// 切换播放状态
		if (!row.playing) {
			playAudio(row);
		} else {
			pauseAudio(row);
		}
	};

	const playAudio = async (row) => {
		try {
			if (currentAudio.value !== row) {
				audioPlayer.value.src = `/api/admin/song/file/${row.fileUrl}`;

				// 添加元数据加载监听
				audioPlayer.value.onloadedmetadata = () => {
					row.duration = audioPlayer.value.duration;
				};

				await audioPlayer.value.play();
			} else {
				await audioPlayer.value.play();
			}
			row.playing = true;
			currentAudio.value = row;
		} catch (error) {
			ElMessage.error('音频播放失败');
		}
	};

	const pauseAudio = (row) => {
		audioPlayer.value.pause();
		row.playing = false;
	};

	const handleTimeUpdate = () => {
		if (currentAudio.value && !isDragging.value) {
			const audio = audioPlayer.value;
			currentAudio.value.currentTime = audio.currentTime;
			currentAudio.value.progress = (audio.currentTime / audio.duration) * 100 || 0;
		}
	};

	const handleAudioEnd = () => {
		if (currentAudio.value) {
			currentAudio.value.playing = false;
			currentAudio.value.progress = 0;
			currentAudio.value = null;
		}
	};
	// 添加时间格式化方法
	const formatTime = (seconds = 0) => {
		const minutes = Math.floor(seconds / 60);
		const secs = Math.floor(seconds % 60);
		return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
	};

	// 新增拖动状态控制
	const isDragging = ref(false);

	const handleSeek = (row) => {
		if (audioPlayer.value && currentAudio.value === row) {
			const newTime = (row.progress / 100) * audioPlayer.value.duration;
			audioPlayer.value.currentTime = newTime;
			if (!row.playing) {
				audioPlayer.value.play();
				row.playing = true;
			}
		}
	};

	const handleDragStart = () => {
		isDragging.value = true;
	};

	const handleDragEnd = () => {
		isDragging.value = false;
	};
	onMounted(() => {
		getUserList();
	});
	/**
	 * 查询用户数据
	 */
	// 分页数据
	const pageForm = reactive({
		artistName: '',
		phone: '',
		email: '',
		identityType: '-1',
		status: '0',
		pageNum: 1,
		pageSize: 10,
	});
	const tableData = reactive([]); // 表格数据
	const total = ref(0); // 数据总数
	// 获取用户数据
	const getUserList = () => {
		apiPageArtistIdentityApplication(pageForm).then((res) => {
			const records = res.data.data.records.map((item) => ({
				...item,
				playing: false,
				progress: 0,
			}));
			tableData.splice(0, tableData.length, ...records);
			total.value = res.data.data.total;
		});
	};
	// 重置查询条件
	const handleResetPageForm = () => {
		Object.assign(pageForm, {
			name: '',
			phone: '',
			email: '',
			vip: '-1',
			identityType: '-1',
			status: '0',
		});
		getUserList();
	};
	//页码变化
	const handlePageNumChange = (newNum) => {
		pageForm.pageNum = newNum;
		getUserList();
	};
	// 页容量变化
	const handlePageSizeChange = () => {
		getUserList();
	};

	/**
	 * 更新用户数据
	 */
	const currentUpdateUserId = ref(null); // 当前修改的用户id
	const updateUserDialog = ref(false); // 修改用户对话框
	// 修改用户数据表单
	const updateForm = reactive({
		id: '',
		name: '',
		avatar: '',
		gender: '',
		phone: '',
		email: '',
		type: '',
		singerReviewStatus: '',
		reason: '',
		statusTime: '',
		vip: '',
		vipTime: '',
	});
	// 打开修改用户对话框
	const OpenUpdateDialog = (row) => {
		updateUserDialog.value = true;
		//拿数据
		currentUpdateUserId.value = row.id;
		Object.assign(updateForm, row);
		//把gender转成字符串
		updateForm.gender = String(updateForm.gender);
		updateForm.type = String(updateForm.type);
		updateForm.vip = String(updateForm.vip);
		updateForm.singerReviewStatus = String(updateForm.singerReviewStatus);
	};
	//修改用户数据
	const handleUpdateUser = (singerReviewStatus) => {
		if (singerReviewStatus == 1) {
			apiApproveArtistIdentity(updateForm.artistIdentityApplicationId).then((res) => {
				ElMessage.success('审批成功');
				getUserList();
				updateUserDialog.value = false;
			});
		} else if (singerReviewStatus == 0) {
			apiRejectArtistIdentity(updateForm.artistIdentityApplicationId, updateForm.reason).then((res) => {
				ElMessage.success('审批成功');
				getUserList();
				updateUserDialog.value = false;
			});
		}
	};

	// 新增弹窗相关变量
	const lyricsDialogVisible = ref(false);
	const currentLyricsContent = ref('');

	// 点击歌词名处理方法
	const showLyricsDialog = (row) => {
		if (row.identityType === 2) {
			// 仅处理歌词类型
			currentLyricsContent.value = row.fileContent;
			lyricsDialogVisible.value = true;
		}
	};
</script>

<style lang="scss" scoped>
	/* 允许单元格内容换行 */
	:deep(.el-table .cell) {
		white-space: normal;
	}

	/* 多行文本省略 */
	.singer-review-message {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 1.5em; /* 保持合适的行高 */
		max-height: 3em; /* 两行文本的高度 */
	}
	.lyrics-link {
		color: #409eff;
		cursor: pointer;
		text-decoration: underline;
	}

	/* 歌词内容保留换行格式 */
	.lyrics-content {
		white-space: pre-line;
		line-height: 1.8;
		font-size: 14px;
		max-height: 60vh;
		overflow-y: auto;
	}
	.file-container {
		display: flex;
		flex-direction: column;
	}

	.audio-item {
		display: flex;
		flex-direction: column;
		gap: 4px;
	}

	.play-controls {
		display: flex;
		align-items: center;
		margin-top: 4px;
	}

	.el-icon {
		cursor: pointer;
		color: #409eff;
		transition: color 0.3s;
	}

	.el-icon:hover {
		color: #79bbff;
	}
	.audio-item {
		display: flex;
		flex-direction: column;
		gap: 6px;
		width: 100%;
	}

	.audio-header {
		display: flex;
		align-items: center;
		gap: 8px;

		.play-icon {
			flex-shrink: 0;
			cursor: pointer;
			color: #409eff;
			transition: color 0.3s;

			&:hover {
				color: #79bbff;
			}
		}

		.file-name {
			flex: 1;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
	}

	.progress-container {
		display: flex;
		align-items: center;
		gap: 4px;
		width: 100%;

		.time {
			font-size: 12px;
			color: #666;
			flex-shrink: 0;
			min-width: 40px;
			text-align: center;
		}

		.progress-bar {
			flex: 1;
			margin: 0;
		}

		:deep(.el-slider) {
			flex: 1;
			margin: 0 8px;

			.el-slider__runway {
				background-color: #ebeef5;
				height: 4px;
			}

			.el-slider__bar {
				background-color: #409eff;
				height: 4px;
			}

			.el-slider__button {
				width: 12px;
				height: 12px;
				border: 2px solid #409eff;
				transition: all 0.2s;

				&:hover {
					transform: scale(1.2);
				}
			}
		}
	}
</style>
