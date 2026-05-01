<template>
	<div class="artist-container">
		<div class="main-content">
			<div class="left-content">
				<div class="left-main">
					<div class="song-detail">
						<div class="cover-wrapper">
							<img :src="songInfo.coverUrl" class="cover" />
						</div>

						<div class="info-wrapper">
							<h1 class="song-title">{{ songInfo.songName }}</h1>

							<el-alert
								v-if="isSongOffline"
								title="该歌曲已下架，当前仍可查看详情，但暂时无法播放。"
								type="warning"
								:closable="false"
								class="offline-alert"
							/>

							<div class="artist-info">
								<span>歌手：</span>
								<template v-for="(name, index) in songInfo.artistNames?.split(',')" :key="index">
									<router-link
										:to="`/artist?id=${songInfo.artistIds?.split(',')[index]}`"
										class="link"
									>
										{{ name }}
									</router-link>
									<span v-if="index < songInfo.artistNames.split(',').length - 1"> / </span>
								</template>
							</div>

							<div class="album-info">
								<span>所属专辑：</span>
								<router-link :to="`/album?id=${songInfo.albumId}`" class="link">
									《{{ songInfo.albumName }}》
								</router-link>
							</div>

							<div class="actions">
								<el-button type="primary" @click="handlePlay">播放</el-button>
								<el-button @click="handleFavorite">收藏</el-button>
							</div>

							<div class="lyrics-box">
								<div v-if="songInfo.lyricistNames" class="lyrics-meta">
									作词：{{ songInfo.lyricistNames.split(',').join(' / ') }}
								</div>
								<div v-if="songInfo.composerNames" class="lyrics-meta">
									作曲：{{ songInfo.composerNames.split(',').join(' / ') }}
								</div>
								<pre class="lyrics-text">{{ songInfo.lyrics }}</pre>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="right-content">
				<HotArtist />
			</div>
		</div>

		<div class="comment-area-wrapper">
			<CommentSection :target-type="1" :target-id="songInfo.songId" />
		</div>
	</div>

	<el-dialog v-model="dialogVisible" title="选择收藏歌单" width="400px">
		<div class="playlist-dialog">
			<div
				v-for="playlist in createdPlaylists"
				:key="playlist.playlistId"
				class="playlist-item"
				@click="handleSelectPlaylist(playlist.playlistId)"
			>
				<img :src="playlist.coverUrl" class="cover" />
				<div class="info">
					<div class="name">{{ playlist.playlistName }}</div>
					<div class="count">{{ playlist.songCount }}首</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup>
import { computed, inject, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

import { apiGetSongDetail } from '@/api/frontend/song';
import { useAuthStore } from '@/stores/frontend/auth';
import { usePlayerStore } from '@/stores/frontend/player';
import { useSongAction } from '@/composables/useSongAction';
import HotArtist from '/src/components/frontend/HotArtist.vue';
import CommentSection from '/src/components/frontend/CommentSection.vue';

const route = useRoute();
const playerBarRef = inject('playerBar');
const playerStore = usePlayerStore();
const authStore = useAuthStore();

const songInfo = ref({});
const isSongOffline = computed(() => Number(songInfo.value?.status) === 1);

const { dialogVisible, createdPlaylists, openAddToPlaylistDialog, handleSelectPlaylist } =
	useSongAction(playerStore, playerBarRef, authStore);

const loadSongData = async (songId) => {
	if (!songId) return;
	try {
		const res = await apiGetSongDetail(songId);
		if (res.data.code !== 1) {
			ElMessage.error(res.data.msg || '获取歌曲详情失败');
			return;
		}
		songInfo.value = res.data.data || {};
		if (Number(songInfo.value.status) === 1) {
			ElMessage.warning('该歌曲已下架，当前仅支持查看详情');
		}
	} catch (error) {
		ElMessage.error(error?.response?.data?.msg || '获取歌曲详情失败');
	}
};

const handleFavorite = () => {
	openAddToPlaylistDialog(songInfo.value);
};

const handlePlay = async () => {
	if (!playerBarRef.value || !songInfo.value?.songId) return;
	const row = songInfo.value;

	if (playerStore.currentSong?.songId === row.songId) {
		playerStore.togglePlay();
	} else {
		const index = playerStore.playQueue.findIndex((item) => item.songId === row.songId);
		if (index > -1) {
			if (index < playerStore.currentIndex) {
				for (let i = index + 1; i <= playerStore.currentIndex; i++) {
					playerStore.playQueue[i - 1] = playerStore.playQueue[i];
				}
				playerStore.playQueue[playerStore.currentIndex] = row;
				await playerStore.playAtIndex(playerStore.currentIndex);
			} else {
				for (let i = index - 1; i >= playerStore.currentIndex + 1; i--) {
					playerStore.playQueue[i + 1] = playerStore.playQueue[i];
				}
				playerStore.currentIndex++;
				playerStore.playQueue[playerStore.currentIndex] = row;
				await playerStore.playAtIndex(playerStore.currentIndex);
			}
		} else {
			playerStore.addNext(row);
			await playerStore.playAtIndex(playerStore.currentIndex + 1);
		}
	}

	playerBarRef.value.showBar();
	setTimeout(() => {
		playerBarRef.value?.scheduleHide();
	}, 1000);
};

onMounted(() => {
	loadSongData(route.query.id);
});

watch(
	() => route.query.id,
	(newId) => {
		loadSongData(newId);
	}
);
</script>

<style lang="scss" scoped>
$primary-color: #d43c33;
$text-color: #333;
$secondary-text: #666;
$bg-hover: #f5f5f5;
$box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
$border-radius: 8px;

.artist-container {
	width: 100%;
}

.main-content {
	width: 1000px;
	margin: 0 auto;
	display: flex;
	gap: 30px;
}

.left-content {
	display: flex;
	flex: 3;
	padding: 10px 20px;
	flex-direction: column;
	background: white;
}

.song-detail {
	display: flex;
	gap: 40px;
	background: #fff;
	border-radius: 8px;
}

.cover-wrapper {
	flex-shrink: 0;
}

.cover {
	width: 180px;
	height: 180px;
	border-radius: 8px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-wrapper {
	flex: 1;
}

.song-title {
	font-size: 28px;
	margin: 0 0 15px;
	color: #333;
}

.offline-alert {
	margin-bottom: 16px;
}

.artist-info,
.album-info {
	font-size: 14px;
	color: #666;
	margin-bottom: 12px;
}

.link {
	color: #409eff;
	text-decoration: none;
	transition: color 0.3s;

	&:hover {
		color: #d43c33;
		text-decoration: underline;
	}
}

.actions {
	margin: 25px 0;
}

.lyrics-meta {
	font-size: 14px;
	color: #666;
	font-family: 'Microsoft Yahei', sans-serif;
}

.lyrics-text {
	white-space: pre-wrap;
	font-size: 14px;
	line-height: 1.8;
	color: #555;
	margin: 0;
	font-family: 'Microsoft Yahei', sans-serif;
}

.right-content {
	flex: 1;
	padding: 20px;
	background: #fff;
	border-radius: $border-radius;
	box-shadow: $box-shadow;
}

.comment-area-wrapper {
	width: 1000px;
	margin: 20px auto 0;
	background: #fff;
	padding: 20px;
	border-radius: $border-radius;
}

.playlist-dialog {
	max-height: 60vh;
	overflow-y: auto;
}

.playlist-item {
	display: flex;
	align-items: center;
	padding: 12px;
	cursor: pointer;
	transition: background 0.3s;

	&:hover {
		background: #f5f7fa;
	}
}

.playlist-item .cover {
	width: 50px;
	height: 50px;
	border-radius: 4px;
	margin-right: 15px;
}

.playlist-item .name {
	font-size: 14px;
	margin-bottom: 4px;
}

.playlist-item .count {
	font-size: 12px;
	color: #909399;
}
</style>
