// @/api/frontend/user.js
import request from './request';

// 获取邮箱验证码
export function apiSendCodeEmail(email) {
	const params = new URLSearchParams();
	params.append('email', email);
	return request.post('/auth/sendCode/email', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
		timeout: 20000,
	});
}
/**
 * 发送邮箱验证码到用户邮箱
 */
export function apiSendCodeEmailToMe() {
	return request.post('/auth/me/sendCode/email',null,{
		timeout: 20000,
	});

}

// 通过邮箱注册账号
export function apiRegisterEmail(form) {
	return request.post('/auth/register/email', form);
}

// 账密登录
export function apiLoginByUsername(username, password) {
	const params = new URLSearchParams();
	params.append('username', username);
	params.append('password', password);
	return request.post('/auth/login/username', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}

//邮箱登录
export function apiLoginByEmail(email, code) {
	const params = new URLSearchParams();
	params.append('email', email);
	params.append('code', code);
	return request.post('/auth/login/email', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}

// 验证邮箱是否正确
export function apiVerifyEmail(email) {
	const params = new URLSearchParams();
	params.append('email', email);
	return request.post('/auth/me/email/verify', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}

// 验证邮箱及验证码是否正确
export function apiVerifyEmailAndCode(email,code) {
	const params = new URLSearchParams();
	params.append('email', email);
	params.append('code', code);
	return request.post('/auth/email/verify', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}

// 换绑邮箱
export function apiUpdateEmail(newEmail, code) {
	const params = new URLSearchParams();
	params.append('newEmail', newEmail);
	params.append('code', code);
	return request.put('/auth/me/email', params, {
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}
// 初始化账户密码
export function apiInitPassword(username, password) {
	const params = new URLSearchParams();
	params.append('username', username);
	params.append('password', password);
	return request.post('/auth/me/password', params);
}

// 禁用账户密码登录
export function apiDisablePassword() {
	return request.put('/auth/me/password/disable');
}

// 启用账户密码登录
export function apiEnablePassword() {
	return request.put('/auth/me/password/enable');
}

// 修改密码
export function apiUpdatePassword(email,code, newPassword) {
	const params = new URLSearchParams();
	params.append('email', email);
	params.append('code', code);
	params.append('newPassword', newPassword);
	return request.put('/auth/me/password', params);
}

// 获取最新token及权限
export function apiGetToken() {
	return request.get('/auth/me/token/userId');
}