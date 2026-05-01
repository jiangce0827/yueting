<template>
	<div class="artist-box">
		<!-- 标题行 -->
		<div class="artist-header">
			<h3>热门歌手</h3>
			<router-link to="/discover/artist" class="view-all">查看全部</router-link>
		</div>

		<!-- 歌手列表 -->
		<div class="artist-list">
			<div v-for="artist in artists" :key="artist.artistId" class="artist-item">
				<router-link :to="`/artist?id=${artist.artistId}`" class="artist-router">
					<img :src="artist.artistAvatarUrl" class="avatar" alt="歌单封面" />
					<div class="name">{{ artist.artistName }}</div>
				</router-link>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, watch } from 'vue';
	import { apiSearchArtist } from '@/api/frontend/artist';

	const artists = ref([]);

	onMounted(async () => {
		const artistRes = await apiSearchArtist();
		artists.value = artistRes.data.data.records;
	});
</script>

<style lang="scss" scoped>
	$primary-color: #d43c33;
	$text-color: #333;
	$secondary-text: #666;
	$bg-hover: #f5f5f5;
	$box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
	$border-radius: 8px;
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

				.artist-router {
					display: flex;
					align-items: center;
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
</style>
