<!-- @/views/frontend/user/myMusic.vue -->
<template>
	<div class="main-container">
		<!-- 左侧导航 -->
		<div class="left-side">
			<!-- 创建的歌单 -->
			<div class="menu-group">
				<div class="menu-header">
					<span>创建的歌单</span>
					<el-button size="small" @click.stop="showCreateDialog = true"> 新建 </el-button>
				</div>
				<div class="sub-list">
					<div
						v-for="item in createdPlaylists"
						:key="item.playlistId"
						class="playlist-item"
						@click="toPlaylist(item.playlistId)"
					>
						<img :src="item.coverUrl" class="cover" />
						<span class="name">{{ item.playlistName }}</span>
						<el-icon
							v-if="!item.isDefault"
							class="delete-icon"
							@click.stop="handleDelete(item.playlistId, 'created')"
						>
							<Close />
						</el-icon>
					</div>
				</div>
			</div>

			<!-- 收藏的歌单 -->
			<div class="menu-group">
				<div class="menu-header">收藏的歌单</div>
				<div class="sub-list">
					<div
						v-for="item in collectedPlaylists"
						:key="item.id"
						class="playlist-item"
						@click="toPlaylist(item.playlistId)"
					>
						<img :src="item.coverUrl" class="cover" />
						<span class="name">{{ item.playlistName }}</span>
						<el-icon class="delete-icon" @click.stop="handleDelete(item.playlistId, 'collected')">
							<Close />
						</el-icon>
					</div>
				</div>
			</div>
		</div>

		<!-- 右侧内容 -->
		<div class="right-side" v-if="isDataLoaded">
			<router-view />
		</div>

		<!-- 新建歌单对话框 -->
		<el-dialog v-model="showCreateDialog" title="新建歌单" width="30%">
			<el-form>
				<el-form-item label="歌单名称">
					<el-input v-model="newPlaylistName" placeholder="请输入歌单名称" />
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="showCreateDialog = false">取消</el-button>
				<el-button type="primary" @click="handleCreate">创建</el-button>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import { ref, reactive, onMounted, provide } from 'vue';
	import {
		apiCreatePlaylist,
		apiGetCreatePlayListsByUserId,
		apiGetCollectPlayListsByUserId,
		apiDeletePlaylist,
		apiCancelCollectPlaylist,
	} from '@/api/frontend/playlist';
	import {
		ElDialog,
		ElButton,
		ElInput,
		ElForm,
		ElFormItem,
		ElMessageBox,
		ElMessage,
	} from 'element-plus';
	import { useRouter, useRoute } from 'vue-router';
	import { useAuthStore } from '@/stores/frontend/auth';
	const authStore = useAuthStore();

	const router = useRouter();
	const route = useRoute();
	// 数据状态
	const showCreateDialog = ref(false);
	const newPlaylistName = ref('');
	const currentPlaylist = ref(null);
	// 新增：数据加载状态
	const isDataLoaded = ref(false);
	// 模拟数据
	const createdPlaylists = ref([]);

	const collectedPlaylists = ref([]);

	// 对话框处理
	const handleCreate = async () => {
		if (newPlaylistName.value.trim()) {
			await apiCreatePlaylist(newPlaylistName.value.trim());
			getCreatePlayLists();

			showCreateDialog.value = false;
			newPlaylistName.value = '';
		}
	};
	const getCreatePlayLists = async () => {
		const res = await apiGetCreatePlayListsByUserId(authStore.user.userId);
		createdPlaylists.value = res.data.data;
	};
	const handleDelete = async (playlistId, type) => {
		try {
			await ElMessageBox.confirm('确定要删除该歌单吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning',
			});

			if (type === 'created') {
				await apiDeletePlaylist(playlistId);
				createdPlaylists.value = createdPlaylists.value.filter(
					(item) => item.playlistId !== playlistId
				);
			} else {
				await apiCancelCollectPlaylist(playlistId);
				collectedPlaylists.value = collectedPlaylists.value.filter(
					(item) => item.playlistId !== playlistId
				);
			}

			ElMessage.success('操作成功');
		} catch (error) {
			if (error !== 'cancel') {
				ElMessage.error('操作失败');
			}
		}
	};

	const getCollectedPlaylists = async () => {
		const res = await apiGetCollectPlayListsByUserId(authStore.user.userId);
		collectedPlaylists.value = res.data.data;
	};
	const toPlaylist = (id) => {
		router.push({
			path: '/my/playlist',
			query: { id: id },
		});
	};
	const likelistId = ref(1);
	onMounted(async () => {
		await getCreatePlayLists();
		if (createdPlaylists.value.length > 0) {
			likelistId.value = createdPlaylists.value[0].playlistId; // 先赋值
		}
		await getCollectedPlaylists();
		isDataLoaded.value = true; // 数据加载完成
	});
	provide('likelistId', likelistId);
</script>

<style lang="scss" scoped>
	.main-container {
		max-width: 1000px;
		margin: 0 auto;
		display: flex;
		min-height: 100vh;
	}

	.left-side {
		width: 25%;
		padding: 20px;
		background: #f8f9fa;
		border-right: 1px solid #e0e0e0;
	}

	.right-side {
		flex: 1;
		padding: 20px;
		background: white;
	}

	.menu-group {
		margin-top: 16px;

		.menu-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 8px 12px;
			cursor: pointer;

			&:hover {
				background: #f1f3f5;
			}
		}
	}

	.sub-list {
		margin-top: 8px;
	}

	.playlist-item {
		display: flex;
		align-items: center;
		padding: 8px;
		cursor: pointer;
		border-radius: 4px;

		&:hover {
			background: #f1f3f5;
		}

		.cover {
			width: 40px;
			height: 40px;
			border-radius: 4px;
			margin-right: 12px;
		}

		.name {
			font-size: 14px;
			flex: 1;
			@apply truncate;
		}
	}
</style>
