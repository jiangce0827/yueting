// @/api/frontend/ai.js
import request from './request';

export function apiSendMessage(prompt, chatId) {
	return request.post('/ai/chat', null, {
		params: { prompt, chatId },
		responseType: 'text',
	});
}

export function apiGetHistory(chatId) {
	return request.get('/ai/history', {
		params: { chatId },
	});
}

export function apiGetChats() {
	return request.get('/ai/chats');
}

export function apiClearHistory(chatId) {
	return request.delete('/ai/history', {
		params: { chatId },
	});
}
