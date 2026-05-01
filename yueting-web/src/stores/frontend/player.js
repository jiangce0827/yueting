import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';
import { debounce } from 'lodash';
import { ElMessage } from 'element-plus';

import { apiGetSongDetail, apiIncrementPlay } from '@/api/frontend/song';

export const usePlayerStore = defineStore('player', () => {
	const savedState = JSON.parse(localStorage.getItem('playerState') || '{}');

	const audioElement = ref(null);
	const isPlaying = ref(false);
	const currentTime = ref(0);
	const duration = ref(0);
	const playQueue = ref(savedState.playQueue || []);
	const currentIndex = ref(savedState.currentIndex ?? -1);
	const currentSong = computed(() => playQueue.value[currentIndex.value] || null);
	const upcomingQueue = computed(() => playQueue.value.slice(currentIndex.value + 1));
	const countedSongs = ref(new Set());

	let currentSongDuration = 0;
	let hasCounted = false;
	let metadataResolver = null;
	let metadataRejector = null;
	let committedQueue = Array.isArray(savedState.playQueue) ? [...savedState.playQueue] : [];
	let committedIndex = typeof savedState.currentIndex === 'number' ? savedState.currentIndex : -1;

	watch(
		[playQueue, currentIndex],
		() => {
			localStorage.setItem(
				'playerState',
				JSON.stringify({
					playQueue: playQueue.value,
					currentIndex: currentIndex.value,
				})
			);
		},
		{ deep: true }
	);

	const initAudio = () => {
		if (!audioElement.value) {
			audioElement.value = new Audio();
			setupAudioListeners();
		}
	};

	const setupAudioListeners = () => {
		audioElement.value.addEventListener('timeupdate', () => {
			currentTime.value = audioElement.value.currentTime;
		});

		audioElement.value.addEventListener('loadedmetadata', () => {
			duration.value = audioElement.value.duration || 0;
			if (metadataResolver) {
				metadataResolver();
				metadataResolver = null;
				metadataRejector = null;
			}
		});

		audioElement.value.addEventListener('error', () => {
			if (metadataRejector) {
				metadataRejector(new Error('audio-error'));
				metadataResolver = null;
				metadataRejector = null;
			}
		});

		audioElement.value.addEventListener('ended', () => {
			isPlaying.value = false;
			playNext();
		});
	};

	const cloneQueue = (queue) => queue.map((item) => ({ ...item }));

	const commitPlaybackState = () => {
		committedQueue = cloneQueue(playQueue.value);
		committedIndex = currentIndex.value;
	};

	const restoreCommittedState = () => {
		playQueue.value = cloneQueue(committedQueue);
		currentIndex.value = committedIndex;
		if (committedIndex === -1) {
			isPlaying.value = false;
			currentTime.value = 0;
			duration.value = 0;
			if (audioElement.value) {
				audioElement.value.pause();
				audioElement.value.src = '';
			}
		}
	};

	const ensureSongPlayable = async (song) => {
		try {
			const res = await apiGetSongDetail(song.songId);
			if (res.data.code !== 1) {
				throw new Error(res.data.msg || '当前音乐暂时无法播放');
			}
			if (Number(res.data.data?.status) === 1) {
				throw new Error('音乐已下架');
			}
			return true;
		} catch (error) {
			const message =
				error?.response?.data?.msg ||
				error?.message ||
				'当前音乐暂时无法播放';
			ElMessage.error(message);
			return false;
		}
	};

	const waitForMetadata = () =>
		new Promise((resolve, reject) => {
			metadataResolver = resolve;
			metadataRejector = reject;
		});

	const playSong = async (song) => {
		initAudio();

		const playable = await ensureSongPlayable(song);
		if (!playable) {
			restoreCommittedState();
			return false;
		}

		const existIndex = playQueue.value.findIndex((item) => item.songId === song.songId);
		if (existIndex === -1) {
			playQueue.value = [song];
			currentIndex.value = 0;
		} else {
			currentIndex.value = existIndex;
		}

		hasCounted = false;
		currentSongDuration = 0;
		audioElement.value.removeEventListener('timeupdate', handlePlayProgress);

		try {
			const metadataReady = waitForMetadata();
			audioElement.value.src = `/api/web/song/play/${song.songId}`;
			await metadataReady;
			currentSongDuration = audioElement.value.duration || 0;
			audioElement.value.addEventListener('timeupdate', handlePlayProgress);
			await audioElement.value.play();
			isPlaying.value = true;
			commitPlaybackState();
			return true;
		} catch (error) {
			console.error('播放失败:', error);
			restoreCommittedState();
			ElMessage.error('当前音乐暂时无法播放');
			isPlaying.value = false;
			return false;
		}
	};

	const playAtIndex = async (index) => {
		if (index < 0 || index >= playQueue.value.length) {
			return false;
		}
		currentIndex.value = index;
		return playSong(playQueue.value[index]);
	};

	const handlePlayProgress = () => {
		if (!currentSongDuration) return;
		if (!hasCounted && audioElement.value.currentTime / currentSongDuration >= 0.5) {
			triggerPlayCount();
			hasCounted = true;
		}
	};

	const triggerPlayCount = debounce(async () => {
		try {
			const songId = currentSong.value?.songId;
			if (!songId || countedSongs.value.has(songId)) return;
			await apiIncrementPlay(songId);
			countedSongs.value.add(songId);
			setTimeout(() => countedSongs.value.delete(songId), 24 * 3600 * 1000);
		} catch (error) {
			console.error('播放量统计失败:', error);
		}
	}, 3000);

	const addNext = (song) => {
		playQueue.value.splice(currentIndex.value + 1, 0, song);
	};

	const nextTrack = () => {
		if (currentIndex.value < playQueue.value.length - 1) {
			playAtIndex(currentIndex.value + 1);
		} else {
			playAtIndex(0);
		}
	};

	const previousTrack = () => {
		if (currentIndex.value > 0) {
			playAtIndex(currentIndex.value - 1);
		} else {
			playAtIndex(playQueue.value.length - 1);
		}
	};

	const playNext = () => {
		if (currentIndex.value < playQueue.value.length - 1) {
			currentIndex.value += 1;
			playSong(playQueue.value[currentIndex.value]);
		} else {
			isPlaying.value = false;
		}
	};

	const togglePlay = () => {
		initAudio();
		if (!audioElement.value || !currentSong.value) return;

		if (isPlaying.value) {
			audioElement.value.pause();
			isPlaying.value = false;
			return;
		}

		audioElement.value
			.play()
			.then(() => {
				isPlaying.value = true;
			})
			.catch(() => {
				ElMessage.error('当前音乐暂时无法播放');
				isPlaying.value = false;
			});
	};

	const seek = (time) => {
		if (audioElement.value) {
			audioElement.value.currentTime = time;
		}
	};

	const playAll = async (songs) => {
		if (!songs?.length) return false;
		playQueue.value = [...songs];
		currentIndex.value = 0;
		const success = await playSong(songs[0]);
		if (!success) {
			restoreCommittedState();
		}
		return success;
	};

	const clearQueue = () => {
		playQueue.value = [];
		currentIndex.value = -1;
		isPlaying.value = false;
		currentTime.value = 0;
		duration.value = 0;
		commitPlaybackState();
		if (audioElement.value) {
			audioElement.value.pause();
			audioElement.value.src = '';
		}
	};

	const removeFromQueue = (index) => {
		if (index < 0 || index >= playQueue.value.length) return;

		const wasPlaying = isPlaying.value;
		const wasCurrent = index === currentIndex.value;
		playQueue.value.splice(index, 1);

		if (index < currentIndex.value) {
			currentIndex.value -= 1;
			commitPlaybackState();
			return;
		}

		if (!wasCurrent) {
			commitPlaybackState();
			return;
		}

		if (!playQueue.value.length) {
			clearQueue();
			return;
		}

		if (currentIndex.value >= playQueue.value.length) {
			currentIndex.value = playQueue.value.length - 1;
		}
		playSong(playQueue.value[currentIndex.value]).then((success) => {
			if (!success && !wasPlaying) {
				isPlaying.value = false;
			}
		});
	};

	return {
		currentSong,
		isPlaying,
		currentTime,
		duration,
		playQueue,
		upcomingQueue,
		playSong,
		togglePlay,
		seek,
		addNext,
		playNext,
		nextTrack,
		previousTrack,
		currentIndex,
		playAtIndex,
		playAll,
		clearQueue,
		removeFromQueue,
	};
});
