<template>
	<div class="binding-settings">
		<!-- 邮箱绑定 -->
		<div class="setting-item">
			<div class="item-header">
				<el-icon :size="24"><Message /></el-icon>
				<div class="item-info">
					<h3>邮箱绑定</h3>
					<p v-if="userEmail">
						使用邮箱注册：{{ authStore.user.email }}
						<el-link type="primary" @click="showEmailDialog">修改</el-link>
					</p>
				</div>
			</div>
		</div>

		<!-- 账号密码 -->
		<div class="setting-item">
			<div class="item-header">
				<el-icon :size="24"><Lock /></el-icon>
				<div class="item-info">
					<h3>账密登录</h3>
					<p>
						{{
							passwordStatus === 0
								? '未设置账密登录'
								: passwordStatus === 1
								? '已启用账密登录'
								: '已禁用账密登录'
						}}
						<el-switch
							v-model="passwordEnabled"
							:loading="passwordLoading"
							@click="handlePasswordSwitch"
						/>
						<el-link
							v-if="passwordStatus != 0"
							type="primary"
							style="margin-left: 12px"
							@click="showUpdatePasswordDialog"
						>
							修改密码
						</el-link>
					</p>
				</div>
			</div>
		</div>

		<!-- 邮箱修改对话框 -->
		<el-dialog v-model="emailDialogVisible" :title="emailSteps[currentStep].title" width="500px">
			<!-- 步骤1: 验证原邮箱 -->
			<div v-if="currentStep === 0">
				<el-form label-width="120px" @submit.prevent>
					<el-form-item label="完整邮箱">
						<el-input v-model="emailForm.original" placeholder="请输入完整邮箱" />
					</el-form-item>
				</el-form>
			</div>

			<!-- 步骤2: 验证码验证 -->
			<div v-if="currentStep === 1">
				<el-form label-width="120px" @submit.prevent>
					<el-form-item label="验证码">
						<el-input v-model="emailForm.code">
							<template #append>
								<el-button
									:disabled="codeCooldown > 0"
									@click="sendEmailCode(emailForm.original, 'original')"
								>
									{{ codeCooldown > 0 ? `${codeCooldown}s后重试` : '获取验证码' }}
								</el-button>
							</template>
						</el-input>
					</el-form-item>
				</el-form>
			</div>

			<!-- 步骤3: 设置新邮箱 -->
			<div v-if="currentStep === 2">
				<el-form label-width="120px" @submit.prevent>
					<el-form-item label="新邮箱">
						<el-input v-model="emailForm.newEmail" />
					</el-form-item>
					<el-form-item label="验证码">
						<el-input v-model="emailForm.newCode">
							<template #append>
								<el-button
									:disabled="newCodeCooldown > 0"
									@click="sendEmailCode(emailForm.newEmail, 'newOriginal')"
								>
									{{ newCodeCooldown > 0 ? `${newCodeCooldown}s后重试` : '发送验证码' }}
								</el-button>
							</template>
						</el-input>
					</el-form-item>
				</el-form>
			</div>

			<template #footer>
				<el-button @click="emailDialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="isSaving" @click="handleEmailStep">
					{{ currentStep === 2 ? '完成' : '下一步' }}
				</el-button>
			</template>
		</el-dialog>

		<!-- 设置账密对话框 -->
		<el-dialog
			v-model="passwordDialogVisible"
			:title="passwordSteps[passwordStep].title"
			width="500px"
		>
			<!-- 步骤1: 验证邮箱 -->
			<div v-if="passwordStep === 0">
				<el-form label-width="120px" @submit.prevent>
					<el-form-item label="完整邮箱">
						<el-input v-model="passwordForm.email" />
					</el-form-item>
				</el-form>
			</div>

			<!-- 步骤2: 验证码验证 -->
			<div v-if="passwordStep === 1">
				<el-form label-width="120px" @submit.prevent>
					<el-form-item label="验证码">
						<el-input v-model="passwordForm.code">
							<template #append>
								<el-button
									:disabled="passwordCodeCooldown > 0"
									@click="sendEmailCode(passwordForm.email, 'password')"
								>
									{{ passwordCodeCooldown > 0 ? `${passwordCodeCooldown}s后重试` : '发送验证码' }}
								</el-button>
							</template>
						</el-input>
					</el-form-item>
				</el-form>
			</div>

			<!-- 步骤3: 设置账号密码 -->
			<div v-if="passwordStep === 2">
				<el-form label-width="120px" @submit.prevent>
					<el-form-item label="账号" v-if="passwordStatus === 0">
						<el-input v-model="passwordForm.username" />
						<div class="el-form-item__tip">账号设置后不可修改</div>
					</el-form-item>
					<el-form-item label="密码">
						<el-input v-model="passwordForm.password" type="password" show-password />
					</el-form-item>
				</el-form>
			</div>

			<template #footer>
				<el-button @click="passwordDialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="isSaving" @click="handlePasswordStep">
					{{ passwordStep === 2 ? '保存' : '下一步' }}
				</el-button>
			</template>
		</el-dialog>
		<!-- 修改密码对话框 -->
		<el-dialog v-model="updatePwdDialogVisible" title="修改密码" width="500px">
			<el-form label-width="120px" @submit.prevent>
				<!-- 验证邮箱 -->
				<el-form-item label="完整邮箱">
					<el-input v-model="updatePwdForm.email" />
				</el-form-item>
				<el-form-item label="验证码">
					<el-input v-model="updatePwdForm.code">
						<template #append>
							<el-button
								:disabled="updatePwdCooldown > 0"
								@click="sendEmailCode(updatePwdForm.email, 'updatePwd')"
							>
								{{ updatePwdCooldown > 0 ? `${updatePwdCooldown}s后重试` : '发送验证码' }}
							</el-button>
						</template>
					</el-input>
				</el-form-item>
				<el-form-item label="新密码">
					<el-input v-model="updatePwdForm.newPassword" type="password" show-password />
				</el-form-item>
			</el-form>

			<template #footer>
				<el-button @click="updatePwdDialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="isSaving" @click="handleUpdatePassword">
					保存
				</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, computed, reactive } from 'vue';
	import { Message, Lock } from '@element-plus/icons-vue';
	import { ElMessage } from 'element-plus';
	import {
		apiSendCodeEmail,
		apiVerifyEmail,
		apiVerifyEmailAndCode,
		apiUpdateEmail,
		apiInitPassword,
		apiDisablePassword,
		apiEnablePassword,
		apiUpdatePassword,
	} from '@/api/frontend/auth';
	import { useAuthStore } from '@/stores/frontend/auth';

	const authStore = useAuthStore();
	// ==================== 邮箱绑定功能 ====================
	const userEmail = ref(authStore.user.email);
	const emailDialogVisible = ref(false);
	const currentStep = ref(0);
	const codeCooldown = ref(0); // 原邮箱验证码冷却
	const newCodeCooldown = ref(0); // 新邮箱验证码冷却
	const isSaving = ref(false);

	const emailSteps = [{ title: '验证原邮箱' }, { title: '输入验证码' }, { title: '设置新邮箱' }];

	const emailForm = reactive({
		original: '',
		code: '',
		newEmail: '',
		newCode: '',
	});

	// ==================== 账密功能 ====================

	// 密码设置逻辑
	const passwordCodeCooldown = ref(0); //验证码冷却时间
	const passwordStatus = ref(authStore.user.passwordStatus); // 0-未设置 1-启用 2-禁用
	// 密码开关计算属性
	const passwordEnabled = computed({
		get: () => passwordStatus.value === 1,
		set: () => {},
	});

	// ================ 修改密码功能 ====================
	const updatePwdDialogVisible = ref(false);
	const updatePwdCooldown = ref(0);
	const updatePwdForm = reactive({
		email: '',
		code: '',
		newPassword: '',
	});

	// ================ 初始化账密功能 ====================
	const passwordLoading = ref(false);
	const passwordDialogVisible = ref(false);
	const passwordStep = ref(0);
	const passwordSteps = [{ title: '验证邮箱' }, { title: '验证验证码' }, { title: '设置账号密码' }];
	const passwordForm = reactive({
		email: '',
		code: '',
		username: '',
		password: '',
	});

	// ====================== 邮箱绑定逻辑 ======================
	// 展示邮箱表单
	const showEmailDialog = () => {
		emailDialogVisible.value = true;
		currentStep.value = 0;
		Object.keys(emailForm).forEach((k) => (emailForm[k] = ''));
	};
	// 发送邮箱验证码
	const sendEmailCode = async (email, type = 'original') => {
		try {
			// 验证邮箱格式（仅对新邮箱）
			if (!email) {
				ElMessage.error('请先输入新邮箱地址');
				return;
			}
			if (!isValidEmail(email)) {
				ElMessage.error('邮箱格式不正确，请检查输入');
				return;
			}

			startCooldown(type);
			await apiSendCodeEmail(email);
		} catch (error) {
			ElMessage.error(error.response?.data?.msg || '发送验证码失败');
		}
	};
	// 绑定邮箱流程
	const handleEmailStep = async () => {
		try {
			isSaving.value = true;
			switch (currentStep.value) {
				case 0:
					const verifyEmailRes = await apiVerifyEmail(emailForm.original);
					if (verifyEmailRes.data.code == 1) {
						currentStep.value++;

						// 验证通过后自动发送验证码
						startCooldown('original');

						await apiSendCodeEmail(emailForm.original);
					} else {
						ElMessage.error(verifyEmailRes.data.msg);
					}
					break;
				case 1:
					const verifyEmailAndCodeRes = await apiVerifyEmailAndCode(
						emailForm.original,
						emailForm.code
					);
					if (verifyEmailAndCodeRes.data.code == 1) {
						currentStep.value++;
					} else {
						ElMessage.error(verifyEmailAndCodeRes.data.msg);
					}
					break;
				case 2:
					const updateEmailRes = await apiUpdateEmail(emailForm.newEmail, emailForm.newCode);
					if (updateEmailRes.data.code == 1) {
						userEmail.value = emailForm.newEmail;
						authStore.updateUser(updateEmailRes.data.data);
						emailDialogVisible.value = false;
						ElMessage.success('邮箱修改成功');
					} else {
						ElMessage.error(updateEmailRes.data.msg);
					}
					break;
			}
		} catch (error) {
			ElMessage.error(error.message);
		} finally {
			isSaving.value = false;
		}
	};

	// ################### 密码设置逻辑 #######################
	// 处理密码开关状态变化
	const handlePasswordSwitch = async () => {
		try {
			passwordLoading.value = true;
			// 关闭状态直接更新
			if (passwordStatus.value === 0) {
				passwordDialogVisible.value = true;
				passwordStep.value = 0;
			} else if (passwordStatus.value === 1) {
				const disableRes = await apiDisablePassword();
				if (disableRes.data.code == 1) {
					authStore.updateUser(disableRes.data.data);
					passwordStatus.value = disableRes.data.data.passwordStatus;
					ElMessage.success('已关闭账密登录');
				} else {
					ElMessage.success(disableRes.data.msg);
				}
			}
			// 启用时需要验证
			else if (passwordStatus.value === 2) {
				const disableRes = await apiEnablePassword();
				if (disableRes.data.code == 1) {
					authStore.updateUser(disableRes.data.data);
					passwordStatus.value = disableRes.data.data.passwordStatus;
					ElMessage.success('已开启账密登录');
				} else {
					ElMessage.success(disableRes.data.msg);
				}
			}
		} catch (error) {
			ElMessage.error('请稍后再试');
		} finally {
			passwordLoading.value = false;
		}
	};

	// 初始化账密处理步骤
	const handlePasswordStep = async () => {
		try {
			isSaving.value = true;
			switch (passwordStep.value) {
				case 0: // 验证邮箱
					const verifyRes = await apiVerifyEmail(passwordForm.email);
					if (verifyRes.data.code === 1) {
						// 验证通过后发送验证码
						startCooldown('password');
						passwordStep.value++;
						await apiSendCodeEmail(passwordForm.email);
					} else {
						ElMessage.error(verifyRes.data.msg);
					}
					break;

				case 1: // 验证验证码
					const codeVerifyRes = await apiVerifyEmailAndCode(passwordForm.email, passwordForm.code);
					if (codeVerifyRes.data.code === 1) {
						passwordStep.value++;
					} else {
						ElMessage.error(codeVerifyRes.data.msg);
					}
					break;

				case 2: // 设置账户密码
					const setRes = await apiInitPassword(passwordForm.username, passwordForm.password);
					if (setRes.data.code === 1) {
						passwordStatus.value = 1;
						passwordDialogVisible.value = false;
						authStore.updateUser(setRes.data.data);
						ElMessage.success('设置成功');
					} else {
						ElMessage.error(setRes.data.msg);
					}
					break;
			}
		} catch (error) {
			ElMessage.error(error.response?.data?.msg || '操作失败');
		} finally {
			isSaving.value = false;
		}
	};
	// 展示修改密码表单
	const showUpdatePasswordDialog = () => {
		updatePwdDialogVisible.value = true;
		Object.keys(updatePwdForm).forEach((k) => (updatePwdForm[k] = ''));
	};

	// 修改密码逻辑
	const handleUpdatePassword = async () => {
		try {
			isSaving.value = true;
			const res = await apiUpdatePassword(
				updatePwdForm.email,
				updatePwdForm.code,
				updatePwdForm.newPassword
			);
			if (res.data.code == 1) {
				authStore.updateUser(res.data.data);
				updatePwdDialogVisible.value = false;
				ElMessage.success('密码修改成功');
			} else {
				ElMessage.error(res.data.msg);
			}
		} catch (error) {
			ElMessage.error(error.response?.data?.msg || '密码修改失败');
		} finally {
			isSaving.value = false;
		}
	};

	// 冷却时间管理
	const startCooldown = (type) => {
		let target;
		switch (type) {
			case 'password':
				target = passwordCodeCooldown;
				break;
			case 'updatePwd':
				target = updatePwdCooldown;
				break;
			case 'original':
				target = codeCooldown;
				break;
			case 'newOriginal':
				target = newCodeCooldown;
				break;
		}

		target.value = 60;
		const timer = setInterval(() => {
			target.value--;
			if (target.value <= 0) clearInterval(timer);
		}, 1000);
	};

	// 邮箱格式校验函数
	const isValidEmail = (email) => {
		const pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
		return pattern.test(email);
	};
</script>

<style lang="scss" scoped>
	.binding-settings {
		padding: 20px;
	}

	.setting-item {
		padding: 20px 0;
		border-bottom: 1px solid #eee;
	}

	.item-header {
		display: flex;
		align-items: center;
		gap: 16px;
	}

	.item-info {
		flex: 1;

		h3 {
			margin: 0 0 8px 0;
			font-size: 16px;
		}

		p {
			margin: 0;
			color: #666;
		}
	}

	.el-link {
		margin-left: 12px;
	}
</style>
