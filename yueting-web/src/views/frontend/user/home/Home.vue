<template>
	<div class="user-home-container">
		<!-- 基本信息区域 -->
		<UserProfileSection
			:user-info="userInfo"
			@go-to-notes="goToMyNotes"
			@go-to-following="goToMyFollowing"
			@go-to-followers="goToMyFollowers"
		>
			<template #actions>
				<button v-if="authStore.user.userId === route.query.id" class="edit-button" @click="navigate('/user/basic')">编辑个人资料</button>
				<button v-if="artistInfo" class="artist-button" @click="navigateToArtist">歌手页</button>
				<button v-if="authStore.user.userId !== route.query.id && authStore.user.userId" class="follow-button" :class="{ 'following': isFollowing }" @click="handleFollow">
					{{ isFollowing ? '已关注' : '关注' }}
				</button>
				<button v-if="authStore.user.userId !== route.query.id && authStore.user.userId" class="message-button" @click="goToMessage">
					发私信
				</button>
			</template>
		</UserProfileSection>

		<!-- 听歌排行区域 -->
		<div class="play-ranking-section" v-if="showPlayRanking">
			<div class="ranking-header">
				<h2 class="ranking-title">听歌排行</h2>
				<div class="header-right">
					<div class="time-filter">
						<span
							class="time-option"
							:class="{ active: timeRange === true }"
							@click="handleTimeRangeChange(true)"
						>最近一周</span>
						<span class="time-separator">|</span>
						<span
							class="time-option"
							:class="{ active: timeRange === false }"
							@click="handleTimeRangeChange(false)"
						>所有时间</span>
					</div>
					<router-link to="/my/play-history" class="view-more-btn">查看更多</router-link>
				</div>
			</div>
			<div class="ranking-table" v-if="playRanking.length > 0">
				<el-table :data="playRanking" stripe style="width: 100%">
					<el-table-column label="序号" width="80">
						<template #default="scope">
							{{ scope.$index + 1 }}
						</template>
					</el-table-column>
					<el-table-column label="" width="60">
						<template #default="scope">
							<el-icon size="20px" @click="handlePlaySong(scope.row)"><videoPlay /></el-icon>
						</template>
					</el-table-column>
					<el-table-column label="歌曲">
						<template #default="{ row }">
							<router-link v-if="row.songId" :to="`/song?id=${row.songId}`" class="song-link">
								{{ row.songName }}
							</router-link>
							<span v-else>{{ row.songName || '未知歌曲' }}</span>
							<span class="artist-separator" v-if="row.artistNames"> - </span>
							<template v-if="row.artistIds">
								<router-link
									v-for="(artistName, idx) in (row.artistNames || '').split('/')"
									:key="idx"
									:to="`/artist?id=${(row.artistIds || '').split(',')[idx]}`"
									class="artist-link"
								>
									{{ artistName || '未知歌手' }}
								</router-link>
							</template>
							<span v-else-if="row.artistNames" class="artist-names">{{ row.artistNames }}</span>
						</template>
					</el-table-column>
					<el-table-column label="播放次数" width="120">
						<template #default="scope">
							{{ scope.row.playCount }}
						</template>
					</el-table-column>
				</el-table>
			</div>
			<div class="empty-ranking" v-else>
				<span>暂无播放记录</span>
			</div>
		</div>

		<div class="playlist-container">
			<!-- 创建的歌单 -->
			<div class="section" v-if="createdPlaylists.length">
				<h2 class="section-title">我的歌单（{{ createdPlaylists.length }}）</h2>
				<div class="playlist-grid">
					<div
						v-for="playlist in createdPlaylists"
						:key="playlist.playlistId"
						class="playlist-item"
					>
						<router-link :to="`/playlist?id=${playlist.playlistId}`">
							<div class="cover-wrapper">
								<img :src="playlist.coverUrl" class="cover" alt="歌单封面" />
							</div>
							<div class="name">{{ playlist.playlistName }}</div>
						</router-link>
					</div>
				</div>
			</div>

			<!-- 收藏的歌单 -->
			<div class="section" v-if="collectedPlaylists.length">
				<h2 class="section-title">收藏歌单（{{ collectedPlaylists.length }}）</h2>
				<div class="playlist-grid">
					<div
						v-for="playlist in collectedPlaylists"
						:key="playlist.playlistId"
						class="playlist-item"
					>
						<router-link :to="`/playlist?id=${playlist.playlistId}`">
							<div class="cover-wrapper">
								<img :src="playlist.coverUrl" class="cover" alt="歌单封面" />
							</div>
							<div class="name">{{ playlist.playlistName }}</div>
						</router-link>
					</div>
				</div>
			</div>
		</div>

		<!-- 关注列表弹框 -->
		<el-dialog v-model="followingDialogVisible" title="关注" width="500px">
			<div class="user-list" v-if="followingList.length">
				<div v-for="user in followingList" :key="user.userId" class="user-item">
					<img :src="user.avatarUrl || defaultAvatar" class="user-avatar" @click="goToUserHome(user.userId)" />
					<div class="user-info" @click="goToUserHome(user.userId)">
						<div class="user-name">{{ user.nickname }}</div>
						<div class="user-signature">{{ user.signature || '这个人很懒，什么都没有留下~' }}</div>
					</div>
				</div>
			</div>
			<div v-else class="empty-list">暂无关注</div>
		</el-dialog>

		<!-- 粉丝列表弹框 -->
		<el-dialog v-model="followersDialogVisible" title="粉丝" width="500px">
			<div class="user-list" v-if="followersList.length">
				<div v-for="user in followersList" :key="user.userId" class="user-item">
					<img :src="user.avatarUrl || defaultAvatar" class="user-avatar" @click="goToUserHome(user.userId)" />
					<div class="user-info" @click="goToUserHome(user.userId)">
						<div class="user-name">{{ user.nickname }}</div>
						<div class="user-signature">{{ user.signature || '这个人很懒，什么都没有留下~' }}</div>
					</div>
				</div>
			</div>
			<div v-else class="empty-list">暂无粉丝</div>
		</el-dialog>
	</div>
</template>

<script setup>
	import { onMounted, ref, inject, watch } from 'vue';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { useRouter, useRoute } from 'vue-router';
	import { apiGetUserBasicByUserId, apiGetUserPlayRanking, apiFollowUser, apiUnfollowUser, apiGetFollowingList, apiGetFollowerList, apiIsFollowing } from '@/api/frontend/user';
	import {
		apiGetCreatePlayListsByUserId,
		apiGetCollectPlayListsByUserId,
	} from '@/api/frontend/playlist';
	import { apiGetArtistBasicByUserId } from '@/api/frontend/artist';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { VideoPlay, VideoPause } from '@element-plus/icons-vue';
	import UserProfileSection from '@/components/frontend/user/UserProfileSection.vue';

	const router = useRouter();
	const route = useRoute();
	const authStore = useAuthStore();
	const playerStore = usePlayerStore();
	const playerBarRef = inject('playerBar');

	const defaultAvatar = 'https://jc-yueting.oss-cn-beijing.aliyuncs.com/frontend/defaultUserImage.png';

	const userInfo = ref({})
	const createdPlaylists = ref([]);
	const collectedPlaylists = ref([]);
	const playRanking = ref([]);
	const timeRange = ref(true);
	const showPlayRanking = ref(false);
	const artistInfo = ref(null);
	const isFollowing = ref(false);
	const followingDialogVisible = ref(false);
	const followersDialogVisible = ref(false);
	const followingList = ref([]);
	const followersList = ref([]);

	const navigate = (path) => {
		router.push(path);
	};

	const navigateToArtist = () => {
		if (artistInfo.value) {
			router.push(`/artist?id=${artistInfo.value.artistId}`);
		}
	};

	const goToUserHome = (userId) => {
		router.push(`/user/home?id=${userId}`);
		followingDialogVisible.value = false;
		followersDialogVisible.value = false;
	};

	const handleFollow = async () => {
		if (!authStore.user.userId) {
			router.push('/login');
			return;
		}
		try {
			if (isFollowing.value) {
				await apiUnfollowUser(route.query.id);
				isFollowing.value = false;
				userInfo.value.followerCount--;
			} else {
				await apiFollowUser(route.query.id);
				isFollowing.value = true;
				userInfo.value.followerCount++;
			}
		} catch (error) {
			console.error('操作失败:', error);
		}
	};

	const showFollowingDialog = async () => {
		followingDialogVisible.value = true;
		try {
			const res = await apiGetFollowingList(route.query.id);
			followingList.value = res.data.data || [];
		} catch (error) {
			console.error('获取关注列表失败:', error);
			followingList.value = [];
		}
	};

	const showFollowersDialog = async () => {
		followersDialogVisible.value = true;
		try {
			const res = await apiGetFollowerList(route.query.id);
			followersList.value = res.data.data || [];
		} catch (error) {
			console.error('获取粉丝列表失败:', error);
			followersList.value = [];
		}
	};

	// 跳转我的笔记页面
	const goToMyNotes = () => {
		router.push('/my-notes');
	};

	// 跳转我的关注页面
	const goToMyFollowing = () => {
		router.push('/my-following');
	};

	// 跳转我的粉丝页面
	const goToMyFollowers = () => {
		router.push('/my-followers');
	};

	// 跳转私信页面
	const goToMessage = () => {
		router.push(`/user/message?userId=${route.query.id}&nickname=${encodeURIComponent(userInfo.value.nickname)}`);
	};

	const getCreatePlayLists = async () => {
		const res = await apiGetCreatePlayListsByUserId(route.query.id);
		createdPlaylists.value = res.data.data;
	};

	const getCollectedPlaylists = async () => {
		const res = await apiGetCollectPlayListsByUserId(route.query.id);
		collectedPlaylists.value = res.data.data;
	};

	const getPlayRanking = async () => {
		try {
			// 先查询最近一周的数据
			let res = await apiGetUserPlayRanking(true);
			let weekData = res.data.data || [];

			if (weekData.length === 0) {
				// 一周内没有数据，查询所有时间
				res = await apiGetUserPlayRanking(false);
				const allTimeData = res.data.data || [];

				if (allTimeData.length === 0) {
					// 所有时间都没有数据，不展示听歌排行
					showPlayRanking.value = false;
					playRanking.value = [];
					return;
				} else {
					// 所有时间有数据，切换到"所有时间"标签
					timeRange.value = false;
					playRanking.value = allTimeData;
				}
			} else {
				// 一周内有数据，保持"最近一周"标签
				playRanking.value = weekData;
			}
			showPlayRanking.value = true;
		} catch (error) {
			console.error('获取播放排行失败:', error);
			playRanking.value = [];
			showPlayRanking.value = false;
		}
	};

	// 根据当前 timeRange 直接查询数据（用于用户手动切换标签）
	const fetchPlayRankingByTimeRange = async () => {
		try {
			const res = await apiGetUserPlayRanking(timeRange.value);
			playRanking.value = res.data.data || [];
		} catch (error) {
			console.error('获取播放排行失败:', error);
			playRanking.value = [];
		}
	};

	const handleTimeChange = () => {
		// 用户手动切换标签时，直接根据当前 timeRange 查询
		fetchPlayRankingByTimeRange();
	};

	// 处理纯文字形式的标签切换
	const handleTimeRangeChange = (value) => {
		if (timeRange.value !== value) {
			timeRange.value = value;
			fetchPlayRankingByTimeRange();
		}
	};

	const getPlayIcon = (songId) => {
		return playerStore.currentSong?.songId === songId && playerStore.isPlaying ? VideoPause : VideoPlay;
	};

	const handlePlaySong = (song) => {
		if (!playerBarRef.value) return;

		if (playerStore.currentSong?.songId === song.songId) {
			playerStore.togglePlay();
		} else {
			const index = playerStore.playQueue.findIndex((item) => item.songId === song.songId);
			if (index > -1) {
				if (index < playerStore.currentIndex) {
					for (let i = index + 1; i <= playerStore.currentIndex; i++) {
						playerStore.playQueue[i - 1] = playerStore.playQueue[i];
					}
					playerStore.playQueue[playerStore.currentIndex] = song;
					playerStore.playAtIndex(playerStore.currentIndex);
				} else {
					for (let i = index - 1; i >= playerStore.currentIndex + 1; i--) {
						playerStore.playQueue[i + 1] = playerStore.playQueue[i];
					}
					playerStore.currentIndex++;
					playerStore.playQueue[playerStore.currentIndex] = song;
					playerStore.playAtIndex(playerStore.currentIndex);
				}
			} else {
				playerStore.addNext(song);
				playerStore.playAtIndex(playerStore.currentIndex + 1);
			}
		}
		playerBarRef.value.showBar();
		setTimeout(() => {
			playerBarRef.value?.scheduleHide();
		}, 1000);
	};

	onMounted(async () => {
		await getCreatePlayLists();
		await getCollectedPlaylists();
		await getPlayRanking();
	});
	onMounted(async () => {
		const res = await apiGetUserBasicByUserId(route.query.id);
		userInfo.value = res.data.data;
		if (route.query.id === authStore.user.userId) authStore.updateUser(res.data.data);
		// 获取艺人信息（歌手在查看自己主页时也需要显示"个人主页"按钮）
		fetchArtistInfo();
		// 如果查看的是其他用户页面，检查是否已关注
		if (route.query.id && route.query.id !== authStore.user.userId) {
			checkFollowingStatus();
		}
	});

	const checkFollowingStatus = async () => {
		try {
			const res = await apiIsFollowing(route.query.id);
			isFollowing.value = res.data.data;
		} catch (error) {
			console.error('检查关注状态失败:', error);
		}
	};

	const fetchArtistInfo = async () => {
		try {
			const res = await apiGetArtistBasicByUserId(route.query.id);
			artistInfo.value = res.data.data;
		} catch (error) {
			console.error('获取艺人信息失败:', error);
		}
	};

	// 监听路由变化，重新加载数据
	watch(
		() => route.query.id,
		async (newId, oldId) => {
			if (newId && newId !== oldId) {
				// 重置状态
				userInfo.value = {};
				playRanking.value = [];
				showPlayRanking.value = false;
				timeRange.value = true;
				artistInfo.value = null;
				isFollowing.value = false;
				followingList.value = [];
				followersList.value = [];
				// 重新获取用户信息
				const userRes = await apiGetUserBasicByUserId(newId);
				userInfo.value = userRes.data.data;
				// 获取歌单
				await Promise.all([getCreatePlayLists(), getCollectedPlaylists()]);
				// 如果是当前用户，获取播放排行
				if (newId === authStore.user.userId) {
					await getPlayRanking();
				}
				// 如果查看的是其他用户，检查是否已关注，获取艺人信息
				if (newId && newId !== authStore.user.userId) {
					checkFollowingStatus();
					fetchArtistInfo();
				}
			}
		}
	);
</script>

<style lang="scss" scoped>
	.user-home-container {
		width: 950px;
		margin: 0 auto;
		background: white;

		// 按钮样式（通过 slot 传递）
		:deep(.button-group) {
			display: flex;
			gap: 10px;
		}

		:deep(.edit-button) {
			background: #ec4141;
			color: white;
			border: none;
			padding: 8px 20px;
			border-radius: 20px;
			cursor: pointer;
			transition: background 0.3s;

			&:hover {
				background: #d43838;
			}
		}

		:deep(.artist-button) {
			background: #fff;
			color: #333;
			border: 1px solid #ddd;
			padding: 8px 20px;
			border-radius: 20px;
			cursor: pointer;
			transition: all 0.3s;

			&:hover {
				border-color: #ec4141;
				color: #ec4141;
			}
		}

		:deep(.follow-button) {
			background: #ec4141;
			color: white;
			border: none;
			padding: 8px 20px;
			border-radius: 20px;
			cursor: pointer;
			transition: background 0.3s;

			&:hover {
				background: #d43838;
			}

			&.following {
				background: #fff;
				color: #666;
				border: 1px solid #ddd;

				&:hover {
					border-color: #ec4141;
					color: #ec4141;
				}
			}
		}

		:deep(.message-button) {
			background: #fff;
			color: #333;
			border: 1px solid #ddd;
			padding: 8px 20px;
			border-radius: 20px;
			cursor: pointer;
			transition: all 0.3s;

			&:hover {
				border-color: #ec4141;
				color: #ec4141;
			}
		}

		// 听歌排行区域样式
		.play-ranking-section {
			background: white;
			padding: 20px 30px;
			margin-top: 20px;
			box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

			.ranking-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 20px;

				.ranking-title {
					font-size: 18px;
					color: #333;
					margin: 0;
				}

				.header-right {
					display: flex;
					align-items: center;
					gap: 15px;
				}

				.time-filter {
					display: flex;
					align-items: center;
					gap: 12px;
					font-size: 14px;

					.time-option {
						color: #666;
						cursor: pointer;
						transition: color 0.3s;
						user-select: none;

						&:hover {
							color: #333;
						}

						&.active {
							color: #333;
							font-weight: 500;
						}
					}

					.time-separator {
						color: #ddd;
						user-select: none;
					}
				}

				.view-more-btn {
					color: #333;
					font-size: 12px;
					text-decoration: none;

					&:hover {
						text-decoration: underline;
					}
				}
			}

			.ranking-table {
				:deep(.el-table) {
					font-size: 14px;

					th {
						background: #f5f7fa;
						color: #606266;
					}

					.cell {
						padding-left: 15px;
						padding-right: 15px;
					}
				}

				.song-link {
					color: #606266;
					text-decoration: none;
					transition: color 0.3s;

					&:hover {
						color: red;
						text-decoration: underline;
					}
				}

				.artist-link {
					color: #606266;
					text-decoration: none;
					transition: color 0.3s;

					&:hover {
						color: red;
						text-decoration: underline;
					}
				}

				:deep(.el-table__body tr td) {
					padding: 8px 0;
				}
			}

			.empty-ranking {
				text-align: center;
				padding: 40px 0;
				color: #999;
				font-size: 14px;
			}
		}

		.placeholder {
			padding: 40px;
			background: white;
			min-height: 200px;
			text-align: center;
			color: #666;

			h2 {
				color: #999;
				margin: 0;
			}
		}
	}
	.playlist-container {
		padding: 20px;
		max-width: 1200px;
		margin: 0 auto;

		.section {
			margin-bottom: 40px;

			.section-title {
				font-size: 18px;
				color: #333;
				margin: 0 0 20px 10px;
			}
		}

		.playlist-grid {
			display: grid;
			grid-template-columns: repeat(5, 1fr);
			gap: 30px 50px;

			.playlist-item {
				position: relative;
				border-radius: 8px;
				overflow: hidden;
				transition: transform 0.3s ease;

				&:hover {
					transform: translateY(-5px);
					.cover {
						box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
					}
				}

				a {
					text-decoration: none;
					color: inherit;
				}

				.cover-wrapper {
					position: relative;
					padding-top: 100%;
					background: #f5f5f5;
					border-radius: 8px;
					overflow: hidden;

					.cover {
						position: absolute;
						top: 0;
						left: 0;
						width: 100%;
						height: 100%;
						object-fit: cover;
						border-radius: 6px;
						transition: all 0.3s;
					}
				}

				.name {
					margin-top: 10px;
					font-size: 14px;
					color: #333;
					line-height: 1.4;
					height: 40px;
					overflow: hidden;
					display: -webkit-box;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
					padding: 0 5px;
				}
			}
		}

		@media (max-width: 1200px) {
			.playlist-grid {
				grid-template-columns: repeat(4, 1fr);
			}
		}

		@media (max-width: 768px) {
			.playlist-grid {
				grid-template-columns: repeat(3, 1fr);
				gap: 20px 15px;
			}
		}

		@media (max-width: 480px) {
			.playlist-grid {
				grid-template-columns: repeat(2, 1fr);
			}
		}
	}

	// 弹框样式
	.user-list {
		max-height: 400px;
		overflow-y: auto;

		.user-item {
			display: flex;
			align-items: center;
			padding: 10px;
			border-bottom: 1px solid #f0f0f0;
			cursor: pointer;
			transition: background 0.2s;

			&:hover {
				background: #fafafa;
			}

			&:last-child {
				border-bottom: none;
			}

			.user-avatar {
				width: 50px;
				height: 50px;
				border-radius: 50%;
				object-fit: cover;
				margin-right: 12px;
			}

			.user-info {
				flex: 1;
				overflow: hidden;

				.user-name {
					font-size: 14px;
					color: #333;
					font-weight: 500;
					margin-bottom: 4px;
				}

				.user-signature {
					font-size: 12px;
					color: #999;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}
			}
		}
	}

	.empty-list {
		text-align: center;
		padding: 40px 0;
		color: #999;
		font-size: 14px;
	}
</style>
