<template>
	<div class="artist-container">
		<!-- 主内容 -->
		<div class="main-content">
			<!-- 左侧内容 -->
			<div class="left-content">
				<div class="left-main">
					<div class="name-wrapper">
						<div class="name">{{ artistInfo.artistName }}</div>
						<router-link v-if="artistInfo.userId" :to="`/user/home?id=${artistInfo.userId}`" class="home-button">
							个人主页
						</router-link>
					</div>
					<!-- 背景图区域 -->
					<div class="banner-wrapper">
						<div
							class="artist-banner"
							:style="{ backgroundImage: `url(${artistInfo.artistCoverUrl})` }"
						/>
					</div>
					<!-- 新增选项栏 -->
					<div class="tab-bar">
						<router-link
							v-for="tab in tabs"
							:key="tab.id"
							:to="tab.path"
							class="tab-item"
							exact-active-class="active"
						>
							{{ tab.label }}
						</router-link>
					</div>

					<!-- 子视图容器 -->
					<div class="sub-view">
						<router-view :key="route.fullPath" />
					</div>
				</div>
			</div>

			<!-- 右侧预留 -->
			<div class="right-content">
				<HotArtist />
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, onUnmounted,watch } from 'vue';
	import { apiGetArtistBasicByArtistId } from '@/api/frontend/artist';
	import { useRouter, useRoute } from 'vue-router';
	import { ElMessageBox } from 'element-plus';
	import HotArtist from '/src/components/frontend/HotArtist.vue';
	const router = useRouter();
	const route = useRoute();
	const artistInfo = ref({});
	const syncTabs = (artistId) => {
		tabs[0].path = `/artist?id=${artistId}`;
		tabs[1].path = `/artist/album?id=${artistId}`;
		tabs[2].path = `/artist/desc?id=${artistId}`;
	};
	const tabs = [
		{ path: `/artist?id=${route.query.id}`, label: '热门作品' },
		{ path: `/artist/album?id=${route.query.id}`, label: '所有专辑' },
		{ path: `/artist/desc?id=${route.query.id}`, label: '艺人介绍' },
	];
	onMounted(async () => {
		if (!route.query?.id) {
			ElMessageBox.alert('页面找不到了，请稍后再试', '提示', {
				type: 'error'
			});
			return;
		}
		const res = await apiGetArtistBasicByArtistId(route.query.id);
		artistInfo.value = res.data.data;
	});
	watch(() => route.query.id, (artistId) => {
		if (!artistId) return;
		syncTabs(artistId);
	}, { immediate: true });
	watch(() => route.query.id, async () => { 
		const res = await apiGetArtistBasicByArtistId(route.query.id);
		artistInfo.value = res.data.data;
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
					.name-wrapper {
						display: flex;
						font-size: 20px;
						padding: 8px;
						align-items: center;
						justify-content: space-between;

						.home-button {
							font-size: 14px;
							color: #666;
							text-decoration: none;
							padding: 6px 16px;
							border: 1px solid #ddd;
							border-radius: 20px;
							transition: all 0.3s;

							&:hover {
								border-color: #d43c33;
								color: #d43c33;
							}
						}
					}
					.banner-wrapper {
						display: flex;
						.artist-banner {
							height: 300px;
							width: 600px;
							background-size: cover;
							background-position: center;
							position: relative;
						}
					}
					// 新增样式
					.tab-bar {
						width: 600px;
						display: flex;
						border-bottom: 2px solid #e0e0e0;

						.tab-item {
							flex: 1;
							text-align: center;
							padding: 8px 0;
							font-size: 16px;
							color: #666;
							text-decoration: none;
							transition: all 0.3s;
							position: relative;

							&:hover {
								color: $primary-color;
							}

							&.active {
								color: $primary-color;
								font-weight: 500;

								&::after {
									content: '';
									position: absolute;
									bottom: -2px;
									left: 0;
									right: 0;
									height: 2px;
									background: $primary-color;
								}
							}
						}
					}

					.sub-view {
						width: 600px;
						min-height: 400px;
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
</style>
