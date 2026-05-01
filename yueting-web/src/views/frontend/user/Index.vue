<!-- @/components/frontend/user/Index.vue -->
<template>
	<div class="app-container">
		<NavBar />
		<div class="main-content">
			<router-view />
		</div>
		<div
			style="
				height: 60px;
				padding: 10px;
				display: flex;
				align-items: flex-end;
				justify-content: center;
			"
		></div>
		<PlayerBar ref="playerBarRef" />
	</div>
</template>

<script setup>
	import { ref,provide  } from 'vue';
	import NavBar from '@/components/frontend/NavBar.vue';
	import PlayerBar from '@/components/frontend/PlayerBar.vue';
	const playerBarRef = ref(null);
	// 提供播放器引用给所有子组件
	provide('playerBar', playerBarRef);
</script>

<style scoped>
	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}

	.app-container {
		min-height: 100vh;
		background: #f5f5f5;
		min-width: 1000px;
	}

	.main-content {
		min-height: calc(100vh - 220px);
	}

	/* 后台样式隔离 */
	.admin-container * {
		all: initial; /* 重置所有样式 */
		/* 重新定义后台专用样式 */
		box-sizing: border-box;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
	}
</style>
