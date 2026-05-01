<template>
	<span class="tags" v-if="identities.includes('歌手')">歌手</span>
	<span class="tags" v-if="identities.includes('作词')">作词</span>
	<span class="tags" v-if="identities.includes('作曲')">作曲</span>
</template>

<script setup>
	import { defineProps } from 'vue';

	const props = defineProps({
		identities: {
			type: Array,
			default: () => [],
		},
	});
</script>

<style scoped>
	.tags {
		font-size: 14px;
		color: rgba(0, 0, 0, 0.4);
		border: solid 1px rgba(0, 0, 0, 0.3);
		border-radius: 2px;
		margin-left: 5px;
        padding: 2px 6px;

	}
</style>
