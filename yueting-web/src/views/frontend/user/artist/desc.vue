<template>
	<div class="artist-introduction">
		<div class="title-wrapper">
			<h2>{{ artistInfo.artistName }}简介</h2>
		</div>
		<div class="bio-content">
			{{ artistInfo.artistBio }}
		</div>
		<div class="detail-content" v-html="formatDescription(artistInfo.artistDescription)"></div>
	</div>
</template>
<script setup>
	import { ref, watch } from 'vue';
	import { useRoute } from 'vue-router';
	import { apiGetArtistBasicByArtistId } from '@/api/frontend/artist';
	const route = useRoute();
	const artistInfo = ref({
		artistName: '',
		artistBio: '',
		artistDescription: '',
	});
	const formatDescription = (html) => {
		if(!html) return '';
		return html
			.replace(/\n/g, '<br>') // 转换换行符
			.replace(/<h2>/g, '<h2 class="section-title">'); // 可选：给标题添加样式
	};
	watch(() => route.query.id, async () => {
		const res = await apiGetArtistBasicByArtistId(route.query.id);
		artistInfo.value = res.data.data;
	}, { immediate: true });
</script>

<style scoped>
	/* 艺人介绍样式 */
	.title-wrapper h2 {
		position: relative;
		padding-left: 15px;
		margin-bottom: 10px;
	}

	.title-wrapper h2::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 4px;
		height: 70%;
		background: #e74c3c;
	}

	.bio-content {
		text-indent: 1em;
		line-height: 1.8;
		color: #666;
		margin-bottom: 10px;
	}

	.detail-content {
		line-height: 1.8;
		color: #444;
	}
</style>
