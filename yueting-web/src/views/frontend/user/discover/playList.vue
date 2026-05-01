<template>
	<div class="playlist-container">
		<!-- 分类选择栏 -->
		<div class="filter-bar">
			<div class="left-filter">
				<span class="current-tag">{{ currentTagName || '全部' }}</span>
				<el-select
					v-model="currentTag"
					placeholder="选择分类"
					@change="handleTagChange"
					popper-class="multi-col-select"
				>
					<el-option v-for="tag in tags" :key="tag.tagId" :label="tag.name" :value="tag.tagId" />
				</el-select>
			</div>
		</div>

		<!-- 分割线 -->
		<div class="divider"></div>

		<!-- 歌单列表 -->
		<div class="playlist-grid">
			<div v-if="loading" class="loading">加载中...</div>
			<div
				v-else-if="playLists.length"
				v-for="(item, index) in playLists"
				:key="index"
				@click="$router.push(`/playlist?id=${item.playlistId}`)"
				class="playlist-item"
			>
				<img :src="item.coverUrl" class="cover" />
				<div class="title">{{ item.playlistName }}</div>
				<div class="author">by {{ item.userName }}</div>
			</div>
			<div v-else class="empty">暂无相关歌手</div>
		</div>

		<!-- 分页器 -->
		<div class="pagination">
			<el-pagination
				background
				layout="prev, pager, next"
				:total="total"
				:page-size="pageSize"
				:current-page="pageNum"
				@current-change="handlePageChange"
			/>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, computed, watch } from 'vue';
	import { apiSearchPlaylistByTag } from '@/api/frontend/playlist';
	import { apiGetAllTags } from '@/api/frontend/tag';
	import { useRoute, useRouter } from 'vue-router';
	const route = useRoute();
	const router = useRouter();
	// 修改状态初始化方式
	const currentTag = ref('');
	const loading = ref(false);
	const pageNum = ref(Number(route.query.pageNum) || 1);
	const pageSize = ref(Number(route.query.pageSize) || 35);
	// 分类数据
	const tags = ref([]);
	const currentTagName = computed(() => {
		const tag = tags.value.find((t) => t.tagId == currentTag.value);
		return tag ? tag.name : '全部';
	});
	// 歌单数据
	const playLists = ref([]);
	const total = ref(0);

	// 获取歌单列表
	const getPlayLists = async () => {
		loading.value = true;
		try {
			const params = {
				tagId: currentTag.value,
				pageNum: pageNum.value,
				pageSize: pageSize.value,
			};
			const res = await apiSearchPlaylistByTag(params);
			playLists.value = res.data.data.records;
			total.value = res.data.data.total;
		} catch (error) {
			console.error('获取歌单失败:', error);
		}
		loading.value = false;
	};
	watch(
		() => route.query,
		(newQuery) => {
			// 同步参数到状态
			currentTag.value = newQuery.tag || '';
			pageNum.value = Math.max(Number(newQuery.pageNum) || 1, 1);
			pageSize.value = Math.max(Number(newQuery.pageSize) || 35, 1);

			// 自动触发数据加载
			getPlayLists();
		},
		{ immediate: true }
	);
	// 获取标签数据时初始化当前分类
	const getTags = async () => {
		try {
			const res = await apiGetAllTags();
			tags.value = res.data.data;
			// 将每个 tagId 转换为字符串类型
			tags.value = tags.value.map((tag) => ({
				...tag,
				tagId: String(tag.tagId),
			}));
			// 标签加载完成后，再设置当前分类（确保能找到名称）
			currentTag.value = route.query.tag || '';
		} catch (error) {
			console.error('获取分类失败:', error);
		}
	};

	// 分类改变处理
	const handleTagChange = () => {
		router.push({
			query: {
				...route.query,
				tag: currentTag.value,
				pageNum: 1, // 切换分类时重置到第一页
				pageSize: pageSize.value,
			},
		});
	};

	// 分页改变处理
	const handlePageChange = (val) => {
		router.push({
			query: {
				...route.query,
				pageNum: val,
				pageSize: pageSize.value,
			},
		});
	};
	onMounted(() => {
		getTags();
	});
</script>

<style lang="scss" scoped>
	.playlist-container {
		max-width: 1000px;
		margin: 0 auto;
		padding: 20px;
		background: white;

		.filter-bar {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 20px;

			.left-filter {
				display: flex;
				align-items: center;
				gap: 15px;

				.current-tag {
					font-size: 16px;
					color: #333;
				}

				:deep(.el-select) {
					width: 150px;
				}

				.hot-btn {
					margin-left: 20px;
				}
			}
		}

		.divider {
			border-top: 2px solid #d43c33;
			margin: 20px 0;
		}

		.playlist-grid {
			display: grid;
			grid-template-columns: repeat(5, 140px);
			justify-content: space-around;
			gap: 15px;

			.playlist-item {
				width: 140px;
				cursor: pointer;
				transition: transform 0.3s;

				&:hover {
					transform: translateY(-5px);
				}

				.cover {
					width: 140px;
					height: 140px;
					object-fit: cover;
					margin-bottom: 8px;
				}

				.title {
					font-size: 13px;
					color: #333;
					line-height: 1.4;
					height: 20px;
					width: 140px;
					overflow: hidden;
					text-overflow: ellipsis;
					display: -webkit-box;
					-webkit-box-orient: vertical;
				}

				.author {
					font-size: 12px;
					color: #999;
					margin-top: 5px;
				}
			}
		}

		.pagination {
			margin-top: 30px;
			display: flex;
			justify-content: center;
		}
	}

	@media (max-width: 1200px) {
		.playlist-grid {
			grid-template-columns: repeat(5, 1fr) !important;
		}
	}

	@media (max-width: 768px) {
		.playlist-grid {
			grid-template-columns: repeat(3, 1fr) !important;
		}
	}
	.loading,
	.empty {
		text-align: center;
		color: #666;
		font-size: 14px;
	}
</style>
<style lang="scss">
	/* 添加全局样式或使用深度选择器 */
	.multi-col-select {
		.el-select-dropdown__list {
			display: grid;
			grid-template-columns: repeat(5, 1fr);
			gap: 1px;
			max-height: 300px; /* 适当调整最大高度 */

			.el-select-dropdown__item {
				padding: 0 20px;
				white-space: nowrap;
				position: relative;

				&:nth-child(5n + 1) {
					grid-column: 1;
				}
				&:nth-child(5n + 2) {
					grid-column: 2;
				}
				&:nth-child(5n + 3) {
					grid-column: 3;
				}

				/* 鼠标悬浮样式微调 */
				&:hover {
					background: #f5f7fa;
				}
			}
		}

		/* 调整下拉框宽度 */
		.el-select-dropdown {
			min-width: 360px;
		}

		/* 选中状态图标位置调整 */
		.selected {
			.el-icon-check {
				left: 5px;
				right: auto;
			}
		}
	}

	/* 移动端适配 */
	@media (max-width: 768px) {
		.multi-col-select {
			.el-select-dropdown__list {
				grid-template-columns: repeat(2, 1fr);
			}
			.el-select-dropdown {
				min-width: 240px;
			}
		}
	}
</style>
