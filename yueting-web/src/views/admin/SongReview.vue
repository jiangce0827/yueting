<template>
	<!-- 查询条件 -->
	<el-form :model="pageForm" @submit.prevent>
		<el-row>
			<el-form-item label="歌名">
				<el-input
					v-model="pageForm.songName"
					placeholder="输入歌名"
					clearable
					@keyup.enter="getMusicList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
			<el-form-item label="状态" style="margin-left: 30px">
				<el-select v-model="pageForm.status" placeholder="请选择" style="width: 100px">
					<el-option label="全部" value="-1" @click="getMusicList" />
					<el-option label="审核中" value="0" @click="getMusicList" />
					<el-option label="正常" value="1" @click="getMusicList" />
					<el-option label="拒绝" value="2" @click="getMusicList" />
				</el-select>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button icon="Refresh" @click="handleResetPageForm">重置</el-button>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button type="primary" icon="Search" @click="getMusicList">搜索</el-button>
			</el-form-item>
		</el-row>
	</el-form>

	<!-- 音乐数据展示 -->
	<el-table
		:data="tableData"
		stripe
		border
		:fit="true"
		:cell-style="{ padding: '4px 0' }"
		:row-style="{ height: '10px' }"
		@sort-change="handleSortChange"
		style="width: 100%; text-align: left; font-size: 16px"
	>
		<el-table-column prop="applicationId" type="index" label="序号" width="60"> </el-table-column>
		<el-table-column label="音乐" width="200">
			<template #default="{ row }">
				<div class="music-control">
					<el-button
						:type="currentPlayingId === row.applicationId ? 'primary' : 'default'"
						:icon="getPlayIcon(row)"
						:loading="loading && currentPlayingId === row.applicationId"
						@click="handlePlay(row)"
						circle
					/>
					<div v-if="currentPlayingId === row.applicationId" class="progress-container">
						<el-slider
							v-model="currentTime"
							:max="duration"
							:format-tooltip="formatTime"
							@change="handleProgressChange"
							:disabled="!duration"
							:draggable="false"
							class="click-only-slider"
						/>
						<div class="time-display">
							<span>{{ formatTime(currentTime) }}</span>
							<span>{{ formatTime(duration) }}</span>
						</div>
					</div>
				</div>
			</template>
		</el-table-column>
		<el-table-column prop="songName" label="音乐名" min-width="200"> </el-table-column>

		<el-table-column prop="singerNames" label="歌手" min-width="200"> </el-table-column>
		<el-table-column prop="lyricistNames" label="作词" min-width="200"> </el-table-column>
		<el-table-column prop="composerNames" label="作曲" min-width="200"> </el-table-column>

		<el-table-column prop="status" label="状态" width="80">
			<template #default="{ row }">
				<el-tag :type="row.status === 1 ? 'success' : 'danger'">{{
					row.status === 1 ? '通过' : row.status == 0 ? '审批中' : '拒绝'
				}}</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="appliedAt" label="上传时间" min-width="180"> </el-table-column>
		<el-table-column fixed="right" label="操作" width="100" align="center">
			<template #default="{ row }">
				<el-button
					type="success"
					size="small"
					@click="openUpdateDialog(row)"
					style="font-size: 16px"
					>编辑</el-button
				>
			</template>
		</el-table-column>
	</el-table>
	<!-- 分页插件 -->
	<el-pagination
		layout="prev, pager, next"
		:total="total"
		v-model:page="pageForm.pageNum"
		v-model:limit="pageForm.pageSize"
		@current-change="handleCurrentChange"
		@size-change="handleSizeChange"
	/>
	<!-- 编辑音乐对话框 -->
	<el-dialog v-model="updateDialog" @close="closeUpdateDialog">
		<el-form v-model="updateForm">
			<el-form-item label="音乐名" prop="name">
				<span>{{ updateForm.songName }}</span>
			</el-form-item>
			<el-form-item label="作者" prop="author">
				<span>{{ updateForm.singerNames }}</span>
			</el-form-item>
			<el-form-item label="状态">
				{{
					updateForm.musicReviewStatus === 1
						? '通过'
						: updateForm.musicReviewStatus == 2
						? '拒绝'
						: '审批中'
				}}
			</el-form-item>
			<el-form-item label="批复">
				<el-input
					type="textarea"
					v-model="updateForm.musicReviewMessage"
					clearable
					:rows="4"
					style="width: 400px"
					placeholder="输入拒绝理由"
				></el-input>
			</el-form-item>
			<el-form-item style="margin-left: 40px" v-if="updateForm.status == 0">
				<el-button type="primary" @click="handleUpdateMusic(1)">通过</el-button>
				<el-button type="primary" @click="handleUpdateMusic(2)">拒绝</el-button>
			</el-form-item>
		</el-form>
	</el-dialog>
</template>
<script setup>
	import { onMounted, reactive, ref } from 'vue';
	import { ElMessage } from 'element-plus';
	import { apiApprove, apiReject, apiPageSongApplication } from '@/api/admin/song';
	/**
	 * 音乐数据展示
	 */
	const tableData = reactive([]); // 表格数据
	const total = ref(0); // 分页总数
	// 分页查询条件
	const pageForm = reactive({
		pageNum: 1,
		pageSize: 15,
		songName: '',
		status: '0',
	});
	// 重置查询条件
	const handleResetPageForm = () => {
		Object.assign(pageForm, {
			songName: '',
			status: '0',
		});
		getMusicList();
	};
	//发送获取音乐数据请求
	const getMusicList = () => {
		apiPageSongApplication(pageForm).then((res) => {
			tableData.splice(0, tableData.length, ...res.data.data.records);
			total.value = res.data.data.total;
		});
	};
	// 处理页码变化
	const handleCurrentChange = (newPage) => {
		pageForm.pageNum = newPage;
		getMusicList();
	};
	// 处理每页条数变化
	const handleSizeChange = (newSize) => {
		pageForm.pageSize = newSize;
		pageForm.pageNum = 1; // 切换条数后重置到第一页
		getMusicList();
	};
	// 处理排序变化
	const handleSortChange = ({ prop, order }) => {
		if (prop === 'musicReviewCreateTime') {
			// 根据Element Plus的排序值转换后端参数
			pageForm.isAsc = order === 'ascending' ? true : order === 'descending' ? false : true;
			getMusicList();
		}
	};
	//钩子函数
	onMounted(() => {
		getMusicList();
	});

	/**
	 * 音乐播放
	 */
	const currentTime = ref(0); // 当前音乐播放到什么时间
	const duration = ref(0); //当前音乐总时长
	const audioElement = ref(null); // 当前播放的音频元素
	const currentPlayingId = ref(null); // 当前播放的音频ID
	const loading = ref(false); //是否在加载音乐

	// 监听音频时间更新
	const setupAudioListeners = () => {
		// 更新当前音乐时间
		audioElement.value.addEventListener('timeupdate', () => {
			currentTime.value = audioElement.value.currentTime;
		});
		//更新当前音乐总时长
		audioElement.value.addEventListener('loadedmetadata', () => {
			duration.value = audioElement.value.duration;
		});
	};

	// 进度条改变处理
	const handleProgressChange = (val) => {
		currentTime.value = val;
		audioElement.value.currentTime = val;
	};

	// 时间格式化方法
	const formatTime = (seconds) => {
		const minutes = Math.floor(seconds / 60);
		const secs = Math.floor(seconds % 60);
		return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
	};

	//更改播放图标
	const getPlayIcon = (row) => {
		if (currentPlayingId.value === row.applicationId) {
			return audioElement.value?.paused ? 'VideoPlay' : 'VideoPause';
		}
		return 'VideoPlay';
	};

	// 播放/暂停按钮
	const handlePlay = (row) => {
		// 创建音频元素
		if (!audioElement.value) {
			audioElement.value = new Audio();
		}
		if (currentPlayingId.value === row.applicationId) {
			// 点击同一行：切换播放状态
			if (audioElement.value.paused) {
				audioElement.value.play();
			} else {
				audioElement.value.pause();
			}
		} else {
			// 点击不同行：停止当前播放，加载新音频
			currentPlayingId.value = row.applicationId;
			loading.value = true;
			audioElement.value.src = `/api/web/common/file/${row.songUrl}`;
			audioElement.value.play().catch((err) => {
				console.error('播放失败:', err);
				ElMessage.error('音频加载失败');
			});
		}
		// 添加状态更新监听
		audioElement.value.addEventListener('play', () => {
			loading.value = false;
		});

		// 监听音频加载完成
		audioElement.value.addEventListener('loadeddata', () => {
			loading.value = false;
		});

		// 监听播放结束
		audioElement.value.addEventListener('ended', () => {
			currentPlayingId.value = null;
		});
		setupAudioListeners();
	};

	/**
	 * 更新音乐
	 */
	// 更新音乐表单数据
	const updateForm = reactive({
		id: '',
		musicReviewName: '',
		musicReviewAuthors: '',
		musicReviewImage: '',
		musicReviewFile: '',
		musicReviewStatus: '',
		musicReviewVip: '',
		musicReviewMessage: '',
	});
	const updateDialog = ref(false); //更新音乐表单开关
	// 打开更新音乐表单
	const openUpdateDialog = (row) => {
		updateDialog.value = true;
		Object.assign(updateForm, row);
		updateForm.musicReviewStatus = String(updateForm.musicReviewStatus);
		updateForm.musicReviewVip = String(updateForm.musicReviewVip);
	};
	// 关闭更新音乐表单
	const closeUpdateDialog = () => {
		updateDialog.value = false;
	};
	// 审批音乐
	const handleUpdateMusic = (status) => {
		if (status == '1') {
			apiApprove(updateForm.applicationId).then((res) => {
				ElMessage.success('审批成功');
				getMusicList();
				closeUpdateDialog();
			});
		} else if (status == '2') {
			apiReject(updateForm.applicationId, updateForm.musicReviewMessage).then((res) => {
				ElMessage.success('审批成功');
				getMusicList();
				closeUpdateDialog();
			});
		}
	};
</script>

<style scoped>
	/* 音频样式 */
	:deep(.click-only-slider .el-slider__button-wrapper) {
		display: none !important;
	}

	:deep(.click-only-slider .el-slider__runway) {
		cursor: pointer;
		height: 8px;
		background-color: #ebeef5;
	}

	:deep(.click-only-slider .el-slider__bar) {
		height: 8px;
		background-color: #409eff;
	}

	:deep(.click-only-slider:hover .el-slider__runway) {
		background-color: #e4e7ed;
	}

	:deep(.click-only-slider .el-slider__stop) {
		display: none;
	}

	.music-control {
		display: flex;
		align-items: center;
		gap: 10px;
	}

	.progress-container {
		flex: 1;
		width: 150px;
	}

	.time-display {
		display: flex;
		justify-content: space-between;
		font-size: 12px;
		color: #666;
		margin-top: -8px;
	}

	:deep(.el-slider__runway) {
		margin: 8px 0;
	}

	:deep(.el-slider__button) {
		width: 12px;
		height: 12px;
	}
</style>
