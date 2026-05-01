<template>
	<div class="recommend-container">
		<div class="banner-container">
			<div class="banner-wrapper">
				<div
					class="banner-bg"
					:style="{ backgroundImage: `url(${bannerList[currentBanner]?.imageUrl})` }"
				></div>
				<div class="banner-content">
					<div class="arrow left" @click="switchBanner(-1)">‹</div>
					<img
						:src="bannerList[currentBanner]?.imageUrl"
						class="banner-img"
						@click="handleBannerClick"
						style="cursor: pointer"
					/>
					<div class="arrow right" @click="switchBanner(1)">›</div>
				</div>
			</div>
		</div>

		<div class="main-content">
			<div class="left-content">
				<div class="recommend-box">
					<div class="header">
						<h3>热门推荐</h3>
						<div class="tags">
							<span @click="$router.push(`/discover/playlist?tag=1`)">华语</span>
							<span @click="$router.push(`/discover/playlist?tag=101`)">流行</span>
							<span @click="$router.push(`/discover/playlist?tag=102`)">摇滚</span>
							<span @click="$router.push(`/discover/playlist?tag=103`)">民谣</span>
							<span @click="$router.push(`/discover/playlist?tag=104`)">电子</span>
							<a class="more" @click="$router.push(`/discover/playlist`)">更多></a>
						</div>
					</div>

					<div class="playlist-grid">
						<div v-for="playlist in playlists" :key="playlist.playlistId" class="playlist-item">
							<router-link :to="`/playlist?id=${playlist.playlistId}`">
								<div class="cover-wrapper">
									<img :src="playlist.coverUrl" class="cover" alt="歌单封面" />
								</div>
								<div class="name">{{ playlist.playlistName }}</div>
							</router-link>
						</div>
					</div>
				</div>
			</div>

			<div class="right-content">
				<HotArtist />
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, onUnmounted } from 'vue';
	import { apiGetHotPlaylistsByPlayCount } from '@/api/frontend/playlist';
	import { apiGetEnabledBanners } from '@/api/frontend/banner';
	import { useRouter } from 'vue-router';
	import HotArtist from '/src/components/frontend/HotArtist.vue';

	const router = useRouter();

	const bannerList = ref([]);
	const currentBanner = ref(0);
	const playlists = ref([]);
	let timer = null;

	const switchBanner = (step) => {
		if (bannerList.value.length === 0) return;
		currentBanner.value =
			(currentBanner.value + step + bannerList.value.length) % bannerList.value.length;
	};

	const handleBannerClick = () => {
		const banner = bannerList.value[currentBanner.value];
		if (!banner) return;

		const { targetType, targetId, targetUrl } = banner;
		switch (targetType) {
			case 1:
				router.push(`/song?id=${targetId}`);
				break;
			case 2:
				router.push(`/album?id=${targetId}`);
				break;
			case 3:
				router.push(`/playlist?id=${targetId}`);
				break;
			case 4:
				if (targetUrl) {
					window.open(targetUrl, '_blank');
				}
				break;
			default:
				break;
		}
	};

	onMounted(async () => {
		try {
			const bannerRes = await apiGetEnabledBanners();
			if (bannerRes.data.data && bannerRes.data.data.length > 0) {
				bannerList.value = bannerRes.data.data;
			}
		} catch (e) {
			console.error('获取轮播图失败:', e);
		}

		timer = setInterval(() => {
			switchBanner(1);
		}, 5000);

		const playlistRes = await apiGetHotPlaylistsByPlayCount();
		playlists.value = playlistRes.data.data;
	});

	onUnmounted(() => {
		clearInterval(timer);
	});
</script>

<style lang="scss" scoped>
	$primary-color: #d43c33;
	$text-color: #333;
	$secondary-text: #666;
	$box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
	$border-radius: 8px;

	.recommend-container {
		width: 100%;

		.banner-container {
			position: relative;
			height: 300px;
			overflow: hidden;

			.banner-wrapper {
				position: absolute;
				width: 100%;
				height: 100%;

				.banner-bg {
					position: absolute;
					width: 100%;
					height: 100%;
					background-size: cover;
					filter: blur(20px);
					transform: scale(1.2);
				}
			}

			.banner-content {
				position: relative;
				width: 1000px;
				height: 100%;
				margin: 0 auto;
				display: flex;
				align-items: center;
				justify-content: space-between;

				.banner-img {
					width: 900px;
					height: 250px;
					object-fit: cover;
					border-radius: $border-radius - 2;
				}

				.arrow {
					color: white;
					font-size: 40px;
					cursor: pointer;
					padding: 0 20px;
					text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);

					&:hover {
						opacity: 0.8;
					}
				}
			}
		}

		.main-content {
			width: 1000px;
			margin: 30px auto;
			display: flex;
			gap: 30px;

			.left-content {
				flex: 3;

				.recommend-box {
					background: white;
					padding: 20px;
					border-radius: $border-radius;
					box-shadow: $box-shadow;

					.header {
						display: flex;
						justify-content: space-between;
						align-items: center;
						margin-bottom: 20px;

						.tags {
							span {
								margin: 0 10px;
								color: $secondary-text;
								cursor: pointer;

								&:hover {
									color: $primary-color;
								}
							}

							.more {
								color: $secondary-text;
								margin-left: 20px;
								text-decoration: none;
								cursor: pointer;
							}
						}
					}

					.playlist-grid {
						display: grid;
						grid-template-columns: repeat(4, 1fr);
						gap: 20px;

						.playlist-item {
							position: relative;
							border-radius: 8px;
							overflow: hidden;
							transition: transform 0.3s ease;

							&:hover {
								transform: translateY(-5px);

								.cover {
									box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
								}
							}

							a {
								text-decoration: none;
								color: inherit;
							}

							.cover-wrapper {
								position: relative;
								padding-top: 100%;
								background: #f5f5f5;
								border-radius: 8px;
								overflow: hidden;

								.cover {
									position: absolute;
									top: 0;
									left: 0;
									width: 100%;
									height: 100%;
									object-fit: cover;
									border-radius: 6px;
									transition: all 0.3s;
								}
							}

							.name {
								margin-top: 10px;
								font-size: 14px;
								color: $text-color;
								line-height: 1.4;
								height: 40px;
								overflow: hidden;
								display: -webkit-box;
								-webkit-line-clamp: 2;
								-webkit-box-orient: vertical;
								padding: 0 5px;
							}
						}
					}
				}
			}

			.right-content {
				flex: 1;
				padding: 20px;
				background: #fff;
				border-radius: $border-radius;
				box-shadow: $box-shadow;
			}
		}
	}

	@mixin multi-line-ellipsis($lines) {
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: $lines;
		-webkit-box-orient: vertical;
	}
</style>
