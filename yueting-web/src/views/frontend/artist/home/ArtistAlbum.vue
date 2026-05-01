<!-- AlbumDetail.vue -->
<template>
	<div class="album-detail">
		<div class="left-panel">
			<!-- 专辑信息 -->
			<div class="album-info">
				<div class="album-header">
					<img :src="albumData.coverUrl" class="album-cover" />
					<div class="album-meta">
						<div class="name-edit-wrapper">
							<h1 class="album-name">{{ albumData.albumName }}</h1>
							<el-icon class="edit-icon" @click="handleEdit">
								<Edit />
							</el-icon>
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
				<el-table :data="albumData.songs" style="width: 100%">
					<el-table-column width="50">
						<template #default="{ $index }">
							<span class="song-index">{{ $index + 1 }}</span>
						</template>
					</el-table-column>
					<el-table-column label="歌曲标题" min-width="200">
						<template #default="{ row }">
							<div class="song-title">
								<i class="play-icon el-icon-video-play"></i>
								{{ row.songName }}
							</div>
						</template>
					</el-table-column>
					<el-table-column prop="duration" label="时长" width="120">
						<template #default="{ row }">
							{{ formatDuration(row.duration) }}
						</template>
					</el-table-column>
					<el-table-column label="歌手" min-width="180">
						<template #default="{ row }">
							{{ row.artistNames.split(',').join(' / ') }}
						</template>
					</el-table-column>
					<el-table-column label="操作" width="120" align="center">
						<template #default="{ row }">
							<el-button type="danger" size="small" @click="handleDelete(row.songId)"
								>删除
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</div>
		</div>
		<div class="right-panel">
			<!-- 右侧预留区域 -->
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted } from 'vue';
	import { useRoute } from 'vue-router';
	import { apiGetAlbumWithSongsByAlbumId } from '@/api/frontend/album';
	import { Edit } from '@element-plus/icons-vue'; // 新增图标导入
	import { useRouter } from 'vue-router'; // 新增路由导入
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { apiDeleteSong } from '@/api/frontend/song'; // 请确保接口路径正确
	import { formatSeconds as formatDuration } from '@/utils/format.js';

	const router = useRouter(); // 获取路由实例
	const route = useRoute();
	const albumData = ref({
		albumName: '',
		coverUrl: '',
		releaseDate: '',
		description: '',
		songs: [],
	});

	// 新增编辑处理方法
	const handleEdit = () => {
		router.push(`/musician/artist/album/edit/${albumData.value.albumId}`);
	};
	// 格式化日期
	const formatDate = (date) => {
		return new Date(date).toLocaleDateString();
	};

	const handleDelete = (songId) => {
		ElMessageBox.confirm('确定要删除这首歌曲吗？', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				try {
					await apiDeleteSong(songId);
					// 删除成功后更新歌曲列表
					albumData.value.songs = albumData.value.songs.filter((song) => song.songId !== songId);
					ElMessage.success('删除成功');
				} catch (error) {
					console.error('删除失败:', error);
					ElMessage.error('删除失败');
				}
			})
			.catch(() => {});
	};

	onMounted(async () => {
		const albumId = route.params.albumId;
		try {
			const res = await apiGetAlbumWithSongsByAlbumId(albumId);
			albumData.value = res.data.data;
		} catch (error) {
			console.error('获取专辑详情失败:', error);
		}
	});
</script>

<style lang="scss" scoped>
	.album-detail {
		display: flex;
		gap: 30px;
		max-width: 1200px;
		margin: 0 auto;
		padding: 30px;

		.left-panel {
			flex: 3;
		}

		.right-panel {
			flex: 1;
			background: #f8f8f8;
			border-radius: 8px;
		}

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
</style>
