<template>
	<div class="search-container">
		<div class="main-content">
			<!-- 搜索框 -->
			<div class="search-bar">
				<el-input
					v-model="keyword"
					placeholder="请输入搜索内容"
					clearable
					@keyup.enter="handleSearch"
				>
					<template #append>
						<el-button icon="el-icon-search" @click="handleSearch" />
					</template>
				</el-input>
			</div>

			<!-- 选项卡 -->
			<div class="tabs">
				<div
					v-for="(tab, index) in tabs"
					:key="index"
					class="tab-item"
					:class="{ active: currentType === index }"
					@click="changeType(index)"
				>
					{{ tab }}
				</div>
			</div>

			<!-- 搜索结果 -->
			<div class="result-container">
				<div v-if="loading" class="loading">加载中...</div>
				<div v-else-if="error" class="error">{{ error }}</div>

				<template v-else>
					<!-- 根据类型显示不同内容 -->
					<song-results v-if="currentType === 0" :data="resultData" :key="currentType"/>
					<artist-results v-else-if="currentType === 1" :data="resultData" />
					<album-results v-else-if="currentType === 2" :data="resultData" />
					<lyric-results v-else-if="currentType === 3" :data="resultData" />
					<playlist-results v-else-if="currentType === 4" :data="resultData" />
					<user-results v-else-if="currentType === 5" :data="resultData" />
					<div v-if="!resultData.length" class="empty">暂无搜索结果</div>
				</template>
				<!-- 分页器 -->
				<el-pagination
					v-if="resultData.length > 0"
					class="pagination"
					background
					layout="prev, pager, next, jumper"
					:current-page="pagination.pageNum"
					:page-size="pagination.pageSize"
					:total="pagination.total"
					@current-change="handlePageChange"
				/>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, watch } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { apiSearchSongByKeyword } from '@/api/frontend/song';
	import { apiSearchArtist } from '@/api/frontend/artist';
	import { apiSearchAlbumByKeyword } from '@/api/frontend/album';
	import { apiSearchSongByLyrics } from '@/api/frontend/song';
	import { apiSearchPlaylistByKeyword } from '@/api/frontend/playlist';
	import { apiSearchUserByKeyword } from '@/api/frontend/user';
	import SongResults from '@/components/frontend/user/search/SongResults.vue';
	import ArtistResults from '@/components/frontend/user/search/ArtistResults.vue';
	import AlbumResults from '@/components/frontend/user/search/AlbumResults.vue';
	import LyricResults from '@/components/frontend/user/search/LyricResults.vue';
	import PlaylistResults from '@/components/frontend/user/search/PlaylistResults.vue';
	import UserResults from '@/components/frontend/user/search/UserResults.vue';
	const route = useRoute();
	const router = useRouter();
	// 选项卡配置
	const tabs = ['单曲', '歌手', '专辑', '歌词', '歌单', '用户'];

	// 响应式数据
	const keyword = ref(route.query.keyword || '');
	const currentType = ref(Number(route.query.type) || 0);
	const resultData = ref([]);
	const loading = ref(false);
	const error = ref(null);
	const pagination = ref({
		pageNum: 1,
		pageSize: 30, // 默认值（单曲）
		total: 0,
	});
	// 添加类型与分页大小的映射
	const pageSizeMap = {
		0: 30, // 单曲
		1: 50, // 歌手
		2: 20, // 专辑
		3: 30, // 歌词
		4: 20, // 歌单
		5: 20, // 用户
	};
	// 搜索处理
	const handleSearch = () => {
		router.push({
			path: '/search',
			query: {
				keyword: keyword.value.trim(),
				type: currentType.value,
			},
		});
	};

	// 执行搜索
	const doSearch = async () => {
		if (!keyword.value.trim()) {
			resultData.value = [];
			return;
		}

		try {
			loading.value = true;
			error.value = null;
			// 根据类型获取分页大小
			const currentPageSize = pageSizeMap[currentType.value];
			const params = {
				keyword: keyword.value.trim(),
				pageNum: pagination.value.pageNum,
				pageSize: currentPageSize,
			};
			let res;
			// 根据类型调用不同接口
			switch (currentType.value) {
				case 0: // 单曲
					res = await apiSearchSongByKeyword(params);
					break;
				case 1: // 歌手
					res = await apiSearchArtist(params);
					break;
				case 2: // 专辑
					res = await apiSearchAlbumByKeyword(params);
					break;
				case 3: // 歌词
					res = await apiSearchSongByLyrics(params);
					break;
				case 4: // 歌单
					res = await apiSearchPlaylistByKeyword(params);
					break;
				case 5: // 用户
					res = await apiSearchUserByKeyword(params);
					break;
				default:
					throw new Error('未知的搜索类型');
			}

			// 统一处理响应格式
			if (res.data.code === 1) {
				resultData.value = res.data.data?.records || [];
				pagination.value.total = res.data.data?.total || 0;
				// 保持当前分页大小
				pagination.value.pageSize = currentPageSize;
			} else {
				throw new Error(res.message || '搜索失败');
			}
		} catch (e) {
			error.value = e.message || '搜索失败，请稍后重试';
			console.error('搜索错误:', e);
			resultData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// 监听路由参数变化
	watch(
		() => route.query,
		(newQuery) => {
			keyword.value = newQuery.keyword || '';
			currentType.value = Number(newQuery.type) || 0;
			if (keyword.value) {
				doSearch();
			}
		},
		{ immediate: true }
	);
	// 分页变化处理
	const handlePageChange = (newPage) => {
		pagination.value.pageNum = newPage;
		doSearch();
	};

	// 修改切换类型的逻辑（切换类型时重置页码）
	const changeType = (type) => {
		currentType.value = type;
		pagination.value.pageNum = 1; // 重置为第一页
		router.push({
			path: '/search',
			query: {
				keyword: keyword.value,
				type: type,
			},
		});
	};
</script>

<style lang="scss" scoped>
	.search-container {
		max-width: 1200px;
		margin: 0 auto;

		.main-content {
			width: 1000px;
			padding: 0 30px;
			margin: 0 auto;
			background: white;
		}
	}

	.search-bar {
		padding: 40px 0;
		display: flex;
		justify-content: center;

		.el-input {
			width: 600px;
		}
	}

	.tabs {
		display: flex;
		border-bottom: 1px solid #e0e0e0;
		margin-bottom: 20px;

		.tab-item {
			padding: 12px 40px;
			cursor: pointer;
			color: #666;
			transition: all 0.3s;

			&:hover {
				color: #c3473a;
			}

			&.active {
				color: #c3473a;
				border-bottom: 2px solid #c3473a;
				font-weight: 500;
			}
		}
	}

	.result-container {
		min-height: 400px;

		.loading,
		.error,
		.empty {
			padding: 40px;
			text-align: center;
			color: #666;
			font-size: 16px;
		}
	}

	/* 响应式 */
	@media (max-width: 768px) {
		.search-bar .el-input {
			width: 100%;
		}

		.tabs {
			overflow-x: auto;

			.tab-item {
				padding: 8px 12px;
				white-space: nowrap;
			}
		}
	}
	.pagination {
		padding-top: 20px;
	}
</style>
