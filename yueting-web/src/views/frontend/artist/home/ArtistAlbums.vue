<template>
	<div class="artist-albums">
		<!-- 加载状态 -->
		<div v-if="loading" class="loading">专辑加载中...</div>

		<!-- 错误状态 -->
		<div v-if="error" class="error">专辑加载失败，请稍后重试</div>

		<!-- 数据展示 -->
		<div v-else class="album-grid">
			<div v-for="album in albums" @click="$router.push(`/musician/artist/album/${album.albumId}`)" :key="album.albumId" class="album-item">
				<div class="album-cover">
					<img :src="album.coverUrl || defaultCover" alt="专辑封面" />
				</div>
				<div class="album-title">{{ album.albumName }}</div>
				<div class="album-date">{{ formatDate(album.releaseDate) }}</div>
			</div>
		</div>

		<!-- 分页组件 -->
		<el-pagination
			v-if="total > 0"
			background
			layout="prev, pager, next"
			:total="total"
			:page-size="pageSize"
			@current-change="handlePageChange"
		/>
	</div>
</template>

<script setup>
	import { ref, watch } from 'vue';
	import { apiGetAlbumsByArtistId } from '@/api/frontend/album';
	import {useRouter,useRoute} from 'vue-router'
	const router = useRouter();
	const route = useRoute();

	// 响应式数据
	const albums = ref([]);
	const loading = ref(false);
	const error = ref(false);
	const pageNum = ref(1);
	const pageSize = ref(12);
	const total = ref(0);
	const defaultCover = 'https://example.com/default-album-cover.jpg';

	// 日期格式化
	const formatDate = (dateStr) => {
		if (!dateStr) return '未知日期';
		const date = new Date(dateStr);
		return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
	};

	// 获取专辑数据
	const fetchAlbums = async () => {
		try {
			loading.value = true;
			error.value = false;
			const res = await apiGetAlbumsByArtistId({
				artistId: route.query.id,
				pageNum: pageNum.value,
				pageSize: pageSize.value,
			});

			albums.value = res.data.data.records;
			total.value = res.data.data.total;
		} catch (err) {
			console.error('获取专辑失败:', err);
			error.value = true;
		} finally {
			loading.value = false;
		}
	};

	// 分页变化处理
	const handlePageChange = (newPage) => {
		pageNum.value = newPage;
		fetchAlbums();
	};

	// 监听artistId变化
	watch(
		() => route.query.id,
		(newVal) => {
			if (newVal) {
				pageNum.value = 1;
				fetchAlbums();
			}
		},
		{ immediate: true }
	);
</script>

<style lang="scss" scoped>
	.artist-albums {
		padding: 20px 0;

		.album-grid {
			display: grid;
			grid-template-columns: repeat(4, 1fr);
			gap: 30px;
			margin-bottom: 40px;

			.album-item {
				cursor: pointer;
				transition: transform 0.3s ease;

				&:hover {
					transform: translateY(-5px);
				}

				.album-cover {
					aspect-ratio: 1;
					border-radius: 8px;
					overflow: hidden;
					margin-bottom: 12px;
					box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

					img {
						width: 100%;
						height: 100%;
						object-fit: cover;
					}
				}

				.album-title {
					font-size: 14px;
					color: #333;
					font-weight: 500;
					margin-bottom: 6px;
				}

				.album-date {
					font-size: 12px;
					color: #888;
				}
			}
		}

		.el-pagination {
			justify-content: center;
			margin-top: 30px;
		}

		.loading,
		.error {
			text-align: center;
			padding: 40px;
			color: #999;
		}
	}
</style>
