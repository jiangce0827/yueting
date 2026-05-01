<!-- @/components/frontend/CommentSection.vue -->
<template>
	<div class="comment-section">
		<!-- 评论标题 -->
		<div class="comment-header">
			<h3 class="comment-title">评论</h3>
			<span class="comment-count">({{ comments.length }})</span>
		</div>

		<!-- 评论输入框 -->
		<div class="comment-input-wrapper" v-if="authStore.isLoggedIn">
			<img :src="authStore.user?.avatarUrl || defaultAvatar" class="input-avatar" />
			<div class="input-area">
				<el-input
					v-model="commentContent"
					type="textarea"
					:rows="2"
					placeholder="写下你的评论..."
					maxlength="200"
					show-word-limit
				/>
				<el-button type="primary" size="small" class="submit-btn" @click="submitComment">发表评论</el-button>
			</div>
		</div>
		<div v-else class="login-tip">
			<router-link to="/login">登录</router-link> 后发表评论
		</div>

		<!-- 评论列表 -->
		<div class="comment-list" v-if="comments.length > 0">
			<div v-for="comment in comments" :key="comment.commentId" class="comment-item">
				<img :src="comment.avatarUrl || defaultAvatar" class="comment-avatar" />
				<div class="comment-body">
					<div class="comment-header">
						<span class="comment-user">{{ comment.nickname }}</span>
						<span v-if="comment.replyToNickname" class="reply-tag">回复 {{ comment.replyToNickname }}</span>
						<span class="comment-action">评论</span>
						<span class="comment-time">{{ formatRelativeTime(comment.createdAt) }}</span>
						<el-dropdown
							v-if="authStore.user && comment.userId == authStore.user.userId"
							trigger="click"
							@command="(cmd) => handleCommentCommand(cmd, comment)"
						>
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
						<div class="quote-text">{{ comment.quoteNickname }}: {{ comment.quoteContent }}</div>
					</div>

					<!-- 回复输入框 -->
					<div v-if="replyingToId === comment.commentId" class="reply-input-wrapper">
						<el-input
							v-model="replyContent"
							type="textarea"
							:rows="1"
							:placeholder="`回复 ${comment.nickname}...`"
							maxlength="200"
							show-word-limit
						/>
						<div class="reply-input-actions">
							<el-button size="small" @click="cancelReply">取消</el-button>
							<el-button type="primary" size="small" @click="submitReply">发送</el-button>
						</div>
					</div>
				</div>
				<div class="comment-actions">
					<span class="like-btn" :class="{ liked: comment.liked }" @click="handleLike(comment)" v-if="authStore.isLoggedIn">
						<el-icon size="14"><Star /></el-icon>
						<span class="like-count">{{ comment.likeCount || 0 }}</span>
					</span>
					<span class="reply-btn" @click="openReply(comment)" v-if="authStore.isLoggedIn">回复</span>
				</div>
			</div>
		</div>
		<div v-else class="comment-empty">暂无评论，快来抢沙发</div>
	</div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { MoreFilled, Star } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/frontend/auth';
import { apiCreateComment, apiDeleteComment, apiGetComments, apiLikeComment, apiUnlikeComment } from '@/api/frontend/comment';
import { formatRelativeTime } from '@/utils/format.js';

const props = defineProps({
	targetType: {
		type: Number,
		required: true,
	},
	targetId: {
		type: [String, Number],
		required: true,
	},
});

const authStore = useAuthStore();
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/4ea1f28e1d89defd2b7bdbDb3b1acpng.png';

const comments = ref([]);
const commentContent = ref('');
const replyingToId = ref(null);
const replyContent = ref('');
const currentReplyToUserId = ref(null);
const currentParentId = ref(null);

// 加载评论列表
const loadComments = async () => {
	if (!props.targetId) return;
	try {
		const res = await apiGetComments(props.targetType, props.targetId);
		if (res.data.code === 1) {
			comments.value = res.data.data || [];
		}
	} catch (error) {
		console.error('获取评论列表失败:', error);
	}
};

// 发表评论
const submitComment = async () => {
	if (!commentContent.value.trim()) {
		ElMessage.warning('请输入评论内容');
		return;
	}
	try {
		await apiCreateComment({
			targetType: props.targetType,
			targetId: props.targetId,
			content: commentContent.value,
		});
		ElMessage.success('评论成功');
		commentContent.value = '';
		loadComments();
	} catch (error) {
		ElMessage.error('评论失败');
	}
};

// 打开回复输入框
const openReply = (comment) => {
	replyContent.value = '';
	currentParentId.value = comment.commentId;
	currentReplyToUserId.value = comment.userId;
	replyingToId.value = comment.commentId;
};

// 取消回复
const cancelReply = () => {
	replyingToId.value = null;
	replyContent.value = '';
	currentParentId.value = null;
	currentReplyToUserId.value = null;
};

// 提交回复
const submitReply = async () => {
	if (!replyContent.value.trim()) {
		ElMessage.warning('请输入回复内容');
		return;
	}
	try {
		await apiCreateComment({
			targetType: props.targetType,
			targetId: props.targetId,
			content: replyContent.value,
			parentId: currentParentId.value,
			replyToUserId: currentReplyToUserId.value,
		});
		ElMessage.success('回复成功');
		cancelReply();
		loadComments();
	} catch (error) {
		ElMessage.error('回复失败');
	}
};

// 处理点赞
const handleLike = async (comment) => {
	if (!authStore.isLoggedIn) {
		ElMessage.warning('请先登录');
		return;
	}
	try {
		if (comment.liked) {
			await apiUnlikeComment(comment.commentId);
			ElMessage.success('取消点赞成功');
		} else {
			await apiLikeComment(comment.commentId);
			ElMessage.success('点赞成功');
		}
		loadComments();
	} catch (error) {
		ElMessage.error(error.response?.data?.msg || (comment.liked ? '取消点赞失败' : '点赞失败'));
	}
};

// 处理评论操作命令
const handleCommentCommand = async (command, comment) => {
	if (command === 'delete') {
		try {
			await apiDeleteComment(comment.commentId);
			ElMessage.success('删除成功');
			loadComments();
		} catch (error) {
			ElMessage.error('删除失败');
		}
	}
};

onMounted(() => {
	loadComments();
});

// 监听 targetId 变化，当从空变为有值时重新加载评论
watch(() => props.targetId, (newVal, oldVal) => {
	if (newVal && !oldVal) {
		loadComments();
	}
});

// 暴露方法供父组件调用
defineExpose({
	loadComments,
});
</script>

<style lang="scss" scoped>
.comment-section {
	margin-top: 30px;
	padding-top: 20px;
	border-top: 1px solid #eee;

	.comment-header {
		display: flex;
		align-items: center;
		margin-bottom: 20px;

		.comment-title {
			font-size: 18px;
			font-weight: bold;
			margin: 0;
		}

		.comment-count {
			font-size: 14px;
			color: #999;
			margin-left: 8px;
		}
	}

	.comment-input-wrapper {
		display: flex;
		gap: 12px;
		margin-bottom: 20px;

		.input-avatar {
			width: 40px;
			height: 40px;
			border-radius: 50%;
			flex-shrink: 0;
		}

		.input-area {
			flex: 1;
			display: flex;
			flex-direction: column;
			gap: 8px;

			.submit-btn {
				align-self: flex-end;
			}
		}
	}

	.login-tip {
		text-align: center;
		padding: 20px;
		color: #999;
		margin-bottom: 20px;

		a {
			color: #409eff;
			text-decoration: none;

			&:hover {
				text-decoration: underline;
			}
		}
	}

	.comment-list {
		.comment-item {
			display: flex;
			gap: 12px;
			margin-bottom: 16px;
			padding-bottom: 16px;
			border-bottom: 1px solid #f0f0f0;

			&:last-child {
				border-bottom: none;
			}

			.comment-avatar {
				width: 40px;
				height: 40px;
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
					margin-bottom: 6px;

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
					margin-bottom: 6px;
				}

				.quote-content {
					background: #f5f5f5;
					padding: 6px 10px;
					border-left: 3px solid #409eff;
					border-radius: 2px;
					font-size: 13px;
					color: #666;
					line-height: 1.5;

					&.deleted {
						border-left-color: #999;
						color: #999;
					}

					.quote-text {
						overflow: hidden;
						text-overflow: ellipsis;
						display: -webkit-box;
						-webkit-line-clamp: 2;
						-webkit-box-orient: vertical;
					}
				}

				.reply-input-wrapper {
					margin-top: 10px;
					padding: 10px 12px;
					background: #f5f5f5;
					border-radius: 4px;

					.reply-input-actions {
						display: flex;
						justify-content: flex-end;
						gap: 8px;
						margin-top: 8px;
					}
				}
			}

			.comment-actions {
				display: flex;
				flex-direction: column;
				align-items: center;
				gap: 8px;
				flex-shrink: 0;

				.like-btn {
					display: flex;
					align-items: center;
					gap: 2px;
					color: #999;
					font-size: 12px;
					cursor: pointer;
					transition: color 0.2s;

					&:hover {
						color: #ec4141;
					}

					&.liked {
						color: #ec4141;
					}

					.like-count {
						font-size: 12px;
					}
				}

				.reply-btn {
					color: #999;
					font-size: 12px;
					cursor: pointer;

					&:hover {
						color: #409eff;
					}
				}
			}
		}
	}

	.comment-empty {
		text-align: center;
		color: #999;
		padding: 40px 0;
		font-size: 14px;
	}
}
</style>
