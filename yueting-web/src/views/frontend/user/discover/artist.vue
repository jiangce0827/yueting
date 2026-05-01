<template>
	<div class="main-container">
		<!-- 左侧导航 -->
		<div class="left-side">
			<div v-for="(group, index) in navGroups" :key="index" class="nav-group">
				<h3 class="group-title">{{ group.title }}</h3>
				<ul class="sub-list">
					<li
						v-for="(item, i) in group.items"
						:key="i"
						class="sub-item"
						:class="{ active: currentNav === item }"
						@click="handleNavClick(item)"
					>
						{{ item.label }}
					</li>
				</ul>
				<div v-if="index !== navGroups.length - 1" class="divider"></div>
			</div>
		</div>

		<!-- 右侧内容 -->
		<div class="right-side">
			<!-- 歌手列表 -->
			<div v-if="loading" class="loading">加载中...</div>
			<div v-else-if="currentArtists.length" class="artist-grid">
				<div
					v-for="(artist, index) in currentArtists"
					:key="index"
					class="artist-card"
					@click="$router.push(`/artist?id=${artist.artistId}`)"
				>
					<img :src="artist.artistAvatarUrl" class="avatar" />
					<div class="name">{{ artist.artistName }}</div>
					<div class="type">{{ artist.type }}</div>
				</div>
			</div>
			<div v-else class="empty">暂无相关歌手</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, reactive, onMounted, watch } from 'vue';
	import { apiSearchArtist } from '@/api/frontend/artist';
	import { useRouter, useRoute } from 'vue-router';
	const router = useRouter();
	const route = useRoute();
	// 左侧导航数据
	const navGroups = reactive([
		{
			title: '华语',
			items: [
				{ label: '华语男歌手', language: 1, gender: 1 },
				{ label: '华语女歌手', language: 1, gender: 2 },
				{ label: '华语组合/乐队', language: 1, gender: 0 },
			],
		},
		{
			title: '欧美',
			items: [
				{ label: '欧美男歌手', language: 2, gender: 1 },
				{ label: '欧美女歌手', language: 2, gender: 2 },
				{ label: '欧美组合/乐队', language: 2, gender: 0 },
			],
		},
		{
			title: '日本',
			items: [
				{ label: '日本男歌手', language: 3, gender: 1 },
				{ label: '日本女歌手', language: 3, gender: 2 },
				{ label: '日本组合/乐队', language: 3, gender: 0 },
			],
		},
		{
			title: '韩国',
			items: [
				{ label: '韩国男歌手', language: 4, gender: 1 },
				{ label: '韩国女歌手', language: 4, gender: 2 },
				{ label: '韩国组合/乐队', language: 4, gender: 0 },
			],
		},
		{
			title: '其他',
			items: [
				{ label: '其他男歌手', language: 0, gender: 1 },
				{ label: '其他女歌手', language: 0, gender: 2 },
				{ label: '其他组合/乐队', language: 0, gender: 0 },
			],
		},
	]);
	const currentItem = ref(null);
	const currentArtists = ref([]);
	const loading = ref(false);
	// 当前选中导航
	const currentNav = ref('华语男歌手');
	// 监听路由参数变化

	// 导航点击处理
	const handleNavClick = async (item) => {
		router.push({
			path: '/discover/artist',
			query: {
				language: item.language,
				gender: item.gender,
			},
		});
	};
	const loadArtists = async (params) => {
		try {
			loading.value = true;
			const res = await apiSearchArtist({
				language: Number(params.language) || 0,
				gender: Number(params.gender) || 0,
			});
			currentArtists.value = res.data.code === 1 ? res.data.data.records : [];
		} catch (error) {
			console.error('加载失败:', error);
			currentArtists.value = [];
		} finally {
			loading.value = false;
		}
	};
	// 更新当前激活导航
	const updateActiveNav = () => {
		const findNav = () => {
			for (const group of navGroups) {
				for (const item of group.items) {
					if (
						item.language === Number(route.query.language) &&
						item.gender === Number(route.query.gender)
					) {
						return item;
					}
				}
			}
			// 默认返回第一个
			return navGroups[0].items[0];
		};

		currentItem.value = findNav();
	};
	watch(
		() => route.query,
		async (newQuery) => {
			await loadArtists(newQuery);
			updateActiveNav();
		},
		{ immediate: true }
	);

	// 初始化时处理现有参数
	onMounted(() => {
		if (!route.query.language || !route.query.gender) {
			router.replace({
				path: '/discover/artist',
				query: {
					language: navGroups[0].items[0].language,
					gender: navGroups[0].items[0].gender,
				},
			});
		}
	});
</script>

<style lang="scss" scoped>
	.main-container {
		max-width: 1000px;
		margin: 0 auto;
		display: flex;
		min-height: 100vh;
	}

	.left-side {
		width: 20%;
		padding: 20px;
		background: #f8f9fa;
		border-right: 1px solid #e0e0e0;

		.nav-group {
			padding: 12px 0;

			.group-title {
				font-size: 14px;
				color: #666;
				margin-bottom: 8px;
			}

			.sub-list {
				list-style: none;
				padding: 0;
				margin: 0;

				.sub-item {
					padding: 8px 12px;
					font-size: 13px;
					color: #333;
					cursor: pointer;
					border-radius: 4px;
					transition: all 0.2s;

					&:hover {
						background: #e9ecef;
					}

					&.active {
						background: #e34d40;
						color: white;
					}
				}
			}

			.divider {
				height: 1px;
				background: #e0e0e0;
				margin: 12px 0;
			}
		}
	}

	.right-side {
		flex: 1;
		padding: 20px;
		background: white;

		.loading,
		.empty {
			padding: 40px;
			text-align: center;
			color: #666;
			font-size: 14px;
		}
		.artist-grid {
			display: grid;
			grid-template-columns: repeat(5, 1fr);
			gap: 30px;

			.artist-card {
				cursor: pointer;
				text-align: center;

				.avatar {
					width: 100%;
					aspect-ratio: 1;
					border-radius: 8px;
					object-fit: cover;
					margin-bottom: 8px;
				}

				.name {
					font-size: 14px;
					color: #333;
				}

				.type {
					font-size: 12px;
					color: #999;
				}
			}
		}

		.loading {
			&:after {
				content: '...';
				animation: dots 1s infinite;
			}
		}

		@keyframes dots {
			0%,
			20% {
				content: '.';
			}
			40% {
				content: '..';
			}
			60%,
			100% {
				content: '...';
			}
		}
	}

	@media (max-width: 768px) {
		.artist-grid {
			grid-template-columns: repeat(3, 1fr) !important;
		}
	}
	.sub-item.active {
		background: #e34d40;
		color: white;
	}
</style>
