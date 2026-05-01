<template>
	<div class="work-manage-page">
		<div class="page-header">
			<div>
				<h1>作品管理</h1>
				<p>集中管理你的歌曲与专辑，快速查看作品规模和审核状态。</p>
			</div>
			<div class="header-actions">
				<el-button @click="refreshData" :loading="loading">刷新数据</el-button>
				<el-button type="primary" @click="goUpload">上传作品</el-button>
			</div>
		</div>

		<div class="stats-grid">
			<div class="stat-card">
				<div class="stat-label">正常歌曲数</div>
				<div class="stat-value">{{ stats.songCount }}</div>
				<div class="stat-desc">默认对外可见的歌曲数量</div>
			</div>
			<div class="stat-card">
				<div class="stat-label">已删除歌曲</div>
				<div class="stat-value">{{ stats.deletedSongCount }}</div>
				<div class="stat-desc">软删除后仅音乐人侧可见</div>
			</div>
			<div class="stat-card">
				<div class="stat-label">专辑总数</div>
				<div class="stat-value">{{ stats.albumCount }}</div>
				<div class="stat-desc">当前账号下创建的全部专辑</div>
			</div>
			<div class="stat-card">
				<div class="stat-label">累计播放</div>
				<div class="stat-value">{{ formatCount(stats.totalPlayCount) }}</div>
				<div class="stat-desc">仅统计当前正常歌曲的播放量</div>
			</div>
		</div>

		<div class="panel-card">
			<div class="toolbar">
				<el-input
					v-model="searchKeyword"
					placeholder="搜索歌曲、专辑或艺人名称"
					clearable
					class="search-input"
				>
					<template #prefix>
						<el-icon><Search /></el-icon>
					</template>
				</el-input>
				<el-select v-model="selectedAlbumId" clearable placeholder="全部专辑" class="album-filter">
					<el-option
						v-for="album in albumOptions"
						:key="album.albumId"
						:label="album.albumName"
						:value="album.albumId"
					/>
				</el-select>
				<el-select v-model="songStatusFilter" placeholder="歌曲状态" class="status-filter">
					<el-option label="仅看正常" value="normal" />
					<el-option label="仅看已删除" value="deleted" />
					<el-option label="查看全部" value="all" />
				</el-select>
				<el-button @click="resetFilters">重置筛选</el-button>
			</div>

			<div v-if="loading" class="state-block">
				<el-skeleton :rows="8" animated />
			</div>

			<div v-else-if="errorMessage" class="state-block">
				<el-empty :description="errorMessage">
					<el-button type="primary" @click="refreshData">重新加载</el-button>
				</el-empty>
			</div>

			<div v-else>
				<el-tabs v-model="activeTab">
					<el-tab-pane :label="`歌曲管理 (${filteredSongs.length})`" name="songs">
						<template v-if="filteredSongs.length">
							<el-table :data="filteredSongs" stripe class="data-table">
								<el-table-column label="歌曲" min-width="300">
									<template #default="{ row }">
										<div class="song-cell">
											<img :src="row.coverUrl || defaultCover" class="song-cover" />
											<div class="song-meta">
												<div class="song-name-row">
													<div class="song-name">{{ row.songName || '未命名歌曲' }}</div>
													<el-tag
														v-if="isDeletedSong(row)"
														type="info"
														size="small"
														effect="plain"
													>
														已下架
													</el-tag>
												</div>
												<div class="song-subtitle">{{ formatArtists(row.artistNames) }}</div>
											</div>
										</div>
									</template>
								</el-table-column>
								<el-table-column label="所属专辑" min-width="180">
									<template #default="{ row }">
										<span>{{ row.albumName || '未归属专辑' }}</span>
									</template>
								</el-table-column>
								<el-table-column label="时长" width="110">
									<template #default="{ row }">
										{{ formatDurationValue(row.duration) }}
									</template>
								</el-table-column>
								<el-table-column label="播放量" width="120" align="center">
									<template #default="{ row }">
										{{ formatCount(row.playCount || 0) }}
									</template>
								</el-table-column>
								<el-table-column label="发布时间" width="180">
									<template #default="{ row }">
										{{ formatDateTime(getSongPublishTime(row)) }}
									</template>
								</el-table-column>
								<el-table-column label="操作" width="220" fixed="right">
									<template #default="{ row }">
										<div class="table-actions">
											<el-button link type="primary" @click="viewAlbum(row.albumId)">
												查看专辑
											</el-button>
											<el-button
												v-if="!isDeletedSong(row)"
												link
												type="danger"
												@click="deleteSong(row)"
											>
												删除歌曲
											</el-button>
											<span v-else class="deleted-hint">已软删除</span>
										</div>
									</template>
								</el-table-column>
							</el-table>
						</template>
						<template v-else>
							<el-empty description="暂无匹配的歌曲">
								<el-button type="primary" @click="goUpload">去上传作品</el-button>
							</el-empty>
						</template>
					</el-tab-pane>

					<el-tab-pane :label="`专辑管理 (${filteredAlbums.length})`" name="albums">
						<template v-if="filteredAlbums.length">
							<div class="album-grid">
								<div v-for="album in filteredAlbums" :key="album.albumId" class="album-card">
									<div class="album-cover-wrap" @click="viewAlbum(album.albumId)">
										<img :src="album.coverUrl || defaultCover" class="album-cover" />
									</div>
									<div class="album-body">
										<div class="album-name-row">
											<div class="album-name">{{ album.albumName || '未命名专辑' }}</div>
											<el-tag size="small" type="info">
												{{ getAlbumSongSummary(album) }}
											</el-tag>
										</div>
										<div class="album-meta-line">发行时间：{{ formatDate(album.releaseDate) }}</div>
										<div class="album-meta-line">
											累计播放：{{ formatCount(getAlbumPlayCount(album)) }}
										</div>
										<div class="album-meta-line clamp-2">
											{{ album.description || '这个专辑还没有填写简介。' }}
										</div>
									</div>
									<div class="album-actions">
										<el-button @click="viewAlbum(album.albumId)">查看</el-button>
										<el-button type="primary" plain @click="editAlbum(album.albumId)">
											编辑
										</el-button>
										<el-button type="danger" plain @click="deleteAlbum(album)">
											删除
										</el-button>
									</div>
								</div>
							</div>
						</template>
						<template v-else>
							<el-empty description="暂无匹配的专辑">
								<el-button type="primary" @click="goUpload">创建你的第一张专辑</el-button>
							</el-empty>
						</template>
					</el-tab-pane>

					<el-tab-pane :label="`审核记录 (${filteredReviewApplications.length})`" name="reviews">
						<template v-if="filteredReviewApplications.length">
							<el-table :data="filteredReviewApplications" stripe class="data-table">
								<el-table-column label="歌曲名称" min-width="220">
									<template #default="{ row }">
										<div class="song-meta">
											<div class="song-name">{{ row.songName || '未命名歌曲' }}</div>
											<div class="song-subtitle">{{ formatApplicationRoles(row) }}</div>
										</div>
									</template>
								</el-table-column>
								<el-table-column label="所属专辑" min-width="180">
									<template #default="{ row }">
										<span>{{ getAlbumNameById(row.albumId) }}</span>
									</template>
								</el-table-column>
								<el-table-column label="提交时间" width="180">
									<template #default="{ row }">
										{{ formatDateTime(row.appliedAt) }}
									</template>
								</el-table-column>
								<el-table-column label="审核状态" width="120" align="center">
									<template #default="{ row }">
										<el-tag :type="getReviewStatusTagType(row.status)" effect="light">
											{{ getReviewStatusText(row.status) }}
										</el-tag>
									</template>
								</el-table-column>
								<el-table-column label="审核说明" min-width="260">
									<template #default="{ row }">
										<div class="review-note">
											<div v-if="Number(row.status) === 2">
												{{ row.rejectionReason || '未填写驳回原因' }}
											</div>
											<div v-else-if="Number(row.status) === 1" class="approved-text">
												审核已通过，歌曲已进入正式作品库。
											</div>
											<div v-else class="pending-text">平台正在审核，请耐心等待。</div>
										</div>
									</template>
								</el-table-column>
								<el-table-column label="审核时间" width="180">
									<template #default="{ row }">
										{{ formatDateTime(row.reviewedAt) }}
									</template>
								</el-table-column>
							</el-table>
						</template>
						<template v-else>
							<el-empty description="当前没有歌曲审核记录" />
						</template>
					</el-tab-pane>
				</el-tabs>
			</div>
		</div>
	</div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

import {
	apiDeleteAlbum,
	apiGetMyAlbumWithSongsByAlbumId,
	apiSearchMyAlbums,
} from '@/api/frontend/album';
import { apiDeleteSong, apiGetMySongApplications } from '@/api/frontend/song';
import { formatSeconds } from '@/utils/format';

const router = useRouter();
const route = useRoute();

const loading = ref(false);
const searchKeyword = ref('');
const selectedAlbumId = ref('');
const songStatusFilter = ref('normal');
const activeTab = ref('songs');
const errorMessage = ref('');
const albums = ref([]);
const reviewApplications = ref([]);
const defaultCover = 'https://p1.music.126.net/8of6lK0Q6_4dJmP3YfQw1A==/109951169311172503.jpg';

const STATUS_USABLE = 0;
const STATUS_DELETED = 1;

const normalizeSong = (song, album, detail) => ({
	...song,
	albumId: song.albumId ?? detail?.albumId ?? album.albumId,
	albumName: song.albumName ?? detail?.albumName ?? album.albumName,
	coverUrl: song.coverUrl ?? detail?.coverUrl ?? album.coverUrl,
	status: Number(song.status ?? STATUS_USABLE),
});

const normalizeAlbum = (album, detail) => ({
	albumId: detail?.albumId ?? album.albumId,
	albumName: detail?.albumName ?? album.albumName,
	coverUrl: detail?.coverUrl ?? album.coverUrl,
	description: detail?.description ?? album.description,
	releaseDate: detail?.releaseDate ?? album.releaseDate,
	artistNames: detail?.artistNames ?? '',
	songs: Array.isArray(detail?.songs)
		? detail.songs.map((song) => normalizeSong(song, album, detail))
		: [],
});

const refreshData = async () => {
	loading.value = true;
	errorMessage.value = '';
	try {
		const albumRes = await apiSearchMyAlbums();
		const baseAlbums = Array.isArray(albumRes.data.data) ? albumRes.data.data : [];

		const details = await Promise.allSettled(
			baseAlbums.map((album) => apiGetMyAlbumWithSongsByAlbumId(album.albumId))
		);

		albums.value = baseAlbums.map((album, index) => {
			const detailResult = details[index];
			if (detailResult?.status === 'fulfilled') {
				return normalizeAlbum(album, detailResult.value.data.data);
			}
			return normalizeAlbum(album, null);
		});

		const reviewRes = await apiGetMySongApplications({
			pageNum: 1,
			pageSize: 100,
			status: -1,
		});
		reviewApplications.value = Array.isArray(reviewRes.data.data?.records)
			? reviewRes.data.data.records
			: [];
	} catch (error) {
		console.error('加载作品管理数据失败:', error);
		errorMessage.value = '作品数据加载失败，请稍后重试';
	} finally {
		loading.value = false;
	}
};

const albumOptions = computed(() => albums.value);

const filteredAlbums = computed(() => {
	const keyword = searchKeyword.value.trim().toLowerCase();
	return albums.value.filter((album) => {
		const matchAlbum =
			!selectedAlbumId.value || String(album.albumId) === String(selectedAlbumId.value);
		if (!matchAlbum) return false;
		if (!keyword) return true;
		return [album.albumName, album.artistNames, album.description]
			.filter(Boolean)
			.some((item) => String(item).toLowerCase().includes(keyword));
	});
});

const matchesSongStatus = (song) => {
	if (songStatusFilter.value === 'all') return true;
	if (songStatusFilter.value === 'deleted') return Number(song.status) === STATUS_DELETED;
	return Number(song.status) !== STATUS_DELETED;
};

const filteredSongs = computed(() => {
	const keyword = searchKeyword.value.trim().toLowerCase();
	return filteredAlbums.value.flatMap((album) =>
		(album.songs || []).filter((song) => {
			if (!matchesSongStatus(song)) return false;
			if (!keyword) return true;
			return [song.songName, song.artistNames, song.albumName]
				.filter(Boolean)
				.some((item) => String(item).toLowerCase().includes(keyword));
		})
	);
});

const filteredReviewApplications = computed(() => {
	const keyword = searchKeyword.value.trim().toLowerCase();
	return reviewApplications.value.filter((item) => {
		if (![0, 1, 2].includes(Number(item.status))) {
			return false;
		}
		if (selectedAlbumId.value && String(item.albumId) !== String(selectedAlbumId.value)) {
			return false;
		}
		if (!keyword) {
			return true;
		}
		return [
			item.songName,
			item.singerNames,
			item.lyricistNames,
			item.composerNames,
			item.rejectionReason,
			getAlbumNameById(item.albumId),
		]
			.filter(Boolean)
			.some((value) => String(value).toLowerCase().includes(keyword));
	});
});

const stats = computed(() => {
	const allSongs = albums.value.flatMap((album) => album.songs || []);
	const usableSongs = allSongs.filter((song) => Number(song.status) !== STATUS_DELETED);
	const deletedSongs = allSongs.filter((song) => Number(song.status) === STATUS_DELETED);
	return {
		songCount: usableSongs.length,
		deletedSongCount: deletedSongs.length,
		albumCount: albums.value.length,
		totalPlayCount: usableSongs.reduce((sum, song) => sum + Number(song.playCount || 0), 0),
	};
});

const isDeletedSong = (song) => Number(song?.status) === STATUS_DELETED;

const getAlbumPlayCount = (album) =>
	(album.songs || [])
		.filter((song) => !isDeletedSong(song))
		.reduce((sum, song) => sum + Number(song.playCount || 0), 0);

const getAlbumSongSummary = (album) => {
	const songs = album.songs || [];
	const usableCount = songs.filter((song) => !isDeletedSong(song)).length;
	const deletedCount = songs.length - usableCount;
	if (!deletedCount) {
		return `${usableCount} 首歌`;
	}
	return `${usableCount} 首正常 / ${deletedCount} 首已删`;
};

const formatCount = (count) => {
	const value = Number(count || 0);
	if (value >= 10000) {
		return `${(value / 10000).toFixed(1)}万`;
	}
	return `${value}`;
};

const formatDate = (dateString) => {
	if (!dateString) return '未设置';
	return String(dateString).slice(0, 10);
};

const formatDateTime = (dateString) => {
	if (!dateString) return '未记录';
	return String(dateString).replace('T', ' ').slice(0, 19);
};

const getSongPublishTime = (song) => song?.createAt || song?.createdAt || '';

const formatArtists = (artistNames) => artistNames || '未填写艺人信息';

const getAlbumNameById = (albumId) => {
	const target = albums.value.find((album) => String(album.albumId) === String(albumId));
	return target?.albumName || '未匹配到专辑';
};

const formatDurationValue = (seconds) => {
	if (seconds == null) return '--:--';
	return formatSeconds(Number(seconds));
};

const getReviewStatusText = (status) => {
	if (Number(status) === 0) return '审核中';
	if (Number(status) === 1) return '已通过';
	if (Number(status) === 2) return '未通过';
	return '其他';
};

const getReviewStatusTagType = (status) => {
	if (Number(status) === 0) return 'warning';
	if (Number(status) === 1) return 'success';
	if (Number(status) === 2) return 'danger';
	return 'info';
};

const formatApplicationRoles = (row) => {
	const roles = [];
	if (row.singerNames) roles.push(`演唱：${row.singerNames.split(',').join(' / ')}`);
	if (row.lyricistNames) roles.push(`作词：${row.lyricistNames.split(',').join(' / ')}`);
	if (row.composerNames) roles.push(`作曲：${row.composerNames.split(',').join(' / ')}`);
	return roles.join(' | ') || '未填写参与信息';
};

const resetFilters = () => {
	searchKeyword.value = '';
	selectedAlbumId.value = '';
	songStatusFilter.value = 'normal';
};

const goUpload = () => {
	router.push('/musician/artist/songUpload');
};

const viewAlbum = (albumId) => {
	if (!albumId) return;
	router.push(`/musician/artist/album/${albumId}`);
};

const editAlbum = (albumId) => {
	if (!albumId) return;
	router.push(`/musician/artist/album/edit/${albumId}`);
};

const deleteSong = async (song) => {
	try {
		await ElMessageBox.confirm(
			`确认删除歌曲《${song.songName || '未命名歌曲'}》吗？删除后会从用户搜索和播放入口中隐藏，但你仍可在这里查看已删除记录。`,
			'删除歌曲',
			{
				confirmButtonText: '确认删除',
				cancelButtonText: '取消',
				type: 'warning',
			}
		);
		const res = await apiDeleteSong(song.songId);
		if (res.data.code !== 1) {
			throw new Error(res.data.msg || '删除失败');
		}
		albums.value = albums.value.map((album) => ({
			...album,
			songs: (album.songs || []).map((item) =>
				String(item.songId) === String(song.songId)
					? { ...item, status: STATUS_DELETED }
					: item
			),
		}));
		ElMessage.success('歌曲已软删除');
	} catch (error) {
		if (error !== 'cancel' && error !== 'close') {
			ElMessage.error(error.message || '删除歌曲失败');
		}
	}
};

const deleteAlbum = async (album) => {
	try {
		await ElMessageBox.confirm(
			`确认删除专辑《${album.albumName || '未命名专辑'}》吗？专辑下的歌曲会一并软删除。`,
			'删除专辑',
			{
				confirmButtonText: '确认删除',
				cancelButtonText: '取消',
				type: 'warning',
			}
		);
		const res = await apiDeleteAlbum(album.albumId);
		if (res.data.code !== 1) {
			throw new Error(res.data.msg || '删除失败');
		}
		albums.value = albums.value.filter((item) => String(item.albumId) !== String(album.albumId));
		ElMessage.success('专辑已删除');
	} catch (error) {
		if (error !== 'cancel' && error !== 'close') {
			ElMessage.error(error.message || '删除专辑失败');
		}
	}
};

const syncActiveTabFromRoute = (tab) => {
	if (['songs', 'albums', 'reviews'].includes(tab)) {
		activeTab.value = tab;
	}
};

onMounted(() => {
	syncActiveTabFromRoute(route.query.tab);
	refreshData();
});

watch(
	() => route.query.tab,
	(tab) => {
		syncActiveTabFromRoute(tab);
	}
);
</script>

<style lang="scss" scoped>
.work-manage-page {
	padding: 28px;
	background: linear-gradient(180deg, #f7f9fc 0%, #ffffff 180px);
	min-height: 100%;
}

.page-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 16px;
	margin-bottom: 24px;

	h1 {
		margin: 0 0 8px;
		font-size: 28px;
		color: #1f2a37;
	}

	p {
		margin: 0;
		color: #6b7280;
		font-size: 14px;
	}
}

.header-actions {
	display: flex;
	gap: 12px;
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 16px;
	margin-bottom: 24px;
}

.stat-card,
.panel-card,
.album-card {
	background: #fff;
	border: 1px solid #eef1f6;
	border-radius: 18px;
	box-shadow: 0 12px 32px rgba(15, 23, 42, 0.05);
}

.stat-card {
	padding: 20px 22px;
}

.stat-label {
	font-size: 13px;
	color: #6b7280;
	margin-bottom: 12px;
}

.stat-value {
	font-size: 30px;
	font-weight: 700;
	color: #111827;
	line-height: 1.1;
	margin-bottom: 10px;
}

.stat-desc {
	font-size: 13px;
	color: #9ca3af;
}

.panel-card {
	padding: 20px 20px 10px;
}

.toolbar {
	display: flex;
	gap: 12px;
	align-items: center;
	margin-bottom: 16px;
	flex-wrap: wrap;
}

.search-input {
	width: 320px;
}

.album-filter,
.status-filter {
	width: 220px;
}

.state-block {
	padding: 20px 0 30px;
}

.data-table {
	margin-top: 8px;
}

.song-cell {
	display: flex;
	align-items: center;
	gap: 12px;
}

.song-cover {
	width: 48px;
	height: 48px;
	object-fit: cover;
	border-radius: 12px;
	background: #f3f4f6;
	flex-shrink: 0;
}

.song-meta {
	min-width: 0;
}

.song-name-row {
	display: flex;
	align-items: center;
	gap: 8px;
	margin-bottom: 4px;
}

.song-name {
	font-size: 14px;
	font-weight: 600;
	color: #111827;
}

.song-subtitle {
	font-size: 12px;
	color: #6b7280;
}

.table-actions {
	display: flex;
	align-items: center;
	gap: 12px;
}

.deleted-hint {
	font-size: 12px;
	color: #9ca3af;
}

.album-grid {
	display: grid;
	grid-template-columns: repeat(3, minmax(0, 1fr));
	gap: 18px;
	padding: 8px 0 16px;
}

.album-card {
	padding: 16px;
	display: flex;
	flex-direction: column;
}

.album-cover-wrap {
	cursor: pointer;
	border-radius: 16px;
	overflow: hidden;
	background: #f3f4f6;
	margin-bottom: 16px;
}

.album-cover {
	width: 100%;
	aspect-ratio: 1;
	object-fit: cover;
	display: block;
	transition: transform 0.3s ease;
}

.album-cover-wrap:hover .album-cover {
	transform: scale(1.04);
}

.album-body {
	flex: 1;
}

.album-name-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 10px;
}

.album-name {
	font-size: 17px;
	font-weight: 700;
	color: #111827;
}

.album-meta-line {
	font-size: 13px;
	color: #6b7280;
	line-height: 1.7;
}

.clamp-2 {
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.album-actions {
	display: flex;
	gap: 10px;
	margin-top: 18px;
}

.approved-text {
	color: #059669;
}

.pending-text {
	color: #b45309;
}

@media (max-width: 1200px) {
	.stats-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}

	.album-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}

@media (max-width: 768px) {
	.page-header {
		flex-direction: column;
	}

	.header-actions,
	.toolbar {
		width: 100%;
	}

	.search-input,
	.album-filter,
	.status-filter {
		width: 100%;
	}

	.stats-grid,
	.album-grid {
		grid-template-columns: 1fr;
	}

	.album-actions {
		flex-wrap: wrap;
	}
}
</style>
