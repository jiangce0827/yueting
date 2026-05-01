<!-- @/views/frontend/user/Follow.vue -->
<template>
	<div class="follow-container">
		<div class="main-content">
			<!-- 左侧内容 -->
			<div class="left-content">
			<!-- 顶部区域 -->
			<div class="notes-header">
				<span class="notes-title">笔记</span>
				<el-button type="primary" size="small" @click="openNoteDialog">发笔记</el-button>
			</div>
			<!-- 红色横线 -->
			<div class="red-line"></div>

			<!-- 笔记列表 -->
			<div class="notes-list">
				<div v-if="notes.length === 0" class="empty-tip">还没有关注用户的笔记</div>
				<div v-for="note in notes" :key="note.noteId" class="note-card">
					<!-- 用户信息 -->
					<div class="note-user">
						<img :src="note.avatarUrl || defaultAvatar" class="user-avatar" />
						<div class="user-info">
							<div class="user-name-row">
								<span class="user-name">{{ note.nickname }}</span>
								<span v-if="note.forwardSourceId" class="share-tag">转发</span>
								<span v-else-if="note.musicType === 1" class="share-tag">分享单曲</span>
								<span v-else-if="note.musicType === 2" class="share-tag">分享歌手</span>
								<span v-else-if="note.musicType === 3" class="share-tag">分享专辑</span>
								<span v-else-if="note.musicType === 4" class="share-tag">分享歌单</span>
							</div>
							<span class="note-time">{{ formatRelativeTime(note.createdAt) }}</span>
						</div>
						<el-dropdown v-if="authStore.user && note.userId == authStore.user.userId" trigger="click" @command="(cmd) => handleNoteCommand(cmd, note)" class="note-more-dropdown">
							<span class="note-more-btn">
								<el-icon><MoreFilled /></el-icon>
							</span>
							<template #dropdown>
								<el-dropdown-menu>
									<el-dropdown-item command="delete">删除</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</div>
					<!-- 笔记内容 -->
					<div class="note-content">{{ note.content }}</div>
					<!-- 配乐信息 -->
					<div v-if="note.musicName" class="note-music" :class="{ 'song-type': note.musicType === 1, 'artist-type': note.musicType === 2, 'album-type': note.musicType === 3, 'playlist-type': note.musicType === 4 }">
						<!-- 单曲类型 -->
						<template v-if="note.musicType === 1">
							<div class="song-card" @click.stop="playSong(note)">
								<img v-if="note.songCoverUrl" :src="note.songCoverUrl" class="song-cover" />
								<div v-else class="song-cover default-cover">
									<el-icon><VideoPlay /></el-icon>
								</div>
								<div class="song-info">
									<div class="song-name" @click.stop="goToSongDetail(note.musicId)">{{ note.songName || note.musicName }}</div>
									<div class="artist-name" @click.stop="goToArtistDetail(note.artistId)">{{ note.artistName || '' }}</div>
								</div>
								<el-icon class="play-icon" @click.stop="playSong(note)"><VideoPlay /></el-icon>
							</div>
						</template>
						<!-- 歌手类型 -->
						<template v-else-if="note.musicType === 2">
							<div class="artist-card" @click.stop="goToArtistDetail(note.artistId)">
								<img v-if="note.artistAvatarUrl" :src="note.artistAvatarUrl" class="artist-avatar" />
								<div v-else class="artist-avatar default-avatar">
									<el-icon><UserFilled /></el-icon>
								</div>
								<div class="artist-info">
									<div class="artist-name">{{ note.artistName || note.musicName }}</div>
									<div class="artist-followers">{{ note.artistFollowerCount || 0 }} 粉丝</div>
								</div>
							</div>
						</template>
						<!-- 专辑类型 -->
						<template v-else-if="note.musicType === 3">
							<div class="album-card" @click.stop="goToAlbumDetail(note.albumId)">
								<img v-if="note.albumCoverUrl" :src="note.albumCoverUrl" class="album-cover" />
								<div v-else class="album-cover default-cover">
									<el-icon><VideoPlay /></el-icon>
								</div>
								<div class="album-info">
									<div class="album-name">{{ note.albumName || note.musicName }}</div>
									<div class="album-author">{{ note.albumAuthorName || '' }}</div>
								</div>
							</div>
						</template>
						<!-- 歌单类型 -->
						<template v-else-if="note.musicType === 4">
							<div class="playlist-card" @click.stop="goToPlaylistDetail(note.playlistId)">
								<img v-if="note.playlistCoverUrl" :src="note.playlistCoverUrl" class="playlist-cover" />
								<div v-else class="playlist-cover default-cover">
									<el-icon><VideoPlay /></el-icon>
								</div>
								<div class="playlist-info">
									<div class="playlist-name">{{ note.playlistName || note.musicName }}</div>
									<div class="playlist-creator">by {{ note.playlistCreatorName || '' }}</div>
								</div>
							</div>
						</template>
						<!-- 其他类型 -->
						<template v-else>
							<el-icon><VideoPlay /></el-icon>
							<span>{{ note.musicName }}</span>
						</template>
					</div>
					<!-- 笔记封面 -->
					<div v-if="note.coverUrl" class="note-cover">
						<img :src="note.coverUrl" />
					</div>
					<!-- 笔记图片列表 -->
					<div v-if="note.imageUrls && note.imageUrls.length > 0" style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px;">
						<div v-for="(imgUrl, imgIndex) in note.imageUrls" :key="imgIndex" style="width: 120px; height: 120px; border-radius: 4px; overflow: hidden;">
							<img :src="imgUrl" style="width: 100%; height: 100%; object-fit: cover;" />
						</div>
					</div>
					<!-- 转发的源笔记卡片 -->
					<div v-if="note.isForwarded" class="source-note-card">
						<template v-if="note.sourceNoteDeleted">
							<div class="source-note-deleted">笔记已被删除</div>
						</template>
						<template v-else-if="note.sourceNote">
							<div class="source-note-row">
								<img :src="note.sourceNote.avatarUrl || defaultAvatar" class="source-note-avatar" />
								<div class="source-note-text-col">
									<span class="source-note-nickname">{{ note.sourceNote.nickname }}</span>
									<span class="source-note-text">{{ note.sourceNote.content }}</span>
								</div>
							</div>
							<!-- 源笔记配乐（下一行） -->
							<div v-if="note.sourceNote.musicName" class="source-note-music">
								<!-- 单曲类型 -->
								<template v-if="note.sourceNote.musicType === 1">
									<div class="mini-card" @click.stop="playSong(note.sourceNote)">
										<img v-if="note.sourceNote.songCoverUrl" :src="note.sourceNote.songCoverUrl" class="mini-cover" />
										<div v-else class="mini-cover default-cover">
											<el-icon><VideoPlay /></el-icon>
										</div>
										<div class="mini-info">
											<div class="mini-title">{{ note.sourceNote.songName || note.sourceNote.musicName }}</div>
											<div class="mini-sub">{{ note.sourceNote.artistName || '' }}</div>
										</div>
										<el-icon class="mini-play"><VideoPlay /></el-icon>
									</div>
								</template>
								<!-- 歌手类型 -->
								<template v-else-if="note.sourceNote.musicType === 2">
									<div class="mini-card" @click.stop="goToArtistDetail(note.sourceNote.musicId)">
										<img v-if="note.sourceNote.artistAvatarUrl" :src="note.sourceNote.artistAvatarUrl" class="mini-cover round" />
										<div v-else class="mini-cover default-cover round">
											<el-icon><UserFilled /></el-icon>
										</div>
										<div class="mini-info">
											<div class="mini-title">{{ note.sourceNote.artistName || note.sourceNote.musicName }}</div>
											<div class="mini-sub">{{ note.sourceNote.artistFollowerCount || 0 }} 粉丝</div>
										</div>
									</div>
								</template>
								<!-- 专辑类型 -->
								<template v-else-if="note.sourceNote.musicType === 3">
									<div class="mini-card" @click.stop="goToAlbumDetail(note.sourceNote.musicId)">
										<img v-if="note.sourceNote.albumCoverUrl" :src="note.sourceNote.albumCoverUrl" class="mini-cover" />
										<div v-else class="mini-cover default-cover">
											<el-icon><VideoPlay /></el-icon>
										</div>
										<div class="mini-info">
											<div class="mini-title">{{ note.sourceNote.albumName || note.sourceNote.musicName }}</div>
											<div class="mini-sub">{{ note.sourceNote.albumAuthorName || '' }}</div>
										</div>
									</div>
								</template>
								<!-- 歌单类型 -->
								<template v-else-if="note.sourceNote.musicType === 4">
									<div class="mini-card" @click.stop="goToPlaylistDetail(note.sourceNote.musicId)">
										<img v-if="note.sourceNote.playlistCoverUrl" :src="note.sourceNote.playlistCoverUrl" class="mini-cover" />
										<div v-else class="mini-cover default-cover">
											<el-icon><VideoPlay /></el-icon>
										</div>
										<div class="mini-info">
											<div class="mini-title">{{ note.sourceNote.playlistName || note.sourceNote.musicName }}</div>
											<div class="mini-sub">by {{ note.sourceNote.playlistCreatorName || '' }}</div>
										</div>
									</div>
								</template>
								<!-- 其他类型 -->
								<template v-else>
									<el-icon><VideoPlay /></el-icon>
									<span>{{ note.sourceNote.musicName }}</span>
								</template>
							</div>
							<!-- 源笔记图片 -->
							<div v-if="note.sourceNote.imageUrls && note.sourceNote.imageUrls.length > 0" style="display: flex; flex-wrap: wrap; gap: 4px; margin-top: 8px;">
								<div v-for="(imgUrl, imgIndex) in note.sourceNote.imageUrls" :key="imgIndex" style="width: 60px; height: 60px; border-radius: 4px; overflow: hidden;">
									<img :src="imgUrl" style="width: 100%; height: 100%; object-fit: cover;" />
								</div>
							</div>
						</template>
					</div>
					<!-- 操作栏 -->
					<div class="note-actions">
						<span class="action-item" :class="{ active: note.liked }" @click="toggleLike(note)">
							<span class="like-icon">{{ note.liked ? '❤️' : '🤍' }}</span>
							<span>{{ note.likeCount || 0 }}</span>
						</span>
						<span class="action-item" :class="{ active: activeNoteId === note.noteId }" @click="toggleCommentArea(note)">
							<el-icon><ChatLineRound /></el-icon>
							<span>{{ note.commentCount || 0 }}</span>
						</span>
						<span class="action-item" @click="openForwardDialog(note)">
							<el-icon><Share /></el-icon>
							<span>{{ note.forwardCount || 0 }}</span>
						</span>
					</div>
					<!-- 评论展开区域 -->
					<div v-if="activeNoteId === note.noteId" class="comment-area">
						<!-- 评论区输入框 -->
						<div class="comment-input-wrapper">
							<el-input v-model="commentForm.content" type="textarea" :rows="2" placeholder="写下你的评论..." maxlength="200" show-word-limit />
							<el-button type="primary" size="small" class="comment-submit-btn" @click="submitComment">发表评论</el-button>
						</div>
						<!-- 评论列表 -->
						<div v-if="note.comments && note.comments.length > 0" class="comment-list">
							<div v-for="comment in note.comments" :key="comment.commentId" class="comment-item">
								<img :src="comment.avatarUrl || defaultAvatar" class="comment-avatar" />
								<div class="comment-body">
									<div class="comment-header">
										<span class="comment-user">{{ comment.nickname }}</span>
										<span v-if="comment.replyToNickname" class="reply-tag">回复 {{ comment.replyToNickname }}</span>
										<span class="comment-action">评论</span>
										<span class="comment-time">{{ formatRelativeTime(comment.createdAt) }}</span>
										<el-dropdown v-if="authStore.user && comment.userId == authStore.user.userId" trigger="click" @command="(cmd) => handleCommentCommand(cmd, note, comment)">
											<span class="comment-more-btn">
												<el-icon><MoreFilled /></el-icon>
											</span>
											<template #dropdown>
												<el-dropdown-menu>
													<el-dropdown-item command="delete">删除</el-dropdown-item>
												</el-dropdown-menu>
											</template>
										</el-dropdown>
									</div>
									<div class="comment-text">{{ comment.content }}</div>
									<!-- 引用内容 -->
									<div v-if="comment.quotedCommentDeleted" class="quote-content deleted">
										<div class="quote-text">该评论已删除</div>
									</div>
									<div v-else-if="comment.quoteContent" class="quote-content">
										<div class="quote-text">{{ comment.quoteNickname }}:{{ comment.quoteContent }}</div>
									</div>
									<!-- 回复输入框 -->
									<div v-if="replyingToId === comment.commentId" class="reply-input-wrapper">
										<el-input v-model="replyContent" type="textarea" :rows="1" :placeholder="`回复 ${comment.nickname}...`" maxlength="200" show-word-limit />
										<div class="reply-input-actions">
											<el-button size="small" @click="cancelReply">取消</el-button>
											<el-button type="primary" size="small" @click="submitComment">发送</el-button>
										</div>
									</div>
								</div>
								<span class="reply-btn" @click="openReply(comment)">回复</span>
							</div>
						</div>
						<div v-else class="comment-empty">暂无评论，快来抢沙发</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 右侧内容 -->
		<div class="right-content">
			<!-- 个人信息卡片 -->
			<UserProfileCard
				:user-info="authStore.user"
				:stats="userStats"
				@go-to-notes="goToMyNotes"
				@go-to-following="goToMyFollowing"
				@go-to-followers="goToMyFollowers"
			/>

			<!-- 人气用户 -->
			<div class="popular-users-card">
				<div class="card-title-row">
					<div class="card-title">人气用户</div>
					<el-button type="primary" link size="small" @click="refreshPopularUsers">换一批</el-button>
				</div>
				<div class="popular-list">
					<div v-for="user in popularUsers" :key="user.userId" class="popular-item" @click="goToUserHome(user.userId)">
						<img :src="user.avatarUrl || defaultAvatar" class="popular-avatar" />
						<div class="popular-info">
							<div class="popular-name">{{ user.nickname }}</div>
							<div class="popular-followers">{{ user.followerCount }} 粉丝</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		</div>
	</div>

	<!-- 发笔记弹框 -->
	<el-dialog v-model="noteDialogVisible" title="发笔记" width="600px" @close="resetNoteForm">
		<el-form :model="noteForm" label-width="80px">
			<el-form-item label="笔记内容">
				<el-input v-model="noteForm.content" type="textarea" :rows="4" placeholder="分享你的想法..." maxlength="500" show-word-limit />
			</el-form-item>
			<el-form-item label="配乐">
				<div class="music-selector">
					<span v-if="noteForm.musicName" class="selected-music">
						<el-icon><VideoPlay /></el-icon>
						{{ noteForm.musicName }}
						<el-icon class="remove-icon" @click="removeMusic"><Close /></el-icon>
					</span>
					<el-button v-else link @click="openMusicDialog">给笔记配上音乐</el-button>
				</div>
			</el-form-item>
			<el-form-item label="图片">
				<div style="display: flex; flex-wrap: wrap; gap: 8px; align-items: center;">
					<div v-for="(url, index) in noteForm.imageUrls" :key="index" style="position: relative; width: 80px; height: 80px;">
						<img :src="url" style="width: 100%; height: 100%; object-fit: cover; border-radius: 4px;" />
						<span @click="removeImage(index)" style="position: absolute; top: 2px; right: 2px; background: rgba(0,0,0,0.5); color: white; border-radius: 50%; width: 20px; height: 20px; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 12px;">×</span>
					</div>
					<el-button v-if="noteForm.imageUrls.length < 9" @click="triggerFileInput" style="width: 80px; height: 80px; display: flex; align-items: center; justify-content: center;">
						<el-icon style="font-size: 24px;"><Plus /></el-icon>
					</el-button>
				</div>
				<input
					ref="fileInputRef"
					type="file"
					accept="image/*"
					multiple
					style="display: none"
					@change="handleFileChange"
				/>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="noteDialogVisible = false">取消</el-button>
			<el-button type="primary" @click="submitNote">发布</el-button>
		</template>
	</el-dialog>

	<!-- 音乐选择弹框 -->
	<el-dialog v-model="musicDialogVisible" title="选择音乐" width="500px">
		<div class="music-search">
			<el-input v-model="musicKeyword" placeholder="搜索音乐/歌手/专辑/歌单" @keyup.enter="searchMusic">
				<template #append>
					<el-button icon="Search" @click="searchMusic" />
				</template>
			</el-input>
		</div>
		<div class="music-type-tabs">
			<el-radio-group v-model="musicType" @change="changeMusicType">
				<el-radio :label="1">单曲</el-radio>
				<el-radio :label="2">歌手</el-radio>
				<el-radio :label="3">专辑</el-radio>
				<el-radio :label="4">歌单</el-radio>
			</el-radio-group>
		</div>
		<div class="music-results">
			<div v-if="musicResults.length === 0" class="empty-music">暂无搜索结果</div>
			<div v-for="item in musicResults" :key="item.id" class="music-result-item" @click="selectMusic(item)">
				<img v-if="item.coverUrl" :src="item.coverUrl" class="music-cover" />
				<div v-else class="music-cover default-cover">
					<span class="music-icon">♪</span>
				</div>
				<div class="music-info">
					<div class="music-name">{{ item.name }}</div>
					<div class="music-artist">{{ item.artist || item.author || '' }}</div>
				</div>
			</div>
		</div>
	</el-dialog>

	<!-- 转发弹框 -->
	<el-dialog v-model="forwardDialogVisible" title="转发笔记" width="500px">
		<el-input v-model="forwardContent" type="textarea" :rows="4" placeholder="说点什么..." maxlength="200" show-word-limit />
		<template #footer>
			<el-button @click="forwardDialogVisible = false">取消</el-button>
			<el-button type="primary" @click="submitForward">转发</el-button>
		</template>
	</el-dialog>
</template>

<script setup>
	import { ref, reactive, onMounted } from 'vue';
	import { useRouter } from 'vue-router';
	import { ElMessage } from 'element-plus';
	import { VideoPlay, ChatLineRound, Share, Close, UserFilled, MoreFilled, Plus } from '@element-plus/icons-vue';
	import { useAuthStore } from '@/stores/frontend/auth';
	import { usePlayerStore } from '@/stores/frontend/player';
	import { apiGetFollowingNotes, apiGetMyNotes, apiCreateNote, apiDeleteNote, apiLikeNote, apiUnlikeNote, apiCommentNote, apiDeleteComment, apiGetPopularUsers } from '@/api/frontend/note';
	import { apiUploadImage } from '@/api/frontend/common';
	import { apiSearchSongByKeyword } from '@/api/frontend/song';
	import { apiSearchArtist } from '@/api/frontend/artist';
	import { apiSearchAlbumByKeyword } from '@/api/frontend/album';
	import { apiSearchPlaylistByKeyword } from '@/api/frontend/playlist';
import { formatRelativeTime } from '@/utils/format.js';
	import UserProfileCard from '@/components/frontend/user/UserProfileCard.vue';

	const router = useRouter();
	const authStore = useAuthStore();
	const playerStore = usePlayerStore();
	const defaultAvatar = 'https://cube.elemecdn.com/3/7c/4ea1f28e1d89defd2b7bdbDb3b1acpng.png';

	// 笔记相关
	const notes = ref([]);
	const noteDialogVisible = ref(false);
	const noteForm = reactive({
		content: '',
		musicId: null,
		musicType: null,
		musicName: null,
		imageUrls: [],
	});

	// 判断笔记是否属于当前用户
	const isOwner = (note) => {
		return String(note.userId) === String(authStore.user?.userId);
	};

	// 音乐搜索相关
	const musicDialogVisible = ref(false);
	const musicKeyword = ref('');
	const musicType = ref(1);
	const musicResults = ref([]);

	// 转发相关
	const forwardDialogVisible = ref(false);
	const forwardContent = ref('');
	const forwardSourceNoteId = ref(null);

	// 评论相关
	const activeNoteId = ref(null);
	const replyingToId = ref(null);
	const currentNote = ref({});
	const commentForm = reactive({
		content: '',
		parentId: null,
		replyToUserId: null,
	});
	const replyContent = ref('');

	// 用户统计
	const userStats = reactive({
		noteCount: 0,
		followingCount: 0,
		followerCount: 0,
	});

	// 热门用户
	const popularUsers = ref([]);
	const popularUsersPage = ref(0);
	const popularUsersSize = 3;
	let allPopularUsers = [];

	// 图片上传相关
	const fileInputRef = ref(null);

	const triggerFileInput = () => {
		fileInputRef.value?.click();
	};

	const handleFileChange = async (event) => {
		const files = event.target.files;
		if (!files || files.length === 0) return;

		for (let i = 0; i < files.length; i++) {
			if (noteForm.imageUrls.length >= 9) break;
			const file = files[i];
			const formData = new FormData();
			formData.append('image', file);
			try {
				const res = await apiUploadImage(formData);
				if (res.data.code === 1) {
					noteForm.imageUrls.push(res.data.data);
				} else {
					ElMessage.error(res.data.msg || '上传失败');
				}
			} catch (error) {
				console.error('上传图片失败:', error);
				ElMessage.error('上传失败');
			}
		}
		event.target.value = '';
	};

	const removeImage = (index) => {
		noteForm.imageUrls.splice(index, 1);
	};

	// 加载笔记列表
	const loadNotes = async () => {
		try {
			const res = await apiGetFollowingNotes();
			if (res.data.code === 1) {
				notes.value = res.data.data || [];
			}
		} catch (error) {
			console.error('获取笔记列表失败:', error);
		}
	};

	// 加载我的笔记数量
	const loadMyNotesCount = async () => {
		try {
			const res = await apiGetMyNotes();
			if (res.data.code === 1) {
				userStats.noteCount = res.data.data?.length || 0;
			}
		} catch (error) {
			console.error('获取笔记数量失败:', error);
		}
	};

	// 加载热门用户
	const loadPopularUsers = async () => {
		try {
			const res = await apiGetPopularUsers(30);
			if (res.data.code === 1) {
				allPopularUsers = res.data.data || [];
				popularUsersPage.value = 0;
				updatePopularUsersDisplay();
			}
		} catch (error) {
			console.error('获取热门用户失败:', error);
		}
	};

	// 换一批
	const refreshPopularUsers = () => {
		popularUsersPage.value = (popularUsersPage.value + 1) % Math.ceil(allPopularUsers.length / popularUsersSize);
		updatePopularUsersDisplay();
	};

	// 更新热门用户显示
	const updatePopularUsersDisplay = () => {
		const start = popularUsersPage.value * popularUsersSize;
		popularUsers.value = allPopularUsers.slice(start, start + popularUsersSize);
	};

	// 打开发笔记弹框
	const openNoteDialog = () => {
		if (!authStore.isLoggedIn) {
			router.push('/login');
			return;
		}
		noteDialogVisible.value = true;
	};

	// 重置笔记表单
	const resetNoteForm = () => {
		noteForm.content = '';
		noteForm.musicId = null;
		noteForm.musicType = null;
		noteForm.musicName = null;
		noteForm.imageUrls = [];
	};

	// 提交笔记
	const submitNote = async () => {
		if (!noteForm.content.trim()) {
			ElMessage.warning('请输入笔记内容');
			return;
		}
		try {
			const res = await apiCreateNote(noteForm);
			if (res.data.code === 1) {
				ElMessage.success('发布成功');
				noteDialogVisible.value = false;
				resetNoteForm();
				loadNotes();
			} else {
				ElMessage.error(res.data.msg || '发布失败');
			}
		} catch (error) {
			ElMessage.error('发布失败');
		}
	};

	// 打开音乐选择弹框
	const openMusicDialog = () => {
		musicDialogVisible.value = true;
		musicKeyword.value = '';
		musicResults.value = [];
	};

	// 搜索音乐
	const searchMusic = async () => {
		if (!musicKeyword.value.trim()) return;
		const params = { keyword: musicKeyword.value, pageNum: 1, pageSize: 20 };
		try {
			switch (musicType.value) {
				case 1: {
					const res = await apiSearchSongByKeyword(params);
					if (res.data.code === 1) {
						musicResults.value = (res.data.data.records || []).map((s) => ({
							id: s.songId,
							name: s.songName,
							artist: s.artistName,
							coverUrl: s.coverUrl,
						}));
					}
					break;
				}
				case 2: {
					const res = await apiSearchArtist(params);
					if (res.data.code === 1) {
						musicResults.value = (res.data.data.records || []).map((a) => ({
							id: a.artistId,
							name: a.artistName,
							artist: '',
							coverUrl: a.artistAvatarUrl,
						}));
					}
					break;
				}
				case 3: {
					const res = await apiSearchAlbumByKeyword(params);
					if (res.data.code === 1) {
						musicResults.value = (res.data.data.records || []).map((a) => ({
							id: a.albumId,
							name: a.albumName,
							artist: a.artistName,
							coverUrl: a.coverUrl,
						}));
					}
					break;
				}
				case 4: {
					const res = await apiSearchPlaylistByKeyword(params);
					if (res.data.code === 1) {
						musicResults.value = (res.data.data.records || []).map((p) => ({
							id: p.playlistId,
							name: p.playlistName,
							artist: p.creatorNickname || '',
							coverUrl: p.coverUrl,
						}));
					}
					break;
				}
			}
		} catch (error) {
			console.error('搜索音乐失败:', error);
		}
	};

	// 切换音乐类型
	const changeMusicType = () => {
		musicResults.value = [];
	};

	// 选择音乐
	const selectMusic = (item) => {
		noteForm.musicId = item.id;
		noteForm.musicType = musicType.value;
		noteForm.musicName = item.name;
		musicDialogVisible.value = false;
	};

	// 移除音乐
	const removeMusic = () => {
		noteForm.musicId = null;
		noteForm.musicType = null;
		noteForm.musicName = null;
	};

	// 播放歌曲
	const playSong = (note) => {
		const song = {
			songId: note.musicId,
			songName: note.songName || note.musicName,
			artistNames: note.artistName || '',
			coverUrl: note.songCoverUrl || '',
			albumId: note.albumId,
			albumName: note.albumName || '',
		};
		playerStore.playSong(song);
	};

	// 跳转歌曲详情
	const goToSongDetail = (songId) => {
		router.push(`/song?id=${songId}`);
	};

	// 跳转歌手详情
	const goToArtistDetail = (artistId) => {
		if (artistId) {
			router.push(`/artist?id=${artistId}`);
		}
	};

	// 跳转专辑详情
	const goToAlbumDetail = (albumId) => {
		if (albumId) {
			router.push(`/album?id=${albumId}`);
		}
	};

	// 跳转歌单详情
	const goToPlaylistDetail = (playlistId) => {
		if (playlistId) {
			router.push(`/playlist?id=${playlistId}`);
		}
	};

	// 点赞/取消点赞
	const toggleLike = async (note) => {
		try {
			if (note.liked) {
				await apiUnlikeNote(note.noteId);
				note.likeCount--;
			} else {
				await apiLikeNote(note.noteId);
				note.likeCount++;
			}
			note.liked = !note.liked;
		} catch (error) {
			ElMessage.error('操作失败');
		}
	};

	// 处理笔记操作命令
	const handleNoteCommand = async (command, note) => {
		if (command === 'delete') {
			try {
				await apiDeleteNote(note.noteId);
				ElMessage.success('删除成功');
				loadNotes();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		}
	};

	// 处理评论操作命令
	const handleCommentCommand = async (command, note, comment) => {
		if (command === 'delete') {
			try {
				await apiDeleteComment(note.noteId, comment.commentId);
				ElMessage.success('删除成功');
				loadNotes();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		}
	};

	// 打开转发弹框
	const openForwardDialog = (note) => {
		forwardSourceNoteId.value = note.noteId;
		forwardContent.value = '';
		forwardDialogVisible.value = true;
	};

	// 提交转发
	const submitForward = async () => {
		try {
			const form = {
				content: forwardContent.value || '',
				forwardSourceId: forwardSourceNoteId.value,
			};
			await apiCreateNote(form);
			ElMessage.success('转发成功');
			forwardDialogVisible.value = false;
			forwardContent.value = '';
			forwardSourceNoteId.value = null;
			loadNotes();
		} catch (error) {
			ElMessage.error('转发失败');
		}
	};

	// 展开/收起评论区域
	const toggleCommentArea = (note) => {
		if (activeNoteId.value === note.noteId) {
			activeNoteId.value = null;
		} else {
			activeNoteId.value = note.noteId;
			currentNote.value = note;
			commentForm.content = '';
			commentForm.parentId = null;
			commentForm.replyToUserId = null;
			replyingToId.value = null;
		}
	};

	// 打开发回复输入框
	const openReply = (comment) => {
		replyContent.value = '';
		commentForm.parentId = comment.commentId;
		commentForm.replyToUserId = comment.userId;
		replyingToId.value = comment.commentId;
	};

	// 取消回复
	const cancelReply = () => {
		replyingToId.value = null;
		replyContent.value = '';
		commentForm.parentId = null;
		commentForm.replyToUserId = null;
	};

	// 提交评论
	const submitComment = async () => {
		const content = replyingToId.value ? replyContent.value : commentForm.content;
		if (!content.trim()) {
			ElMessage.warning('请输入评论内容');
			return;
		}
		try {
			const param = { ...commentForm, content };
			const res = await apiCommentNote(currentNote.value.noteId, param);
			if (res.data.code === 1) {
				ElMessage.success('评论成功');
				commentForm.content = '';
				commentForm.parentId = null;
				commentForm.replyToUserId = null;
				replyContent.value = '';
				replyingToId.value = null;
				loadNotes();
			}
		} catch (error) {
			ElMessage.error('评论失败');
		}
	};

	// 跳转用户主页
	const goToUserHome = (userId) => {
		router.push(`/user/home?id=${userId}`);
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

	onMounted(() => {
		loadNotes();
		loadMyNotesCount();
		loadPopularUsers();
		// 初始化用户统计
		if (authStore.user) {
			userStats.noteCount = authStore.user.noteCount || 0;
			userStats.followingCount = authStore.user.followingCount || 0;
			userStats.followerCount = authStore.user.followerCount || 0;
		}
	});
</script>

<style lang="scss" scoped>
	.follow-container {
		width: 1000px;
		margin: 0 auto;
		padding: 20px;

		.main-content {
			display: flex;
			gap: 20px;
		}

		.left-content {
			flex: 3;
			background: white;
			padding: 10px 20px;
			min-width: 0;

			.notes-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 10px;

				.notes-title {
					font-size: 20px;
					font-weight: bold;
				}
			}

			.red-line {
				height: 2px;
				background: #ef4444;
				margin-bottom: 20px;
			}

			.notes-list {
				.empty-tip {
					text-align: center;
					color: #999;
					padding: 40px 0;
				}

				.note-card {
					background: #fff;
					border-radius: 8px;
					padding: 16px;
					margin-bottom: 16px;
					box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

					.note-user {
						display: flex;
						align-items: center;
						margin-bottom: 12px;
						position: relative;

						.user-avatar {
							width: 40px;
							height: 40px;
							border-radius: 50%;
							margin-right: 12px;
						}

						.user-info {
							display: flex;
							flex-direction: column;

							.user-name {
								font-weight: bold;
								color: #333;
							}

							.user-name-row {
								display: flex;
								align-items: center;
								gap: 6px;
							}

							.share-tag {
								font-size: 12px;
								color: #999;
								font-weight: normal;
							}

							.note-time {
								font-size: 12px;
								color: #999;
							}
						}

						.note-more-btn {
							cursor: pointer;
							color: #999;
							padding: 4px;

							&:hover {
								color: #333;
							}
						}

						.note-more-dropdown {
							position: absolute;
							right: 0;
							top: 50%;
							transform: translateY(-50%);
						}
					}

					.note-content {
						color: #333;
						line-height: 1.6;
						margin-bottom: 12px;
					}

					.note-music {
						display: flex;
						align-items: center;
						gap: 8px;
						padding: 8px 12px;
						background: #f5f5f5;
						border-radius: 4px;
						margin-bottom: 12px;
						color: #666;

						&.song-type,
						&.artist-type,
						&.album-type,
						&.playlist-type {
							padding: 0;
							background: none;
						}

						.song-card {
							display: flex;
							align-items: center;
							width: 100%;
							padding: 12px;
							background: #fff;
							border: 1px solid #eee;
							border-radius: 8px;
							cursor: pointer;

							&:hover {
								background: #f5f5f5;
							}

							.song-cover {
								width: 50px;
								height: 50px;
								border-radius: 4px;
								margin-right: 12px;

								&.default-cover {
									background: #eee;
									display: flex;
									align-items: center;
									justify-content: center;
									color: #999;
								}
							}

							.song-info {
								flex: 1;
								min-width: 0;

								.song-name {
									font-size: 14px;
									color: #333;
									overflow: hidden;
									text-overflow: ellipsis;
									white-space: nowrap;
									cursor: pointer;

									&:hover {
										color: #409eff;
									}
								}

								.artist-name {
									font-size: 12px;
									color: #999;
									overflow: hidden;
									text-overflow: ellipsis;
									white-space: nowrap;
									cursor: pointer;

									&:hover {
										color: #409eff;
									}
								}
							}

							.play-icon {
								font-size: 24px;
								color: #409eff;
							}
						}

						.artist-card {
							display: flex;
							align-items: center;
							width: 100%;
							padding: 12px;
							background: #fff;
							border: 1px solid #eee;
							border-radius: 8px;
							cursor: pointer;

							&:hover {
								background: #f5f5f5;
							}

							.artist-avatar {
								width: 50px;
								height: 50px;
								border-radius: 50%;
								margin-right: 12px;

								&.default-avatar {
									background: #eee;
									display: flex;
									align-items: center;
									justify-content: center;
									color: #999;
								}
							}

							.artist-info {
								flex: 1;

								.artist-name {
									font-size: 14px;
									font-weight: bold;
									color: #333;
								}

								.artist-followers {
									font-size: 12px;
									color: #999;
								}
							}
						}

						.album-card,
						.playlist-card {
							display: flex;
							align-items: center;
							width: 100%;
							padding: 12px;
							background: #fff;
							border: 1px solid #eee;
							border-radius: 8px;
							cursor: pointer;

							&:hover {
								background: #f5f5f5;
							}

							.album-cover,
							.playlist-cover {
								width: 50px;
								height: 50px;
								border-radius: 4px;
								margin-right: 12px;

								&.default-cover {
									background: #eee;
									display: flex;
									align-items: center;
									justify-content: center;
									color: #999;
								}
							}

							.album-info,
							.playlist-info {
								flex: 1;

								.album-name,
								.playlist-name {
									font-size: 14px;
									font-weight: bold;
									color: #333;
								}

								.album-author,
								.playlist-creator {
									font-size: 12px;
									color: #999;
								}
							}
						}
					}

					.note-cover {
						margin-bottom: 12px;

						img {
							max-width: 100%;
							border-radius: 4px;
						}
					}

					.source-note-card {
						background: #f5f5f5;
						border-radius: 8px;
						padding: 12px;
						margin-bottom: 12px;

						.source-note-row {
							display: flex;
							align-items: center;
							gap: 10px;
						}

						.source-note-avatar {
							width: 32px;
							height: 32px;
							border-radius: 50%;
							flex-shrink: 0;
						}

						.source-note-text-col {
							display: flex;
							align-items: center;
							gap: 6px;
							min-width: 0;
						}

						.source-note-nickname {
							color: #409eff;
							font-size: 13px;
							font-weight: 500;
							flex-shrink: 0;
						}

						.source-note-text {
							color: #333;
							font-size: 14px;
							line-height: 1.4;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}

						.source-note-music {
							margin-top: 8px;
						}

						.source-note-deleted {
							color: #999;
							font-size: 14px;
							text-align: center;
							padding: 10px 0;
						}

						// 横向小卡片样式
						.mini-card {
							display: flex;
							align-items: center;
							gap: 8px;
							padding: 6px 10px;
							background: #fff;
							border-radius: 6px;
							border: 1px solid #eee;
							cursor: pointer;
							min-width: 180px;

							&:hover {
								background: #f5f5f5;
							}
						}

						.mini-cover {
							width: 36px;
							height: 36px;
							border-radius: 4px;
							flex-shrink: 0;

							&.round {
								border-radius: 50%;
							}

							&.default-cover {
								background: #eee;
								display: flex;
								align-items: center;
								justify-content: center;
								color: #999;
							}
						}

						.mini-info {
							flex: 1;
							min-width: 0;
						}

						.mini-title {
							font-size: 13px;
							color: #333;
							font-weight: 500;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}

						.mini-sub {
							font-size: 11px;
							color: #999;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}

						.mini-play {
							color: #409eff;
							font-size: 18px;
						}
					}

					.note-actions {
						display: flex;
						gap: 24px;
						padding-top: 12px;
						border-top: 1px solid #eee;

						.action-item {
							display: flex;
							align-items: center;
							gap: 4px;
							color: #999;
							cursor: pointer;

							&:hover {
								color: #409eff;
							}

							&.active {
								color: #f56c6c;
							}

							.like-icon {
								font-size: 16px;
							}
						}
					}

					.comment-area {
						margin-top: 12px;
						padding-top: 12px;
						border-top: 1px solid #eee;

						.comment-input-wrapper {
							display: flex;
							gap: 8px;
							margin-bottom: 12px;

							.el-textarea {
								flex: 1;
							}

							.comment-submit-btn {
								align-self: flex-end;
							}
						}

						.comment-list {
							.comment-item {
								display: flex;
								gap: 10px;
								margin-bottom: 12px;

								.comment-avatar {
									width: 32px;
									height: 32px;
									border-radius: 50%;
									flex-shrink: 0;
								}

								.comment-body {
									flex: 1;
									min-width: 0;

									.comment-header {
										display: flex;
										align-items: center;
										gap: 4px;
										margin-bottom: 4px;

										.comment-user {
											color: #409eff;
											font-size: 14px;
											font-weight: 500;
										}

										.reply-tag {
											color: #666;
											font-size: 14px;
										}

										.comment-action {
											color: #999;
											font-size: 14px;
										}

										.comment-time {
											color: #999;
											font-size: 12px;
										}

										.comment-more-btn {
											color: #999;
											cursor: pointer;
											padding: 2px 4px;
											margin-left: auto;

											&:hover {
												color: #333;
											}
										}
									}

									.comment-text {
										color: #333;
										font-size: 14px;
										line-height: 1.6;
									}

									.quote-content {
										background: #f5f5f5;
										padding: 6px 10px;
										border-left: 3px solid #409eff;
										border-radius: 2px;
										font-size: 13px;
										color: #666;
										line-height: 1.5;
										margin-top: 6px;
										margin-left: 20px;
									}
								}

								.reply-btn {
									color: #999;
									font-size: 12px;
									cursor: pointer;
									align-self: flex-start;

									&:hover {
										color: #409eff;
									}
								}
							}
						}

						.comment-empty {
							text-align: center;
							color: #999;
							padding: 20px;
							font-size: 14px;
						}

						.reply-input-wrapper {
							margin-top: 8px;
							padding: 8px 12px;
							background: #f5f5f5;
							border-radius: 4px;

							.el-textarea {
								margin-bottom: 8px;
							}

							.reply-input-actions {
								display: flex;
								justify-content: flex-end;
								gap: 8px;
							}
						}
					}
				}
			}
		}

		.right-content {
			flex: 1;
			padding: 20px;
			background: #fff;

			.popular-users-card {
				background: #fff;
				border-radius: 8px;
				padding: 16px;
				box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

				.card-title {
					font-size: 16px;
					font-weight: bold;
					color: #333;
				}

				.card-title-row {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 12px;
				}

				.popular-list {
					.popular-item {
						display: flex;
						align-items: center;
						padding: 8px 0;
						cursor: pointer;

						&:hover {
							background: #f5f5f5;
						}

						.popular-avatar {
							width: 40px;
							height: 40px;
							border-radius: 50%;
							margin-right: 12px;
						}

						.popular-info {
							.popular-name {
								font-size: 14px;
								color: #333;
							}

							.popular-followers {
								font-size: 12px;
								color: #999;
							}
						}
					}
				}
			}
		}
	}

	// 音乐选择弹框样式
	.music-selector {
		.selected-music {
			display: flex;
			align-items: center;
			gap: 8px;
			padding: 8px 12px;
			background: #f5f5f5;
			border-radius: 4px;
			color: #333;

			.remove-icon {
				cursor: pointer;
				color: #999;

				&:hover {
					color: #f56c6c;
				}
			}
		}
	}

	.music-type-tabs {
		margin: 16px 0;
	}

	.music-results {
		max-height: 300px;
		overflow-y: auto;

		.empty-music {
			text-align: center;
			color: #999;
			padding: 20px;
		}

		.music-result-item {
			display: flex;
			align-items: center;
			padding: 8px;
			cursor: pointer;

			&:hover {
				background: #f5f5f5;
			}

			.music-cover {
				width: 50px;
				height: 50px;
				border-radius: 4px;
				margin-right: 12px;

				&.default-cover {
					background: #eee;
					display: flex;
					align-items: center;
					justify-content: center;
					color: #999;

					.music-icon {
						font-size: 24px;
					}
				}
			}

			.music-info {
				.music-name {
					font-size: 14px;
					color: #333;
				}

				.music-artist {
					font-size: 12px;
					color: #999;
				}
			}
		}
	}

	.comment-note-content {
		padding: 12px;
		background: #f5f5f5;
		border-radius: 4px;
		margin-bottom: 12px;
		color: #666;
	}
</style>