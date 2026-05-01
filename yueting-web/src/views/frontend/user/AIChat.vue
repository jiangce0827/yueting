<template>
	<div class="ai-chat-page">
		<div class="chat-container">
			<div class="chat-header">
				<div class="header-spacer" />
				<div class="header-title">
					<h2>悦听 AI 🎵</h2>
					<p class="sub-title">你的专属音乐智能助手</p>
				</div>
				<div class="header-actions">
					<el-button type="danger" text size="small" @click="clearHistory">
						清除记录
					</el-button>
				</div>
			</div>

			<el-scrollbar ref="scrollbarRef" class="chat-messages">
				<div class="message-list">
					<div
						v-for="(msg, index) in messages"
						:key="index"
						class="message-item"
						:class="msg.role"
					>
						<div class="message-bubble">
							<div class="message-content" v-html="formatContent(msg.content)" />
						</div>
					</div>
					<div v-if="isTyping" class="message-item assistant">
						<div class="message-bubble typing">
							<span class="dot" />
							<span class="dot" />
							<span class="dot" />
						</div>
					</div>
				</div>
			</el-scrollbar>

			<div class="chat-input-area">
				<el-input
					v-model="inputMessage"
					:rows="2"
					type="textarea"
					placeholder="想听什么？跟我说说吧~"
					resize="none"
					@keydown.enter.prevent="sendMessage"
				/>
				<el-button
					type="primary"
					:disabled="!inputMessage.trim() || isSending"
					class="send-btn"
					@click="sendMessage"
				>
					发送
				</el-button>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue';
import { useAuthStore } from '@/stores/frontend/auth';
import { ElMessage, ElMessageBox } from 'element-plus';
import { apiGetHistory, apiClearHistory } from '@/api/frontend/ai';

const authStore = useAuthStore();
const WELCOME_MESSAGE = {
	role: 'assistant',
	content: '你好呀！我是小悦🎵 你的专属音乐智能助手~\n我可以帮你搜索艺人、歌曲、专辑和歌单，也可以根据你的听歌记录推荐好歌哦！今天想听什么呢？',
};
const messages = ref([WELCOME_MESSAGE]);
const inputMessage = ref('');
const isSending = ref(false);
const isTyping = ref(false);
const scrollbarRef = ref(null);

const chatId = (authStore.user?.userId || 'guest') + '_ai';

const scrollToBottom = async () => {
	await nextTick();
	const wrap = scrollbarRef.value?.$el?.querySelector('.el-scrollbar__wrap');
	if (wrap) {
		wrap.scrollTop = wrap.scrollHeight;
	}
};

const formatContent = (content) => {
	if (!content) return '';
	// 将 Markdown 链接 [text](url) 转换为可点击的 <a> 标签
	let html = content.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>');
	return html.replace(/\n/g, '<br/>');
};

const loadHistory = async () => {
	if (!authStore.isLoggedIn) return;
	try {
		const res = await apiGetHistory(chatId);
		if (res.data.code === 1 && res.data.data && res.data.data.length > 0) {
			messages.value = res.data.data.map((item) => ({
				role: item.role,
				content: item.content,
			}));
		} else {
			messages.value = [WELCOME_MESSAGE];
		}
	} catch (e) {
		ElMessage.error('加载历史记录失败');
		console.error(e);
	}
	await scrollToBottom();
};

const clearHistory = async () => {
	if (!authStore.isLoggedIn) return;
	try {
		await ElMessageBox.confirm('确定要清除当前会话的所有历史记录吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		});
		const res = await apiClearHistory(chatId);
		if (res.data.code === 1) {
			ElMessage.success('清除成功');
			messages.value = [WELCOME_MESSAGE];
		} else {
			ElMessage.error(res.data.msg || '清除失败');
		}
	} catch (error) {
		if (error !== 'cancel') {
			ElMessage.error('清除失败');
			console.error(error);
		}
	}
};

const sendMessage = async () => {
	const text = inputMessage.value.trim();
	if (!text || isSending.value) return;

	if (!authStore.isLoggedIn) {
		ElMessage.warning('请先登录后再使用悦听 AI~');
		return;
	}

	messages.value.push({ role: 'user', content: text });
	inputMessage.value = '';
	isSending.value = true;
	isTyping.value = true;
	await scrollToBottom();

	try {
		const response = await fetch(
			`/api/web/ai/chat?prompt=${encodeURIComponent(text)}&chatId=${chatId}`,
			{
				method: 'POST',
				headers: {
					Authorization: `Bearer ${authStore.token}`,
				},
			}
		);

		if (!response.ok) {
			throw new Error('请求失败：' + response.status);
		}

		const reader = response.body.getReader();
		const decoder = new TextDecoder('utf-8');
		let aiReply = '';

		messages.value.push({ role: 'assistant', content: '' });
		isTyping.value = false;
		await scrollToBottom();

		while (true) {
			const { done, value } = await reader.read();
			if (done) break;
			const chunk = decoder.decode(value, { stream: true });
			aiReply += chunk;
			messages.value[messages.value.length - 1].content = aiReply;
			await scrollToBottom();
		}
	} catch (error) {
		isTyping.value = false;
		messages.value.push({
			role: 'assistant',
			content: '抱歉，小悦现在有点忙，请稍后再试~',
		});
		ElMessage.error(error.message || '发送失败');
	} finally {
		isSending.value = false;
		isTyping.value = false;
		await scrollToBottom();
	}
};

onMounted(async () => {
	await loadHistory();
});
</script>

<style lang="scss" scoped>
.ai-chat-page {
	min-height: calc(100vh - 70px);
	background: #f5f5f5;
	padding: 20px 0;

	.chat-container {
		width: 1000px;
		margin: 0 auto;
		height: calc(100vh - 110px);
		background: #fff;
		border-radius: 12px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
		display: flex;
		flex-direction: column;
		overflow: hidden;
	}

	.chat-header {
		padding: 20px 30px;
		border-bottom: 1px solid #eee;
		display: flex;
		align-items: center;
		justify-content: space-between;

		.header-title {
			flex: 1;
			text-align: center;
		}

		.header-spacer {
			width: 60px;
		}

		.header-actions {
			width: 60px;
			text-align: right;
		}

		h2 {
			margin: 0;
			font-size: 22px;
			color: #333;
		}

		.sub-title {
			margin: 6px 0 0;
			font-size: 14px;
			color: #999;
		}
	}

	.chat-messages {
		flex: 1;
		padding: 20px 30px;
		background: #fafafa;

		.message-list {
			display: flex;
			flex-direction: column;
			gap: 16px;
		}

		.message-item {
			display: flex;

			&.user {
				justify-content: flex-end;

				.message-bubble {
					background: #409eff;
					color: #fff;
					border-bottom-right-radius: 4px;
				}
			}

			&.assistant {
				justify-content: flex-start;

				.message-bubble {
					background: #fff;
					color: #333;
					border: 1px solid #e8e8e8;
					border-bottom-left-radius: 4px;
				}
			}
		}

		.message-bubble {
			max-width: 70%;
			padding: 12px 16px;
			border-radius: 16px;
			font-size: 14px;
			line-height: 1.6;
			word-break: break-word;

			.message-content a {
				color: #409eff;
				text-decoration: underline;
				cursor: pointer;

				&:hover {
					color: #66b1ff;
				}
			}

			&.typing {
				display: flex;
				align-items: center;
				gap: 6px;
				padding: 16px 20px;

				.dot {
					width: 8px;
					height: 8px;
					background: #ccc;
					border-radius: 50%;
					animation: bounce 1.4s infinite ease-in-out both;

					&:nth-child(1) {
						animation-delay: -0.32s;
					}
					&:nth-child(2) {
						animation-delay: -0.16s;
					}
				}
			}
		}
	}

	.chat-input-area {
		padding: 16px 30px;
		border-top: 1px solid #eee;
		background: #fff;
		display: flex;
		align-items: flex-end;
		gap: 12px;

		:deep(.el-textarea__inner) {
			background: #f5f5f5;
			border: none;
			border-radius: 8px;
			padding: 10px 14px;
		}

		.send-btn {
			height: 44px;
			padding: 0 24px;
			border-radius: 8px;
		}
	}
}

@keyframes bounce {
	0%,
	80%,
	100% {
		transform: scale(0.6);
	}
	40% {
		transform: scale(1);
	}
}
</style>
