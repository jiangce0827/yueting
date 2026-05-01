<!-- 通用结果组件模板 -->
<template>
	<div class="playlist-grid">
		<div
			v-for="(item, index) in data"
			:key="index"
			@click="$router.push(`/album?id=${item.albumId}`)"
			class="playlist-item"
		>
			<img :src="item.coverUrl" class="cover" />
			<div class="title">{{ item.albumName }}</div>
			<div class="author">by {{ item.userName }}</div>
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
</style>
