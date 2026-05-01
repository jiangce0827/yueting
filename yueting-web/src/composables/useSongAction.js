/**
 * 歌曲操作相关 composable
 * 抽取出重复的歌单选择和添加到播放队列逻辑
 */
import { ref } from 'vue';
import { apiGetCreatePlayListsByUserId, apiAddSongToPlaylist } from '@/api/frontend/playlist';
import { ElMessage } from 'element-plus';

export function useSongAction(playerStore, playerBarRef, authStore) {
	const dialogVisible = ref(false);
	const createdPlaylists = ref([]);
	const playlistData = ref({});

	/**
	 * 打开发添加到歌单弹窗
	 */
	const openAddToPlaylistDialog = async (song) => {
		dialogVisible.value = true;
		playlistData.value = song;
		try {
			const res = await apiGetCreatePlayListsByUserId(authStore?.user?.userId);
			if (res.data.code === 1) {
				createdPlaylists.value = res.data.data || [];
			}
		} catch (error) {
			console.error('获取歌单列表失败:', error);
		}
	};

	/**
	 * 选择要添加到的歌单
	 */
	const handleSelectPlaylist = async (playlistId) => {
		try {
			const res = await apiAddSongToPlaylist(playlistId, playlistData.value.songId);
			if (res.data.code === 1) {
				ElMessage.success('已添加到歌单');
				dialogVisible.value = false;
			} else {
				ElMessage.error(res.data.msg || '添加失败');
			}
		} catch (error) {
			ElMessage.error('添加失败');
		}
	};

	/**
	 * 添加到下一首播放
	 * @param {Object} song - 歌曲对象
	 * @param {boolean} complex - 是否使用复杂模式（去重+插入到当前播放位置后）
	 */
	const handleAddToNext = (song, complex = false) => {
		if (!complex) {
			playerStore.addNext(song);
			if (playerBarRef?.value) {
				playerBarRef.value.showBar();
				setTimeout(() => {
					playerBarRef.value?.scheduleHide();
				}, 1000);
			}
			return;
		}
		// 复杂模式：去重 + 插入到当前播放位置后
		if (!playerStore.currentSong) {
			playerStore.addNext(song);
			playerStore.playAtIndex(playerStore.currentIndex + 1);
			return;
		}
		if (song.songId == playerStore.currentSong.songId) {
			return;
		}
		const currentSongId = playerStore.currentSong.songId;
		// 从播放列表中移除该歌曲（避免重复）
		for (let i = 0; i < playerStore.playQueue.length; i++) {
			if (playerStore.playQueue[i].songId == song.songId) {
				playerStore.playQueue.splice(i, 1);
			}
		}
		// 获取当前播放歌曲的索引
		let index = -1;
		for (let i = 0; i < playerStore.playQueue.length; i++) {
			if (playerStore.playQueue[i].songId == currentSongId)
			{
				index = i;
				break;
			}
		}
		playerStore.currentIndex = index;
		playerStore.addNext(song);
		if (playerStore.currentIndex == -1) {
			playerStore.playAtIndex(0);
		}
	};

	return {
		dialogVisible,
		createdPlaylists,
		playlistData,
		openAddToPlaylistDialog,
		handleSelectPlaylist,
		handleAddToNext,
	};
}
