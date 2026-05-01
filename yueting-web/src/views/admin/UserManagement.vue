<!-- @/views/admin/UserManagement.vue -->

<template>
	<el-form :model="pageForm" @submit.prevent>
		<el-row>
			<el-form-item label="昵称" prop="name">
				<el-input
					v-model="pageForm.nickname"
					placeholder="输入昵称"
					clearable
					@keyup.enter="getUserList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
			<el-form-item label="电话" prop="phone" style="margin-left: 40px">
				<el-input
					v-model="pageForm.phone"
					placeholder="输入电话"
					clearable
					@keyup.enter="getUserList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
			<el-form-item label="邮箱" prop="email" style="margin-left: 40px">
				<el-input
					v-model="pageForm.email"
					placeholder="输入邮箱"
					clearable
					@keyup.enter="getUserList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
		</el-row>
		<el-row>
			<el-form-item label="性别">
				<el-select v-model="pageForm.gender" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="男" value="1" @click="getUserList"></el-option>
					<el-option label="女" value="0" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="状态" style="margin-left: 30px">
				<el-select v-model="pageForm.status" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="正常" value="0" @click="getUserList"></el-option>
					<el-option label="禁用" value="-1" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="vip" style="margin-left: 30px">
				<el-select v-model="pageForm.vip" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="是" value="1" @click="getUserList"> </el-option>
					<el-option label="否" value="0" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button icon="Refresh" @click="resetQuery">重置</el-button>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button type="primary" icon="Search" @click="getUserList">搜索</el-button>
			</el-form-item>
		</el-row>
	</el-form>

	<el-table
		:data="tableData"
		stripe
		border
		:row-style="{ height: '47px' }"
		style="width: 100%; text-align: left; font-size: 16px"
	>
		<el-table-column prop="userId" type="index" label="序号" width="60"> </el-table-column>
		<el-table-column prop="nickname" label="昵称" min-width="120"> </el-table-column>
		<el-table-column prop="gender" label="性别" min-width="80">
			<template #default="{ row }">
				{{ row.gender == 1 ? '男' : row.gender == 0 ? '女' : '私密' }}
			</template>
		</el-table-column>
		<el-table-column prop="phone" label="电话" min-width="130"> </el-table-column>
		<el-table-column prop="email" label="邮箱" min-width="150"> </el-table-column>
		<el-table-column prop="status" label="状态" min-width="150">
			<template #default="{ row }">
				{{ row.status == 0 ? '正常' : '禁用,' + row.statusExpireAt }}
			</template>
		</el-table-column>
		<el-table-column prop="createdAt" label="注册时间" min-width="150"> </el-table-column>
		<el-table-column fixed="right" label="操作" width="80" align="center">
			<template #default="{ row }">
				<el-button
					type="success"
					size="small"
					@click="OpenUpdateDialog(row)"
					style="font-size: 16px"
					>编辑</el-button
				>
			</template>
		</el-table-column>
	</el-table>
	<!-- 分页器 -->
	<el-pagination
		v-if="tableData.length > 0"
		class="pagination"
		background
		layout="prev, pager, next, jumper, sizes"
		:current-page="pageForm.pageNum"
		:page-size="pageForm.pageSize"
		:page-sizes="[10, 20, 50, 100]"
		:total="total"
		@current-change="handlePageNumChange"
		@size-change="handlePageSizeChange"
	/>
	<el-dialog title="用户详情" v-model="updateUserDialog" width="30%">
		<el-form :model="updateForm">
			<el-form-item label="昵称" prop="name">
				{{ updateForm.nickname }}
			</el-form-item>
			<el-form-item label="头像" prop="avatarUrl">
				<img :src="updateForm.avatarUrl" alt="" style="height: 100px" />
			</el-form-item>
			<el-form-item label="性别" prop="gender">
				{{ updateForm.gender == 1 ? '男' : updateForm.gender == 0 ? '女' : '私密' }}
			</el-form-item>
			<el-form-item label="电话" prop="phone">
				{{ updateForm.phone }}
			</el-form-item>
			<el-form-item label="邮箱" prop="email">
				{{ updateForm.email }}
			</el-form-item>
			<el-form-item label="状态" prop="status">
				{{ updateForm.status == 1 ? '禁用' : updateForm.status == 0 ? '正常' : '其他' }}
			</el-form-item>
			<el-form-item v-show="updateForm.status == 1" label="结束时间" prop="statusTime">
				{{ updateForm.statusExpireAt }}
			</el-form-item>
			<el-form-item>
				<el-button type="danger" v-if="updateForm.status === '0'" @click="showDisableDialog">
					禁用用户
				</el-button>
				<el-button type="success" v-else @click="handleUnbanUser"> 解除禁用 </el-button>
			</el-form-item>
		</el-form>
	</el-dialog>
	<!-- 新增禁用对话框 -->
	<el-dialog title="禁用用户" v-model="disableDialogVisible" width="30%">
		<el-form>
			<el-form-item label="解禁时间">
				<el-date-picker
					v-model="disableExpireTime"
					type="datetime"
					placeholder="选择解禁时间"
					format="YYYY-MM-DD HH:mm:ss"
					value-format="YYYY-MM-DD HH:mm:ss"
				/>
			</el-form-item>
			<el-form-item>
				<el-button @click="disableDialogVisible = false">取消</el-button>
				<el-button type="primary" @click="handleDisableUser">确定</el-button>
			</el-form-item>
		</el-form>
	</el-dialog>
</template>
<script setup>
	import { reactive, onMounted, ref, watch } from 'vue';
	import { apiUpdateUser, apiPageUser } from '@/api/admin/user';
	import { ElMessage } from 'element-plus';
	/**
	 * 查询用户数据
	 */
	// 分页数据
	const pageForm = reactive({
		nickname: '',
		phone: '',
		email: '',
		gender: '-1',
		type: '1',
		vip: '-1',
		status: '-1',
		pageNum: 1,
		pageSize: 10,
	});
	const tableData = reactive([]); // 表格数据
	const total = ref(0); // 数据总数
	// 获取用户数据
	const getUserList = () => {
		apiPageUser(pageForm).then((res) => {
			tableData.splice(0, tableData.length, ...res.data.data.records);
			total.value = res.data.data.total;
		});
	};
	// 重置查询条件
	const resetQuery = () => {
		Object.assign(pageForm, {
			name: '',
			phone: '',
			email: '',
			gender: '-1',
			vip: '-1',
			status: '-1',
		});
		getUserList();
	};

	onMounted(() => {
		getUserList();
	});

	const handlePageNumChange = (val) => {
		pageForm.pageNum = val;
		getUserList();
	};
	const handlePageSizeChange = (val) => {
		pageForm.pageSize = val;
		getUserList();
	};
	/**
	 * 更新用户数据
	 */
	const currentUpdateUserId = ref(null); // 当前修改的用户id
	const updateUserDialog = ref(false); // 修改用户对话框
	// 修改用户数据表单
	const updateForm = reactive({});
	// 打开修改用户对话框
	const OpenUpdateDialog = (row) => {
		updateUserDialog.value = true;
		//拿数据
		currentUpdateUserId.value = row.id;
		Object.assign(updateForm, row);
		//把gender转成字符串
		updateForm.gender = String(updateForm.gender);
		updateForm.status = String(updateForm.status);
		updateForm.type = String(updateForm.type);
		updateForm.vip = String(updateForm.vip);
	};
	const disableDialogVisible = ref(false);
	const disableExpireTime = ref('');

	const showDisableDialog = () => {
		disableExpireTime.value = '';
		disableDialogVisible.value = true;
	};

	const handleDisableUser = async () => {
		if (!disableExpireTime.value) {
			ElMessage.warning('请选择解禁时间');
			return;
		}

		try {
			await apiUpdateUser({
				userId: updateForm.userId,
				status: '1',
				statusExpireAt: disableExpireTime.value,
			});

			ElMessage.success('已禁用用户');
			disableDialogVisible.value = false;
			updateUserDialog.value = false;
			getUserList();
		} catch (error) {
			ElMessage.error('操作失败');
		}
	};

	const handleUnbanUser = async () => {
		try {
			await apiUpdateUser({
				userId: updateForm.userId,
				status: '0',
				statusExpireAt: null,
			});

			ElMessage.success('已解除禁用');
			updateUserDialog.value = false;
			getUserList();
		} catch (error) {
			ElMessage.error('操作失败');
		}
	};
	// 监听 pageForm.pageNum 或 pageForm.pageSize 的变化
	watch(
		() => [pageForm.pageNum, pageForm.pageSize],
		() => {
			getUserList(); // 自动刷新数据
		}
	);
</script>
<style scoped>
	.pagination {
		padding-top: 20px;
	}
</style>
