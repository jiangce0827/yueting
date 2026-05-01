<!-- @/views/admin/AdminLogin.vue -->
<template>
	<div class="login-container">
		<div class="login-box">
			<h2>悦听后台管理系统登录</h2>
			<form @submit.prevent>
				<input v-model="form.username" placeholder="管理员账号" />
				<input v-model="form.password" type="password" placeholder="密码" />
				<button @click="handleSubmit">登录</button>
			</form>
		</div>
	</div>
</template>

<script setup>
	import { ref } from 'vue';
	import { ElMessage } from 'element-plus';
	import { useRouter } from 'vue-router';
	import { useAdminStore } from '../../stores/admin/admin';
	import { apiLogin } from '../../api/admin/employee';
	const adminStore = useAdminStore();
	const router = useRouter();
	const form = ref({
		username: '',
		password: '',
	});

	const handleSubmit = async () => {
		// 调用登录API
		const res = await apiLogin(form.value.username, form.value.password);
		if (res.data.code == 1) {
			adminStore.login(res.data.data);
			router.push('/admin');
		} else {
			ElMessage.error(res.data.msg || '请稍后再试');
		}
	};
</script>

<style lang="scss" scoped>
	.login-container {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 100vh;
		background: #f0f2f5;
	}

	.login-box {
		width: 400px;
		padding: 40px;
		background: white;
		border-radius: 8px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

		h2 {
			margin-bottom: 24px;
			text-align: center;
		}

		input {
			width: 100%;
			margin-bottom: 16px;
			padding: 8px 12px;
			border: 1px solid #ddd;
			border-radius: 4px;
		}

		button {
			width: 100%;
			padding: 12px;
			background: #1890ff;
			color: white;
			border: none;
			border-radius: 4px;
			cursor: pointer;
		}
	}
</style>
