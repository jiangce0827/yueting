<!-- @/views/frontend/login.vue -->
<template>
	<div class="login-container">
		<div class="login-box">
			<router-link to="/register" class="register-link">前往注册&gt;</router-link>

			<div class="tabs">
				<button :class="{ active: activeTab === 'username' }" @click="activeTab = 'username'">
					账密登录
				</button>
				<button :class="{ active: activeTab === 'email' }" @click="activeTab = 'email'">
					邮箱登录
				</button>
			</div>

			<transition name="fade" mode="out-in">
				<div v-if="activeTab === 'username'" key="username" class="form">
					<div class="form-item">
						<input v-model="loginForm.username" type="text" placeholder="请输入账号" />
					</div>
					<div class="form-item">
						<input v-model="loginForm.password" type="password" placeholder="请输入密码" />
					</div>
				</div>

				<div v-else key="email" class="form">
					<div class="form-item">
						<input v-model="loginForm.email" type="email" placeholder="请输入邮箱" />
					</div>
					<div class="form-item code-input">
						<input v-model="loginForm.code" type="text" placeholder="请输入验证码" />
						<button :disabled="isCounting" @click="sendCode" class="code-btn">
							{{ isCounting ? `${countdown}s后重试` : '获取验证码' }}
						</button>
					</div>
				</div>
			</transition>

			<button class="login-btn" @click="handleLogin">立即登录</button>
		</div>
	</div>
</template>

<script setup>
	import { ref } from 'vue';
	import { useRouter } from 'vue-router';
	import { ElMessage } from 'element-plus';
	import { apiLoginByUsername, apiLoginByEmail, apiSendCodeEmail } from '@/api/frontend/auth';
	import { useAuthStore } from '@/stores/frontend/auth';
	const router = useRouter();
	const authStore = useAuthStore();
	const activeTab = ref('username'); // username / email
	const loginForm = ref({
		username: '',
		password: '',
		email: '',
		code: '',
	});

	// 验证码倒计时
	const countdown = ref(0);
	const isCounting = ref(false);

	// 发送验证码
	const sendCode = async () => {
		if (!validateEmail(loginForm.value.email)) {
			ElMessage.error('请输入正确的邮箱格式');
			return;
		}

		try {
			isCounting.value = true;
			countdown.value = 60;
			const timer = setInterval(() => {
				countdown.value--;
				if (countdown.value <= 0) {
					clearInterval(timer);
					isCounting.value = false;
				}
			}, 1000);

			// 调用发送验证码接口
			await apiSendCodeEmail(loginForm.value.email);
		} catch (error) {
			console.error('发送失败:', error);
			isCounting.value = false;
			countdown.value = 0;
		}
	};

	// 邮箱格式验证
	const validateEmail = (email) => {
		const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return re.test(email);
	};

	// 提交登录
	const handleLogin = async () => {
		try {
			let response;
			if (activeTab.value === 'username') {
				if (!loginForm.value.username || !loginForm.value.password) {
					ElMessage.error('请输入完整账号信息');
					return;
				}
				// 账号密码登录
				response = await apiLoginByUsername(loginForm.value.username, loginForm.value.password);
			} else {
				if (!validateEmail(loginForm.value.email) || !loginForm.value.code) {
					ElMessage.error('请输入正确的邮箱和验证码');
					return;
				}
				// 调用验证码登录接口
				response = await apiLoginByEmail(loginForm.value.email, loginForm.value.code);
			}
			if (response.data.code === 1) {
				await authStore.login(response.data.data);
				const redirect = router.currentRoute.value.query.redirect || '/';
				router.push(redirect);
			} else {
				ElMessage.error(response.data.msg || '请稍后再试');
			}
		} catch (error) {
			console.error('登录失败:', error);
			ElMessage.error(error.response?.data?.msg || '请稍后再试');
		}
	};
</script>

<style lang="scss" scoped>
	.login-container {
		display: flex;
		justify-content: center;
		align-items: center;
		min-height: 100vh;
		background: #f0f2f5;
	}

	.login-box {
		position: relative;;
		width: 400px;
		padding: 40px;
		background: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
		.register-link {
			position: absolute;
			top: 20px;
			right: 20px;
			color: #ec4141;
			text-decoration: none;
			font-size: 14px;

			&:hover {
				text-decoration: underline;
			}
		}
		.tabs {
			display: flex;
			margin-bottom: 30px;
			border-bottom: 1px solid #eee;

			button {
				flex: 1;
				padding: 12px;
				background: none;
				border: none;
				cursor: pointer;
				font-size: 16px;
				color: #666;
				transition: all 0.3s;

				&.active {
					color: #ec4141;
					border-bottom: 2px solid #ec4141;
				}

				&:hover {
					color: #d43838;
				}
			}
		}

		.form {
			.form-item {
				margin-bottom: 20px;

				input {
					width: 100%;
					padding: 12px;
					border: 1px solid #ddd;
					border-radius: 4px;
					font-size: 14px;

					&:focus {
						border-color: #ec4141;
						outline: none;
					}
				}

				&.code-input {
					display: flex;
					gap: 10px;

					.code-btn {
						flex-shrink: 0;
						padding: 0 15px;
						background: #ec4141;
						color: white;
						border: none;
						border-radius: 4px;
						cursor: pointer;
						transition: opacity 0.3s;

						&:disabled {
							background: #ccc;
							cursor: not-allowed;
						}
					}
				}
			}
		}

		.login-btn {
			width: 100%;
			padding: 12px;
			margin-top: 20px;
			background: #ec4141;
			color: white;
			border: none;
			border-radius: 4px;
			font-size: 16px;
			cursor: pointer;
			transition: background 0.3s;

			&:hover {
				background: #d43838;
			}
		}
	}

	.fade-enter-active,
	.fade-leave-active {
		transition: opacity 0.3s, transform 0.3s;
	}
	.fade-enter-from {
		opacity: 0;
		transform: translateX(-20px);
	}
	.fade-leave-to {
		opacity: 0;
		transform: translateX(20px);
	}
</style>
