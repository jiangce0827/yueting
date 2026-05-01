<template>
	<div class="play-history-container">
		<div class="page-header">
			<h2 class="page-title">听歌排行</h2>
			<div class="header-right">
				<div class="time-filter">
					<el-radio-group v-model="timeRange" @change="handleTimeChange">
						<el-radio-button :label="true">最近一周</el-radio-button>
						<el-radio-button :label="false">所有时间</el-radio-button>
					</el-radio-group>
				</div>
			</div>
		</div>

		<div class="history-table" v-if="historyList.length > 0">
			<el-table :data="historyList" stripe style="width: 100%">
				<el-table-column label="序号" width="80">
					<template #default="scope">
						{{ scope.$index + 1 }}
					</template>
				</el-table-column>
				<el-table-column label="" width="60">
					<template #default="scope">
						<el-icon size="20px" @click="handlePlay(scope.row)"><videoPlay /></el-icon>
					</template>
				</el-table-column>
				<el-table-column label="歌曲">
					<template #default="{ row }">
						<router-link v-if="row.songId" :to="`/song?id=${row.songId}`" class="song-link">
							{{ row.songName }}
						</router-link>
						<span v-else>{{ row.songName || '未知歌曲' }}</span>
						<span class="artist-separator" v-if="row.artistNames"> - </span>
						<template v-if="row.artistIds">
							<router-link
								v-for="(artistName, idx) in (row.artistNames || '').split('/')"
								:key="idx"
								:to="`/artist?id=${(row.artistIds || '').split(',')[idx]}`"
								class="artist-link"
							>
								{{ artistName || '未知歌手' }}
							</router-link>
						</template>
						<span v-else-if="row.artistNames" class="artist-names">{{ row.artistNames }}</span>
					</template>
				</el-table-column>
				<el-table-column label="播放次数" width="120">
					<template #default="scope">
						{{ scope.row.playCount }}
					</template>
				</el-table-column>
			</el-table>
		</div>
		<div v-else class="empty-ranking">
			<span>暂无播放记录</span>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, inject } from 'vue';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { apiGetUserPlayRanking } from '@/api/frontend/user';
	import { VideoPlay } from '@element-plus/icons-vue';

	const playerStore = usePlayerStore();
	const playerBarRef = inject('playerBar');

	const historyList = ref([]);
	const timeRange = ref(true);

	// 加载播放排行
	const loadPlayRanking = async () => {
		try {
			const res = await apiGetUserPlayRanking(timeRange.value);
			if (res.data.code === 1) {
				historyList.value = res.data.data || [];
			}
		} catch (error) {
			console.error('获取播放排行失败:', error);
		}
	};

	// 播放歌曲
	const handlePlay = (song) => {
		if (!playerBarRef.value) return;

		const songWithArtist = {
			songId: song.songId,
			songName: song.songName,
			coverUrl: song.coverUrl,
			artistNames: song.artistNames,
			artistIds: song.artistIds,
		};

		if (playerStore.currentSong?.songId === song.songId) {
			playerStore.togglePlay();
		} else {
			playerStore.addNext(songWithArtist);
			playerStore.playAtIndex(playerStore.currentIndex + 1);
		}
		playerBarRef.value.showBar();
		setTimeout(() => {
			playerBarRef.value?.scheduleHide();
		}, 1000);
	};

	// 时间筛选变化
	const handleTimeChange = () => {
		loadPlayRanking();
	};

	onMounted(() => {
		loadPlayRanking();
	});
</script>

<style lang="scss" scoped>
	.play-history-container {
		width: 950px;
		margin: 0 auto;
		background: white;
		padding: 20px 30px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

		.page-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 20px;

			.page-title {
				font-size: 18px;
				color: #333;
				margin: 0;
			}

			.header-right {
				display: flex;
				align-items: center;
				gap: 15px;
			}

			.time-filter {
				:deep(.el-radio-group) {
					.el-radio-button__inner {
						padding: 8px 15px;
					}
				}
			}
		}

		.history-table {
			:deep(.el-table) {
				font-size: 14px;

				th {
					background: #f5f7fa;
					color: #606266;
				}

				.cell {
					padding-left: 15px;
					padding-right: 15px;
				}
			}

			.song-link {
				color: #606266;
				text-decoration: none;
				transition: color 0.3s;

				&:hover {
					color: #ec4141;
					text-decoration: underline;
				}
			}

			.artist-link {
				color: #606266;
				text-decoration: none;
				transition: color 0.3s;

				&:hover {
					color: #ec4141;
					text-decoration: underline;
				}
			}

			:deep(.el-table__body tr td) {
				padding: 8px 0;
			}
		}

		.empty-ranking {
			text-align: center;
			padding: 60px 0;
			color: #999;
			font-size: 14px;
		}

		.pagination-wrapper {
			display: flex;
			justify-content: center;
			margin-top: 20px;
		}
	}
</style>