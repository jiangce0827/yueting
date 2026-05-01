<!-- ArtistResults.vue -->
<template>
	<div class="artist-grid">
		<div v-for="(item, index) in data" :key="index" class="song-item">
			<div class="index">{{ index + 1 }}</div>
			<div class="info">
				<div class="name">{{ item.songName }}</div>
				<div class="artist">{{ item.artistNames }} - {{ item.albumName }}</div>
			</div>
			<div class="duration">{{ item.duration }}</div>
		</div>
	</div>
</template>

<script setup>
	defineProps({
		data: {
			type: Array,
			default: () => [],
		},
	});
</script>

<style lang="scss" scoped>
	.artist-grid {
		display: grid;
		grid-template-columns: repeat(5, 1fr);
		gap: 20px;
		.song-item {
			display: flex;
			align-items: center;
			padding: 12px;
			border-bottom: 1px solid #eee;
		}
		.index {
			width: 40px;
			color: #999;
		}

		.info {
			flex: 1;

			.name {
				font-size: 14px;
			}

			.artist {
				font-size: 12px;
				color: #666;
			}
		}

		.duration {
			width: 60px;
			text-align: right;
			color: #666;
		}
	}
</style>
