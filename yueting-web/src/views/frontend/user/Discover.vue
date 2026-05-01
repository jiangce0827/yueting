<template>
	<div class="home-container">
		<!-- 导航栏 -->
		<div class="nav-bar">
			<div class="nav-content">
				<div
					v-for="tab in tabs"
					:key="tab.path"
					class="nav-item"
					:class="{ active: isTabActive(tab.path) }"
					@click="switchTab(tab.path)"
				>
					{{ tab.label }}
				</div>
			</div>
		</div>

		<!-- 主内容区 -->
		<router-view></router-view>
	</div>
</template>

<script setup>
	import { useRouter, useRoute } from 'vue-router';

	const router = useRouter();
	const route = useRoute();

	const tabs = [
		{ path: '/discover', label: '推荐' },
		{ path: '/discover/topList', label: '排行榜' },
		{ path: '/discover/playlist', label: '歌单' },
		{ path: '/discover/artist', label: '歌手' },
	];

	const switchTab = (tabPath) => {
		router.push(tabPath);
	};

	const isTabActive = (tabPath) => route.path === tabPath;
</script>


<style scoped>
	.home-container {
		min-height: 100vh;
	}

	.nav-bar {
		background: #d43c33;
		width: 100%;
		height: 40px;
	}

	.nav-content {
		width: 1000px;
		margin: 0 auto;
		display: flex;
		height: 100%;
		justify-content: center;
	}

	.nav-item {
		color: white;
		padding: 0 30px;
		display: flex;
		margin: 0 30px;
		font-size: 16px;
		cursor: pointer;
		display: flex;
		align-items: center;
		transition: background 0.3s;
	}

	.nav-item.active {
		background: #9b0909;
	}

	.nav-item:hover {
		background: #c20c0c;
	}
</style>
