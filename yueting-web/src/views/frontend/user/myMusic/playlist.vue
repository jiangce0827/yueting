<!-- @/views/frontend/user/myMusic/playlist.vue -->
<template>
	<div class="main-container">
		<div class="right-container">
			<!-- 歌单信息 -->
			<div class="top-info" v-if="playlistData.playlistId">
				<div class="cover-box">
					<img :src="playlistData.coverUrl" class="cover" />
				</div>
				<div class="info-box">
					<h2 class="title">
						{{ playlistData.playlistName }}
						<el-icon
							v-if="playlistData.userId == authStore.user.userId"
							class="edit-icon"
							@click="handleEdit"
						>
							<edit />
						</el-icon>
					</h2>
					<div class="update-time">创建时间：{{ formatTime(playlistData.createdAt) }}</div>
					<div class="actions">
						<el-button @click="handlePlayAll">
							<el-icon :size="20"><videoPlay /></el-icon>
							播放
						</el-button>

						<!-- 只有不是自己创建的歌单才显示收藏按钮 -->
						<template v-if="playlistData.userId != authStore.user?.userId">
							<el-button v-if="!isCollected" @click="handleCollectPlaylist" :loading="isCollecting">
								<el-icon :size="20"><folderAdd /></el-icon>
								收藏
							</el-button>
							<el-button v-else @click="handleCancelCollect" :loading="isCollecting">
								<el-icon :size="20"><FolderChecked /></el-icon>
								取消收藏
							</el-button>
						</template>
					</div>
					<!-- 新增标签展示 -->
					<div class="tags-box" v-if="playlistData.tagNames">
						<el-tag
							v-for="(tag, index) in playlistData.tagNames.split(',')"
							:key="index"
							type="info"
							size="small"
							class="tag-item"
						>
							{{ tag }}
						</el-tag>
						<span v-if="!playlistData.tagNames" class="no-tags">暂无标签</span>
					</div>

					<!-- 新增介绍展示 -->
					<div class="description-box">
						{{ playlistData.description || '暂无介绍' }}
					</div>
				</div>
			</div>

			<!-- 歌曲列表 -->
			<div class="song-list" v-if="playlistData.songs?.length">
				<el-table :data="playlistData.songs" stripe style="width: 100%">
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
								<!-- 新增删除按钮 -->
								<el-button
									v-if="authStore.user != null && playlistData.userId == authStore.user?.userId"
									class="delete-btn"
									link
									@click.stop="handleDelete(scope.row)"
								>
									<el-icon :size="20" color="#F56C6C"><delete /></el-icon>
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
	import { ref, watch, onMounted, inject } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import {
		getPlayListDetail,
		apiDeleteSongFromPlaylist,
		apiCollectPlaylist,
		apiCancelCollectPlaylist,
		apiGetCollectPlayListsByUserId,
	} from '@/api/frontend/playlist';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { formatSeconds as formatDuration } from '@/utils/format.js';
	import { useSongAction } from '@/composables/useSongAction';

	const route = useRoute();
	const router = useRouter();
	const likelistId = inject('likelistId');
	const playerStore = usePlayerStore();
	const authStore = useAuthStore();
	const playerBarRef = inject('playerBar');
	const { dialogVisible, createdPlaylists, openAddToPlaylistDialog, handleSelectPlaylist, handleAddToNext: useHandleAddToNext } = useSongAction(playerStore, playerBarRef, authStore);

	const currentPlayListId = ref(route.query.id || null); // 立即初始化

	const playlistData = ref({});

	// 时间格式化方法
	const formatTime = (timeString) => {
		if (!timeString) return '未知时间';
		try {
			return new Date(timeString)
				.toLocaleString('zh-CN', {
					year: 'numeric',
					month: '2-digit',
					day: '2-digit',
					hour: '2-digit',
					minute: '2-digit',
				})
				.replace(/\//g, '-');
		} catch {
			return '未知时间';
		}
	};

	// 加载数据
	const loadData = async () => {
		try {
			const res = await getPlayListDetail(currentPlayListId.value);
			playlistData.value = res.data.data;
			// 检查是否已收藏
			await checkIsCollected();
		} catch (error) {
			ElMessage.error('获取歌单详情失败');
		}
	};
	// 收藏处理函数
	const handleFavorite = (song) => {
		openAddToPlaylistDialog(song);
	};
	// 播放处理
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

	const handleDelete = (song) => {
		ElMessageBox.confirm('确认删除该歌曲？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				try {
					const res = await apiDeleteSongFromPlaylist(playlistData.value.playlistId, song.songId);
					if (res.data.code === 1) {
						ElMessage.success('删除成功');
						// 重新加载数据
						await loadData();
					} else {
						ElMessage.error(res.data.msg || '请稍后再试');
					}
				} catch (error) {
					ElMessage.error('删除失败');
				}
			})
			.catch(() => {});
	};

	const handleEdit = () => {
		router.push(`/my/edit?id=${playlistData.value.playlistId}`);
	};

	const handlePlayAll = () => {
		playerStore.playAll(playlistData.value.songs);
		playerBarRef.value.showBar();
		setTimeout(() => {
			playerBarRef.value?.scheduleHide();
		}, 1000);
	};

	// 收藏歌单
	const isCollecting = ref(false);
	const isCollected = ref(false);

	// 检查是否已收藏
	const checkIsCollected = async () => {
		try {
			const res = await apiGetCollectPlayListsByUserId(authStore.user.userId);
			if (res.data.code === 1) {
				const collectedList = res.data.data || [];
				isCollected.value = collectedList.some(
					(item) => item.playlistId == currentPlayListId.value
				);
			}
		} catch (error) {
			console.error('检查收藏状态失败:', error);
		}
	};
	const handleCollectPlaylist = async () => {
		try {
			isCollecting.value = true;
			const res = await apiCollectPlaylist(playlistData.value.playlistId);
			if (res.data.code === 1) {
				ElMessage.success('收藏成功');
				isCollected.value = true;
			} else {
				ElMessage.warning(res.data.msg || '收藏失败');
			}
		} catch (error) {
			ElMessage.error('网络请求失败');
		} finally {
			isCollecting.value = false;
		}
	};

	// 取消收藏歌单
	const handleCancelCollect = async () => {
		try {
			isCollecting.value = true;
			const res = await apiCancelCollectPlaylist(playlistData.value.playlistId);
			if (res.data.code === 1) {
				ElMessage.success('已取消收藏');
				isCollected.value = false;
			} else {
				ElMessage.warning(res.data.msg || '取消收藏失败');
			}
		} catch (error) {
			ElMessage.error('网络请求失败');
		} finally {
			isCollecting.value = false;
		}
	};

	// onMounted(loadData);

	watch(
		[() => route.query.id, () => likelistId.value],
		([newRouteId, newLikelistId]) => {
			// 优先使用路由中的 ID
			if (newRouteId) {
				currentPlayListId.value = newRouteId;
				loadData();
			} else if (newLikelistId) {
				// 无路由 ID 时使用 likelistId
				currentPlayListId.value = newLikelistId;
				router.replace({
					path: '/my/playlist',
					query: { id: newLikelistId },
				});
			}
		},
		{ immediate: true }
	);
</script>

<style scoped lang="scss">
	.main-container {
		width: 100%;

		.right-container {
			height: 100vh;
			display: flex;
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
						font-size: 20px;
						margin: 0px 0 5px;

						.edit-icon {
							margin-left: 10px;
							cursor: pointer;
							color: #666;
							transition: color 0.3s;

							&:hover {
								color: #409eff;
							}
						}
					}

					.update-time {
						color: #666;
						margin-bottom: 8px;
						font-size: 14px;
					}

					.actions {
						:deep(.el-button) {
							padding: 8px 12px;
						}
					}
					.tags-box {
						margin: 8px 0;
						display: flex;
						flex-wrap: wrap;
						gap: 8px;

						.tag-item {
							margin-right: 8px;
							border-radius: 12px;
							padding: 2px 10px;
						}

						.no-tags {
							color: #909399;
							font-size: 14px;
						}
					}

					.description-box {
						color: #606266;
						font-size: 14px;
						line-height: 1.6;
						margin-bottom: 20px;
						max-width: 600px;
						white-space: pre-wrap;
					}
				}
			}

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
