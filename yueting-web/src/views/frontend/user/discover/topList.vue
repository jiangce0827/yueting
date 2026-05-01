<!-- @/views/frontend/user/discover/toplist.vue -->
<template>
	<div class="topList-container">
		<div class="main-container">
			<div class="left-container">
				<div class="topList-box">
					<div class="header">音乐榜单</div>
					<div class="topList-grid">
						<div
							v-for="item in topList"
							class="topList-item"
							:class="{ active: item.topListId === currentTopListId }"
							@click="toTopList(item.topListId)"
						>
							<img :src="item.topListCoverUrl" alt="" class="cover" />
							<p class="name">{{ item.topListName }}</p>
						</div>
					</div>
				</div>
			</div>
			<div class="right-container">
				<!-- 榜单信息 -->
				<div class="top-info" v-if="currentTopListData">
					<div class="cover-box">
						<img :src="currentTopListData.coverUrl" class="cover" />
					</div>
					<div class="info-box">
						<h2 class="title">{{ currentTopListData.playlistName }}</h2>
						<div class="update-time">
							最近更新：{{ formatTime(currentTopListData.lastUpdatedAt) }}
						</div>
						<div class="actions">
							<el-button icon="el-icon-video-play" @click="handlePlayAll">
								<el-icon :size="20"><videoPlay /></el-icon>
								播放
							</el-button>
						</div>
					</div>
				</div>

				<!-- 歌曲列表 -->
				<div class="song-list" v-if="currentTopListData">
					<el-table :data="currentTopListData.songs" stripe style="width: 100%">
						<el-table-column label="序号" width="80">
							<template #default="scope">
								{{ scope.$index + 1 }}
							</template>
						</el-table-column>
						<el-table-column label="" width="60">
							<template #default="scope">
								<el-icon
									size="20px"
									style="cursor: pointer"
									:class="{
										'active-play-icon': playerStore.currentSong?.songId === scope.row.songId,
									}"
									@click="handlePlay(scope.row)"
								>
									<videoPlay />
								</el-icon>
							</template>
						</el-table-column>
						<el-table-column prop="songName" label="标题">
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

						<el-table-column label="歌手">
							<template #default="{ row }">
								<span v-for="(name, index) in row.artistNames.split(',')" :key="index">
									<router-link
										:to="`/artist?id=${row.artistIds.split(',')[index]}`"
										class="artist-link"
									>
										{{ name }}
									</router-link>
									<span v-if="index < row.artistNames.split(',').length - 1">, </span>
								</span>
							</template>
						</el-table-column>
					</el-table>
				</div>
			</div>
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
	import { ref, watch, onMounted } from 'vue';
	import { ElMessage } from 'element-plus';
	import { useRoute, useRouter } from 'vue-router';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { useAuthStore } from '@/stores/frontend/auth';
	import {
		getPlayListDetail,
		getSystemTopLists,
	} from '@/api/frontend/playlist';
	import { inject } from 'vue';
	import { formatSeconds as formatDuration } from '@/utils/format.js';
	import { useSongAction } from '@/composables/useSongAction';

	const playerStore = usePlayerStore();
	const route = useRoute();
	const router = useRouter();
	const authStore = useAuthStore();
	// 获取父组件传递的播放器引用
	const playerBarRef = inject('playerBar');
	const { dialogVisible, createdPlaylists, openAddToPlaylistDialog, handleSelectPlaylist, handleAddToNext: useHandleAddToNext } = useSongAction(playerStore, playerBarRef, authStore);
	const getToplistId = () => {
		return Number(route.query.id) || 1;
	};
	const currentTopListId = ref(getToplistId());
	const currentTopListData = ref(null);
	watch(
		() => route.query.id,
		(newVal) => {
			currentTopListId.value = Number(newVal) || 1;
			loadData();
		}
	);
	const handlePlayAll = () => {
		if(currentTopListData.value.songs.length == 0) {
			return;
		}
		playerStore.playAll(currentTopListData.value.songs);
		playerBarRef.value.showBar();
		setTimeout(() => {
			playerBarRef.value?.scheduleHide();
		}, 1000);
	};

	const handleAddToNext = (song) => {
		useHandleAddToNext(song, true);
	};

	function loadData() {
		getPlayListDetail(currentTopListId.value)
			.then((res) => {
				if (!res.data.code) {
					ElMessage.error('系统异常，请稍后再试');
				} else {
					currentTopListData.value = res.data.data;
				}
			})
			.catch((err) => {
				ElMessage.error('系统异常，请稍后再试');
			});
	}
	// 时间格式化方法
	const formatTime = (timeString) => {
		return timeString?.slice(0, 16) || '最近';
	};

	const topList = ref([]);

	function loadTopList() {
		getSystemTopLists()
			.then((res) => {
				if (res.data.code) {
					topList.value = res.data.data.map((item) => ({
						topListId: item.playlistId,
						topListName: item.playlistName,
						topListCoverUrl: item.coverUrl,
					}));
				}
			})
			.catch(() => {
				ElMessage.error('获取榜单列表失败');
			});
	}
	const toTopList = (id) => {
		router.push({
			path: '/discover/topList',
			query: { id: id },
		});
	};
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
	onMounted(() => {
		loadTopList();
		loadData();
	});

	// 收藏处理函数（使用 composable）
	const handleFavorite = (song) => {
		openAddToPlaylistDialog(song);
	};
</script>

<style lang="scss" scoped>
	.topList-container {
		width: 100%;

		.main-container {
			width: 1000px;
			margin: 0 auto;
			display: flex;

			.left-container {
				flex: 3;
				background: #f9f9f9;
				border: 1.5px solid #dcdcdc;

				.topList-box {
					.header {
						padding: 10px 20px 2px;
					}

					.topList-grid {
						flex-direction: column;

						.topList-item {
							padding: 10px;
							display: flex;
							align-items: center;
							cursor: pointer;
							gap: 20px;
							&.active {
								background: #e6e6e6 !important;

								&::after {
									content: '';
									position: absolute;
									right: 0;
									top: 50%;
									transform: translateY(-50%);
									border: 6px solid transparent;
								}

								.name {
									color: #409eff;
									font-weight: 500;
								}
							}

							&:hover {
								background: #f0f0f0; // 调整hover颜色与active区分
							}
							.cover {
								width: 45px;
								height: 45px;
							}
							.name {
								font-size: 14px;
							}
						}
					}
				}
			}

			.right-container {
				padding: 20px;
				display: flex;
				flex: 7;
				flex-direction: column;

				.top-info {
					display: flex;
					margin-bottom: 30px;

					.cover-box {
						margin-right: 30px;

						.cover {
							width: 150px;
							height: 150px;
						}
					}

					.info-box {
						flex: 1;

						.title {
							font-size: 24px;
							margin: 10px 0 15px;
						}

						.update-time {
							color: #666;
							margin-bottom: 25px;
							font-size: 14px;
						}

						.actions {
							:deep(.el-button) {
								padding: 10px 20px;
								width: 70px;

								i {
									margin-right: 5px;
									margin: 0 auto;
								}
							}
						}
					}
				}

				.song-list {
					flex: 1;
					.song-link {
						color: #606266;
						text-decoration: none;
						transition: color 0.3s;

						&:hover {
							color: red; // 保持与歌手链接相同的红色
							text-decoration: underline;
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
							left: 0px; // 调整原有按钮位置
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

						&:hover {
							.duration-text {
								opacity: 0;
							}
							.addnext-btn,
							.favorite-btn {
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
			}
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
	// 在 <style> 部分添加
	:deep(.active-play-icon) {
		color: #409eff !important; // 使用 Element 的主题蓝色
		svg {
			fill: currentColor; // 确保 SVG 图标颜色生效
		}
	}
</style>
