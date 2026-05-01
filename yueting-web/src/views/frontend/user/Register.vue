<template>
	<div class="register-container">
		<router-link to="/login" class="login-link">前往登录&gt;</router-link>

		<form @submit.prevent="handleSubmit" class="register-form">
			<!-- 邮箱输入 -->
			<div class="form-group">
				<label>邮箱</label>
				<input v-model="formData.email" type="email" placeholder="请输入邮箱" required />
			</div>

			<!-- 验证码输入 -->
			<div class="form-group">
				<label>验证码</label>
				<div class="code-input-group">
					<input v-model="formData.code" type="text" placeholder="请输入验证码" required />
					<button
						type="button"
						class="get-code-btn"
						:disabled="isCounting"
						@click="getVerificationCode"
					>
						{{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
					</button>
				</div>
			</div>

			<!-- 可跳过提示 -->
			<div class="skip-tip">信息补充（可跳过）</div>

			<!-- 账号名 -->
			<div class="form-group">
				<label>账号名</label>
				<input v-model="formData.username" type="text" placeholder="请输入账号名" />
			</div>

			<!-- 密码 -->
			<div class="form-group">
				<label>密码</label>
				<input v-model="formData.password" type="password" placeholder="请输入密码" />
			</div>

			<button type="submit" class="register-btn">立即注册</button>
		</form>
	</div>
</template>

<script setup>
	import { ref } from 'vue';
	import { useRouter } from 'vue-router';
	import { ElMessage } from 'element-plus';
	import { apiSendCodeEmail, apiRegisterEmail } from '@/api/frontend/auth';

	const router = useRouter();

	// 表单数据
	const formData = ref({
		email: '',
		code: '',
		username: '',
		password: '',
	});

	// 验证码倒计时相关
	const isCounting = ref(false);
	const countdown = ref(0);
	// 邮箱格式校验函数
	const validateEmail = (email) => {
		const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return re.test(email);
	};

	// 获取验证码
	const getVerificationCode = async () => {
		if (!formData.value.email) {
			ElMessage.error('请先输入邮箱');
			return;
		}
		// 邮箱格式校验
		if (!validateEmail(formData.value.email)) {
			ElMessage.error('邮箱格式不正确，请重新输入');
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

			// 调用API
			await apiSendCodeEmail(formData.value.email);
		} catch (error) {
			console.error('发送验证码失败:', error);
			isCounting.value = false;
			countdown.value = 0;
		}
	};

	// 提交注册
	const handleSubmit = async () => {
		if (!formData.value.email || !formData.value.code) {
			ElMessage.error('邮箱和验证码为必填项');
			return;
		}
		try {
			// 注册请求
			const registerRes = await apiRegisterEmail(formData.value);
			if (registerRes.data.code == 1) {
				ElMessage.success('注册成功！');
				router.push('/login');
			} else if (registerRes.data.code == 0) {
				ElMessage.error(registerRes.data.msg);
			}
		} catch (error) {
			console.error('注册失败:', error);
			ElMessage.error('注册失败，请稍后重试');
		}
	};
</script>

<style lang="scss" scoped>
	.register-container {
		max-width: 400px;
		margin: 50px auto;
		padding: 30px;
		background: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
		position: relative;

		.login-link {
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

		.register-form {
			margin-top: 40px;

			.form-group {
				margin-bottom: 20px;

				label {
					display: block;
					margin-bottom: 8px;
					color: #666;
					font-size: 14px;
				}

				input {
					width: 100%;
					padding: 10px;
					border: 1px solid #ddd;
					border-radius: 4px;
					font-size: 14px;

					&:focus {
						border-color: #ec4141;
						outline: none;
					}
				}
			}

			.code-input-group {
				display: flex;
				gap: 10px;

				input {
					flex: 1;
				}

				.get-code-btn {
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

			.skip-tip {
				text-align: center;
				color: #000000;
				margin: 20px 0;
				font-size: 14px;
			}

			.register-btn {
				width: 100%;
				padding: 12px;
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
	}
</style>
