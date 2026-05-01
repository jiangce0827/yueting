// @/api/frontend/user.js
import request from './request';
import { toRaw } from 'vue';

// 修改用户基本设置
export function apiUserBasicUpdate(form) {
	return request.put('/user/me/basic', form);
}

// 获取用户基本信息
export function apiGetUserBasic() {
	return request.get('/user/me/basic');
}
// 获取用户基本信息
export function apiGetUserBasicByUserId(userId) {
	return request.get(`/user/basic/${userId}`);
}

// 上传头像
export function apiUpdateAvatarUrl(avatarUrl) {
	const params = new URLSearchParams();
	params.append('avatarUrl', avatarUrl);
	return request.put('/user/me/avatar', params,{
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}

//修改用户隐私设置
export function apiUserPrivacyUpdate(form) {
	return request.put('/user/me/privacy', form);
}

export function apiSearchUserByKeyword(form) {
	// 将reactive对象转化为普通对象
	const params = toRaw(form);
	// 创建URLSearchParams实例并将params传递进去
	const searchParams = new URLSearchParams(params);
	return request.get(`/user/search/keyword?${searchParams.toString()}`);
}

// 获取用户播放排行
export function apiGetUserPlayRanking(recentWeek = true) {
	return request.get(`/user/me/play-ranking?recentWeek=${recentWeek}`);
}

// 关注用户
export function apiFollowUser(followingId) {
	return request.post(`/user/me/follow/${followingId}`);
}

// 取消关注用户
export function apiUnfollowUser(followingId) {
	return request.delete(`/user/me/follow/${followingId}`);
}

// 获取关注列表
export function apiGetFollowingList(userId) {
	return request.get(`/user/me/following/${userId}`);
}

// 获取粉丝列表
export function apiGetFollowerList(userId) {
	return request.get(`/user/me/followers/${userId}`);
}

// 检查是否已关注
export function apiIsFollowing(followingId) {
	return request.get(`/user/me/is-following/${followingId}`);
}

// 获取用户播放历史
export function apiGetUserPlayHistory(pageNum = 1, pageSize = 30) {
	return request.get('/user/me/play-history', {
		params: {
			pageNum,
			pageSize,
		},
	});
}
