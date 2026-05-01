<template>
	<router-view />
</template>

<script setup>
</script>

<style>
	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}

	/* 始终显示滚动条，防止页面切换时布局抖动 */
	html,
	body {
		overflow-y: auto;
	}
</style>
