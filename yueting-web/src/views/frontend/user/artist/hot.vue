<template>
	<div class="hot-songs">
		<div v-if="loading" class="loading">加载中...</div>
		<div v-else-if="error" class="error">数据加载失败</div>
		<div v-else class="song-list">
			<el-table :data="songs" stripe style="width: 100%">
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
				<el-table-column label="标题">
					<template #default="{ row }">
						<router-link v-if="row.songId" :to="`/song?id=${row.songId}`" class="song-link">
							{{ row.songName }}
						</router-link>
						<span v-else>{{ row.songName || '未知歌曲' }}</span>
					</template>
				</el-table-column>
				<el-table-column label="时长" width="120">
					<template #default="scope">
						<div class="duration-container">
							<span class="duration-text">{{ formatDuration(scope.row.duration) }}</span>
							<el-button class="addnext-btn" link @click.stop="handleAddToNext(scope.row)">
								<el-icon :size="18"><Plus /></el-icon>
							</el-button>
							<el-button class="favorite-btn" link @click.stop="handleFavorite(scope.row)">
								<el-icon :size="20"><folderAdd /></el-icon>
							</el-button>
						</div>
					</template>
				</el-table-column>
				<el-table-column label="专辑">
					<template #default="{ row }">
						<router-link v-if="row.albumId" :to="`/album?id=${row.albumId}`" class="album-link">
							{{ row.albumName }}
						</router-link>
						<span v-else>{{ row.albumName || '未知专辑' }}</span>
					</template>
				</el-table-column>
			</el-table>
		</div>
	</div>
	<el-dialog v-model="dialogVisible" title="选择收藏歌单" width="400px">
		<div class="playlist-dialog">
			<div
				v-for="playlist in createdPlaylists"
				:key="playlist.playlistId"
				class="playlist-item"
				@click="handleSelectPlaylist(playlist.playlistId)"
			>
				<img :src="playlist.coverUrl" class="cover" />
				<div class="info">
					<div class="name">{{ playlist.playlistName }}</div>
					<div class="count">{{ playlist.songCount }}首</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>
<script setup>
	import { ref, watch, inject } from 'vue';
	import { apiGetHotSongs } from '@/api/frontend/song';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import {
		apiDeleteSongFromPlaylist,
		apiCollectPlaylist,
	} from '@/api/frontend/playlist';
	import { useRoute } from 'vue-router';
	import { formatSeconds as formatDuration } from '@/utils/format.js';
	import { useSongAction } from '@/composables/useSongAction';

	const authStore = useAuthStore();
	const playerStore = usePlayerStore();
	const route = useRoute();
	const playerBarRef = inject('playerBar');
	const { dialogVisible, createdPlaylists, openAddToPlaylistDialog, handleSelectPlaylist, handleAddToNext: useHandleAddToNext } = useSongAction(playerStore, playerBarRef, authStore);

	const songs = ref([]);
	const loading = ref(false);
	const error = ref(false);
	const handlePlay = async (row) => {
		// 添加空值检查
		if (!playerBarRef.value) return;

		if (playerStore.currentSong?.songId === row.songId) {
			playerStore.togglePlay();
		} else {
			const index = playerStore.playQueue.findIndex((item) => item.songId === row.songId);
			if (index > -1) {
				if (index < playerStore.currentIndex) {
					for (let i = index + 1; i <= playerStore.currentIndex; i++) {
						playerStore.playQueue[i - 1] = playerStore.playQueue[i];
					}
					playerStore.playQueue[playerStore.currentIndex] = row;
					playerStore.playAtIndex(playerStore.currentIndex);
				} else {
					for (let i = index - 1; i >= playerStore.currentIndex + 1; i--) {
						playerStore.playQueue[i + 1] = playerStore.playQueue[i];
					}
					playerStore.currentIndex++;
					playerStore.playQueue[playerStore.currentIndex] = row;
					playerStore.playAtIndex(playerStore.currentIndex);
				}
			} else {
				playerStore.addNext(row);
				playerStore.playAtIndex(playerStore.currentIndex + 1);
			}
		}
		playerBarRef.value.showBar();
		setTimeout(() => {
			playerBarRef.value?.scheduleHide();
		}, 1000);
	};

	const handleAddToNext = (song) => {
		useHandleAddToNext(song, true);
	};

	// 收藏处理函数
	const handleFavorite = (song) => {
		openAddToPlaylistDialog(song);
	};
	const fetchSongs = async () => {
		try {
			loading.value = true;
			error.value = false;
			const res = await apiGetHotSongs(route.query.id || authStore.artist.artistId);
			songs.value = res.data.data;
		} catch (err) {
			console.error('获取热门作品失败:', err);
			error.value = true;
		} finally {
			loading.value = false;
		}
	};

	watch(() => route.query.id, () => {
		fetchSongs();
	}, { immediate: true });
</script>

<style lang="scss" scoped>
	.hot-songs {
		background: #fff;
		border-radius: 8px;

		.song-list {
			flex: 1;
			overflow: auto;

			:deep(.el-table) {
				font-size: 14px;

				th {
					background: #f5f7fa;
					color: #606266;
				}

				.cell {
					padding-left: 15px;
				}
			}
			.artist-link {
				color: #606266;
				text-decoration: none;
				transition: color 0.3s;

				&:hover {
					color: red;
					text-decoration: underline;
				}
			}
			.album-link {
				color: #606266;
				text-decoration: none;
				transition: color 0.3s;

				&:hover {
					color: red; // 保持与歌手链接相同的红色
					text-decoration: underline;
				}
			}
			.song-link {
				color: #606266;
				text-decoration: none;
				transition: color 0.3s;

				&:hover {
					color: red; // 保持与歌手链接相同的红色
					text-decoration: underline;
				}
			}
			.duration-container {
				position: relative;
				display: inline-block;
				width: 100%;
				height: 100%;

				.duration-text {
					display: inline-block;
					width: 100%;
				}

				// 新增按钮样式
				.addnext-btn {
					position: absolute;
					left: -10px;
					top: 50%;
					transform: translateY(-50%);
					opacity: 0;
					transition: opacity 0.2s;
					padding: 0;
					color: #666;

					&:hover {
						color: #ec4141;
					}
				}

				.favorite-btn {
					position: absolute;
					left: 0;
					top: 50%;
					transform: translateY(-50%);
					opacity: 0;
					transition: opacity 0.2s;
					padding: 0;
				}

				.delete-btn {
					position: absolute;
					left: 24px; // 在收藏按钮右侧
					top: 50%;
					transform: translateY(-50%);
					opacity: 0;
					transition: opacity 0.2s;
					padding: 0;
				}

				&:hover {
					.duration-text {
						opacity: 0;
					}

					.addnext-btn,
					.favorite-btn,
					.delete-btn {
						opacity: 1;
					}
				}
			}

			// 保持表格行高度稳定
			:deep(.el-table__body tr) td {
				padding: 8px 0;
			}

			// 调整按钮位置
			:deep(.el-table__cell) {
				.cell {
					padding-left: 15px;
					padding-right: 15px;
				}
			}
		}

		.loading,
		.error {
			text-align: center;
			padding: 20px;
			color: #999;
		}
	}
	.playlist-dialog {
		max-height: 60vh;
		overflow-y: auto;

		.playlist-item {
			display: flex;
			align-items: center;
			padding: 12px;
			cursor: pointer;
			transition: background 0.3s;

			&:hover {
				background: #f5f7fa;
			}

			.cover {
				width: 50px;
				height: 50px;
				border-radius: 4px;
				margin-right: 15px;
			}

			.info {
				flex: 1;

				.name {
					font-size: 14px;
					margin-bottom: 4px;
				}

				.count {
					font-size: 12px;
					color: #909399;
				}
			}
		}
	}
</style>
