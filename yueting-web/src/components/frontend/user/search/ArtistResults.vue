<!-- ArtistResults.vue -->
<template>
	<div class="artist-grid">
		<div
			v-for="(item, index) in data"
			:key="index"
			class="artist-card"
			@click="$router.push(`/artist?id=${item.artistId}`)"
		>
			<img :src="item.artistAvatarUrl" class="avatar" />
			<div class="name">{{ item.artistName }}</div>
		</div>
	</div>
</template>

<script setup>
	import { useRouter } from 'vue-router';
	const router = useRouter();
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

		.artist-card {
			text-align: center;
			cursor: pointer;

			.avatar {
				width: 75px;
				aspect-ratio: 1;
				margin-bottom: 8px;
			}

			.name {
				font-size: 14px;
			}

			.fans {
				font-size: 12px;
				color: #666;
			}
		}
	}
</style>
