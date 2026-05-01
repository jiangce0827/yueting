<!-- @/components/PlayerBar.vue -->
<template>
	<div
		class="player-bar"
		:class="{ active: isBarVisible }"
		@mouseenter="handleBarEnter"
		@mouseleave="handleBarLeave"
	>
		<div class="player-controls">
			<!-- 左侧控制按钮 -->
			<div class="left-controls">
				<el-button circle @click="previousTrack" class="control-btn">
					<el-icon><ArrowLeft /></el-icon>
				</el-button>

				<el-button circle @click="togglePlay" class="play-btn">
					<el-icon :size="24">
						<VideoPlay v-if="!isPlaying" />
						<VideoPause v-else />
					</el-icon>
				</el-button>

				<el-button circle @click="nextTrack" class="control-btn">
					<el-icon><ArrowRight /></el-icon>
				</el-button>
			</div>
			<!-- 歌曲封面 -->
			<div class="album-cover">
				<img :src="currentSong?.coverUrl || errorCoverUrl" alt="album cover" class="cover-image" />
			</div>
			<!-- 右侧信息区域 -->
			<div class="info-container">
				<!-- 新增歌曲信息 -->
				<div class="song-info">
					<div class="title-artist">
						<div class="title">{{ currentSong?.songName || '未知歌曲' }}</div>
						<div class="artist">{{ currentSong?.artistNames || '未知歌手' }}</div>
					</div>
				</div>

				<!-- 修改后的进度条容器 -->
				<div class="progress-wrapper">
					<input
						type="range"
						class="progress-bar"
						:min="0"
						:max="duration"
						:value="currentTime"
						@input="handleSeek"
					/>
					<div class="time-display">
						<span>{{ formatTime(currentTime) }}</span>
						<span>{{ formatTime(duration) }}</span>
					</div>
				</div>
			</div>
			<div class="right-controls">
				<el-tooltip content="播放列表">
					<el-button circle class="playlist-btn" @click="togglePlaylist">
						<el-icon><Menu /></el-icon>
					</el-button>
				</el-tooltip>

				<!-- 播放列表 -->
				<div class="playlist-panel" v-show="showPlaylist">
					<div class="playlist-header">
						<span>播放队列 ({{ playQueue.length }})</span>
						<el-button type="danger" link @click.stop="clearQueue" class="clear-btn">
							<el-icon><Delete /></el-icon>
							<span>清空</span>
						</el-button>
					</div>
					<div class="playlist-items">
						<div
							v-for="(song, index) in playQueue"
							:key="song.songId"
							class="playlist-item"
							:class="{ active: index === currentIndex }"
							@click="playTrack(index)"
						>
							<div class="song-info">
								<div class="title">{{ song.songName }}</div>
								<div class="artist">{{ song.artistNames }}</div>
							</div>
							<div class="duration">{{ formatTime(song.duration) }}</div>
							<el-button
								type="danger"
								text
								circle
								class="delete-btn"
								@click.stop="removeFromQueue(index)"
							>
								<el-icon><Close /></el-icon>
							</el-button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, onUnmounted, computed } from 'vue';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { Close } from '@element-plus/icons-vue'; // 导入关闭图标

	const playerStore = usePlayerStore();
	const showPlaylist = ref(false);

	// 从store获取状态
	const currentTime = computed(() => playerStore.currentTime);
	const duration = computed(() => playerStore.duration);
	const isPlaying = computed(() => playerStore.isPlaying);
	const currentSong = computed(() => playerStore.currentSong);
	const playQueue = computed(() => playerStore.playQueue);
	const currentIndex = computed(() => playerStore.currentIndex);
	const errorCoverUrl =
		'https://p2.music.126.net/H5L_u2Kugx7bO-uma97bDQ==/109951170918412757.jpg?param=50y50&quality=100';

	// 切换播放列表显示
	const togglePlaylist = () => {
		showPlaylist.value = !showPlaylist.value;
	};

	const removeFromQueue = (index) => {
		playerStore.removeFromQueue(index);
	};

	// 播放指定曲目
	const playTrack = (index) => {
		playerStore.playAtIndex(index);
	};
	// 时间格式化
	const formatTime = (seconds) => {
		const mins = Math.floor(seconds / 60)
			.toString()
			.padStart(2, '0');
		const secs = Math.floor(seconds % 60)
			.toString()
			.padStart(2, '0');
		return `${mins}:${secs}`;
	};

	// 进度条拖动处理
	const handleSeek = (e) => {
		playerStore.seek(parseFloat(e.target.value));
	};
	const clearQueue = () => {
		playerStore.clearQueue();
	};
	// 播放控制方法
	const togglePlay = () => playerStore.togglePlay();
	const previousTrack = () => playerStore.previousTrack();
	const nextTrack = () => playerStore.nextTrack();

	// 动态绑定类名。当 isBarVisible 的值为 true 时，添加 active 类；否则移除。active 类用于控制播放器是否显示。
	const isBarVisible = ref(false);
	let hideTimeout = null; //隐藏定时器
	const triggerHeight = 30; // 触发隐藏播放器的垂直距离
	let isManualControl = false; // 新增手动控制标志

	//监听鼠标位置，判断鼠标是否进入触发区域
	const checkMousePosition = (e) => {
		if (isManualControl) return; // 当手动控制时忽略自动检测

		const { clientY } = e;
		const distanceFromBottom = window.innerHeight - clientY;

		if (distanceFromBottom <= triggerHeight) {
			//如果鼠标距离页面底部的距离小于或等于 triggerHeight，则调用 showBar 显示播放器。
			showBar();
		}
	};

	const handleBarEnter = () => {
		// 现在鼠标已经位于播放器区域，调用 showBar 显示播放器，
		// 并且设置isManualControl为true，取消是否进入触发区域的监听。
		isManualControl = true;
		showBar();
	};

	const handleBarLeave = () => {
		// 现在鼠标已经离开播放器区域，调用 scheduleHide 计划关闭播放器，
		// 并且设置isManualControl为false，打开是否进入触发区域的监听。
		isManualControl = false;
		scheduleHide();
	};

	// 显示播放器
	const showBar = () => {
		isBarVisible.value = true;
		clearTimeout(hideTimeout);
	};
	//如果当前不是手动控制状态，则设置一个定时器，在 1 秒后隐藏播放器。
	const scheduleHide = () => {
		if (!isManualControl) {
			hideTimeout = setTimeout(() => {
				isBarVisible.value = false;
				showPlaylist.value = false;
			}, 1000);
		}
	};
	onMounted(() => {
		//为 document 添加 mousemove 事件监听器，实时检查鼠标位
		document.addEventListener('mousemove', checkMousePosition);
	});

	onUnmounted(() => {
		//移除 mousemove 事件监听器，并清除所有未执行的定时器。
		document.removeEventListener('mousemove', checkMousePosition);
		clearTimeout(hideTimeout);
	});
	defineExpose({
		showBar,
		scheduleHide,
	});
</script>

<style lang="scss" scoped>
	.player-bar {
		position: fixed;
		bottom: -60px;
		left: 0;
		right: 0;
		height: 60px;
		background: rgba(30, 30, 30, 0.9);
		transition: bottom 0.3s ease;
		padding: 0 20px;
		z-index: 9000;
		&.active {
			bottom: 0;
		}

		.player-controls {
			display: flex;
			align-items: center;
			height: 100%;
			gap: 30px;
			width: 1000px; // 新增最大宽度限制
			margin: 0 auto;

			.left-controls {
				display: flex;
				gap: 12px;
				align-items: center;

				.control-btn {
					width: 25px;
					height: 25px;
					padding: 8px;
					background-color: rgba(236, 65, 65, 0.9);
					border: none;
					transition: all 0.3s;

					&:hover {
						background-color: rgba(236, 65, 65, 1);
						transform: scale(1.1);
					}

					.el-icon {
						color: white;
						font-size: 18px;
					}
				}

				.play-btn {
					width: 30px;
					height: 30px;
					background-color: rgba(236, 65, 65, 0.9);
					border: none;
					transition: all 0.3s;

					&:hover {
						background-color: rgba(236, 65, 65, 1);
						transform: scale(1.05);
					}

					.el-icon {
						color: white;
					}
				}
			}
			.album-cover {
				margin-right: 15px;
				flex-shrink: 0;

				.cover-image {
					width: 50px;
					height: 50px;
					border-radius: 4px;
					box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
					object-fit: cover;

					// 加载失败时显示背景
					background: linear-gradient(45deg, #2c3e50, #3498db);
				}

				// 没有封面时的占位符
				&:empty::before {
					content: '\f001'; // 音乐图标
					font-family: 'Font Awesome 5 Free';
					display: flex;
					align-items: center;
					justify-content: center;
					width: 50px;
					height: 50px;
					background: #555;
					color: #fff;
					border-radius: 4px;
				}
			}
			.info-container {
				flex: 1;
				width: 650px;
				display: flex;
				flex-direction: column;
				gap: 6px;

				.song-info {
					width: 650px;
					.title-artist {
						display: flex;
						align-items: center;
						gap: 12px;
						overflow: hidden;
						.title {
							color: #fff;
							font-size: 14px;
							font-weight: 500;
							white-space: nowrap;
							overflow: hidden;
							text-overflow: ellipsis;
						}

						.artist {
							color: #aaa;
							font-size: 12px;
							white-space: nowrap;
							overflow: hidden;
							text-overflow: ellipsis;
						}
					}
				}

				.progress-wrapper {
					display: flex;
					align-items: center;
					gap: 10px;
					width: 650px;

					.progress-bar {
						flex: 1;
						height: 4px;
						background: #555;
						border-radius: 2px;
						transition: all 0.2s;

						&::-webkit-slider-thumb {
							-webkit-appearance: none;
							width: 12px;
							height: 12px;
							background: #ec4141;
							border-radius: 50%;
							cursor: pointer;
							transition: all 0.2s;
						}

						&:hover {
							height: 6px;

							&::-webkit-slider-thumb {
								transform: scale(1.2);
							}
						}
					}

					.time-display {
						display: flex;
						gap: 5px;
						color: #fff;
						font-size: 12px;
						width: 110px;

						span:last-child {
							color: #aaa;
							&::before {
								content: '/';
								margin: 0 4px;
							}
						}
					}
				}
			}
			.right-controls {
				position: relative;
				.playlist-btn {
					width: 36px;
					height: 36px;
					background: rgba(255, 255, 255, 0.1);
					border: none;
					color: #fff;
					transition: all 0.3s;

					&:hover {
						background: rgba(255, 255, 255, 0.2);
						transform: scale(1.1);
					}
				}

				.playlist-panel {
					position: absolute;
					bottom: 50px;
					right: 0;
					width: 350px;
					max-height: 500px;
					background: rgba(40, 40, 40, 0.95);
					border-radius: 8px;
					backdrop-filter: blur(10px);
					box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
					overflow: hidden;
					z-index: 10000;

					.playlist-header {
						display: flex;
						justify-content: space-between;
						align-items: center;
						padding: 16px;
						font-size: 14px;
						color: #fff;
						border-bottom: 1px solid rgba(255, 255, 255, 0.1);

						.clear-btn {
							padding: 4px 8px;
							color: #ec4141;
							transition: all 0.3s;

							&:hover {
								color: #ff6363;
								background: rgba(236, 65, 65, 0.1);
							}

							.el-icon {
								margin-right: 4px;
								font-size: 14px;
							}

							span {
								font-size: 12px;
							}
						}
					}

					.playlist-items {
						overflow-y: auto;
						max-height: 400px;

						.playlist-item {
							display: flex;
							align-items: center;
							padding: 12px 16px;
							cursor: pointer;
							transition: all 0.2s;
							border-bottom: 1px solid rgba(255, 255, 255, 0.05);

							&:hover {
								background: rgba(255, 255, 255, 0.05);
							}

							&.active {
								background: rgba(236, 65, 65, 0.15);
								.title {
									color: #ec4141;
								}
							}

							.song-info {
								flex: 1;
								min-width: 0;
								.title {
									color: #fff;
									font-size: 13px;
									margin-bottom: 4px;
									white-space: nowrap;
									overflow: hidden;
									text-overflow: ellipsis;
								}
								.artist {
									color: #aaa;
									font-size: 12px;
								}
							}

							.duration {
								color: #888;
								font-size: 12px;
								margin: 0 12px;
							}

							.playing-icon {
								color: #ec4141;
								font-size: 14px;
							}

							.delete-btn {
								margin-right: 10px;
								opacity: 0.5;
								transition: all 0.2s;
								&:hover {
									opacity: 1;
									transform: scale(1.1);
									color: #ec4141;
								}
							}
						}
					}
				}
			}
		}
	}
</style>
