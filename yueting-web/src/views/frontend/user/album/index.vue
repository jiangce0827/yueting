<template>
	<div class="artist-container">
		<!-- 主内容 -->
		<div class="main-content">
			<!-- 左侧内容 -->
			<div class="left-content">
				<div class="left-main">
					<!-- 专辑信息 -->
					<div class="album-info">
						<div class="album-header">
							<img :src="albumData.coverUrl" class="album-cover" />
							<div class="album-meta">
								<div class="name-edit-wrapper">
									<h1 class="album-name">{{ albumData.albumName }}</h1>
								</div>
								<div class="release-date">发行时间：{{ formatDate(albumData.releaseDate) }}</div>
								<div class="artists" v-if="albumData.artistNames">
									歌手：{{ albumData.artistNames.split(',').join(' / ') }}
								</div>
							</div>
						</div>
						<div class="album-description">
							<h3>专辑介绍</h3>
							<p>{{ albumData.description || '暂无专辑介绍' }}</p>
						</div>
					</div>

					<!-- 歌曲列表 -->
					<div class="song-list">
						<div class="list-header">
							<h3>歌曲列表</h3>
							<span class="song-count">{{ albumData.songs?.length || 0 }}首歌</span>
						</div>
						<el-table :data="albumData.songs" stripe style="width: 100%">
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
							<el-table-column label="标题" width="200">
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
							<el-table-column label="专辑">
								<template #default="{ row }">
									<router-link
										v-if="row.albumId"
										:to="`/album?id=${row.albumId}`"
										class="album-link"
									>
										{{ row.albumName }}
									</router-link>
									<span v-else>{{ row.albumName || '未知专辑' }}</span>
								</template>
							</el-table-column>
						</el-table>
					</div>
				</div>
			</div>

			<!-- 右侧预留 -->
			<div class="right-content">
				<HotArtist />
			</div>
		</div>

		<!-- 评论区域 -->
		<div class="comment-area-wrapper">
			<CommentSection :target-type="3" :target-id="albumData.albumId" />
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
	import { ref, onMounted, onUnmounted, inject, watch } from 'vue';
	import { apiGetAlbumWithSongsByAlbumId } from '@/api/frontend/album';
	import { useRouter, useRoute } from 'vue-router';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { usePlayerStore } from '@/stores/frontend/player';
	import HotArtist from '/src/components/frontend/HotArtist.vue';
	import CommentSection from '/src/components/frontend/CommentSection.vue';
	import { useAuthStore } from '@/stores/frontend/auth';
	import {
		getPlayListDetail,
		apiDeleteSongFromPlaylist,
		apiCollectPlaylist,
	} from '@/api/frontend/playlist';
	import { formatSeconds as formatDuration } from '@/utils/format.js';
	import { useSongAction } from '@/composables/useSongAction';

	const playerBarRef = inject('playerBar');
	const authStore = useAuthStore();

	const route = useRoute();
	const playerStore = usePlayerStore();
	const { dialogVisible, createdPlaylists, openAddToPlaylistDialog, handleSelectPlaylist, handleAddToNext: useHandleAddToNext } = useSongAction(playerStore, playerBarRef, authStore);

	const albumData = ref({
		albumName: '',
		coverUrl: '',
		releaseDate: '',
		description: '',
		songs: [],
	});
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
	// 格式化日期
	const formatDate = (date) => {
		return new Date(date).toLocaleDateString();
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

	const loadAlbumData = async (albumId) => {
		if (!albumId) return;
		try {
			const res = await apiGetAlbumWithSongsByAlbumId(albumId);
			albumData.value = res.data.data;
		} catch (error) {
			console.error('获取专辑详情失败:', error);
		}
	};

	onMounted(() => {
		loadAlbumData(route.query.id);
	});

	watch(() => route.query.id, (newId) => {
		loadAlbumData(newId);
	});

</script>
<style lang="scss" scoped>
	// 定义变量
	$primary-color: #d43c33;
	$text-color: #333;
	$secondary-text: #666;
	$bg-hover: #f5f5f5;
	$box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
	$border-radius: 8px;

	.artist-container {
		width: 100%;

		// 主内容区
		.main-content {
			width: 1000px;
			margin: 0 auto;
			display: flex;
			gap: 30px;

			.left-content {
				display: flex;
				flex: 3;
				background: white;
				padding: 10px 20px;
				align-items: center;
				flex-direction: column;
				.left-main {
					.album-header {
						display: flex;
						gap: 30px;
						margin-bottom: 30px;

						.album-cover {
							width: 200px;
							height: 200px;
							border-radius: 8px;
							box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
						}

						.album-meta {
							flex: 1;

							.name-edit-wrapper {
								display: flex;
								align-items: center;
								gap: 12px;
								margin-bottom: 15px;

								.album-name {
									font-size: 28px;
									margin: 0;
								}

								.edit-icon {
									font-size: 30px;
									color: #999;
									cursor: pointer;
									transition: all 0.3s;
									padding: 4px;
									border-radius: 4px;

									&:hover {
										color: #409eff;
										background-color: #f5f7fa;
									}
								}
							}
							.release-date,
							.artists {
								font-size: 14px;
								color: #666;
								margin-bottom: 8px;
							}
						}
					}

					.album-description {
						padding: 20px 0;
						border-top: 1px solid #eee;

						h3 {
							font-size: 18px;
							margin-bottom: 15px;
						}

						p {
							line-height: 1.8;
							color: #666;
						}
					}

					.song-list {
						margin-top: 40px;
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
						.album-link {
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

						.list-header {
							display: flex;
							align-items: center;
							margin-bottom: 20px;

							h3 {
								font-size: 18px;
								margin: 0;
							}

							.song-count {
								margin-left: 15px;
								color: #666;
								font-size: 14px;
							}
						}

						.el-button--danger {
							padding: 8px 12px;
							text-decoration: none;

							&:hover {
								background-color: #f56c6c;
								border-color: #f56c6c;
							}
						}
					}

					.play-icon {
						color: #ec4141;
						margin-right: 10px;
						cursor: pointer;
						font-size: 18px;
					}

					.song-index {
						color: #999;
					}
				}
			}

			// 右侧内容
			.right-content {
				flex: 1;
				padding: 20px;
				background: #fff;
				border-radius: $border-radius;
				box-shadow: $box-shadow;

				.artist-box {
					.artist-header {
						display: flex;
						justify-content: space-between;
						align-items: center;
						margin-bottom: 20px;

						h3 {
							font-size: 16px;
							margin: 0;
						}

						.view-all {
							font-size: 12px;
							color: $secondary-text;
							text-decoration: none;
							transition: color 0.3s;

							&:hover {
								color: $primary-color;
							}
						}
					}

					.artist-list {
						display: flex;
						flex-direction: column;
						gap: 15px;

						.artist-item {
							display: flex;
							align-items: center;
							padding: 8px;
							border-radius: $border-radius - 2;
							transition: background 0.3s;
							cursor: pointer;

							&:hover {
								background: $bg-hover;
							}

							.avatar {
								width: 62px;
								height: 62px;
								margin-right: 12px;
								object-fit: cover;
							}

							.name {
								font-size: 14px;
								color: $text-color;
								font-weight: 500;
							}
						}
					}
				}
			}
		}
	}

	// 混合宏定义
	@mixin multi-line-ellipsis($lines) {
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: $lines;
		-webkit-box-orient: vertical;
	}

	// 评论区域样式
	.comment-area-wrapper {
		width: 1000px;
		margin: 20px auto 0;
		background: #fff;
		padding: 20px;
		border-radius: $border-radius;
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
