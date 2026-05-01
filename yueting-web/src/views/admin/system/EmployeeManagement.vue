<!-- @/views/admin/system/EmployeeManagement.vue -->
<template>
	<div class="employee-management">
		<!-- 顶部操作栏 -->
		<div class="top-bar">
			<el-button type="primary" icon="Plus" @click="openAddDialog">新增员工</el-button>
			<div class="search-wrapper">
				<el-input v-model="searchForm.realName" placeholder="输入员工姓名" clearable @keyup.enter="handleSearch" style="width: 200px">
					<template #append>
						<el-button icon="Search" @click="handleSearch" />
					</template>
				</el-input>
			</div>
		</div>

		<!-- 员工列表 -->
		<el-table :data="tableData" stripe border style="width: 100%; font-size: 14px">
			<el-table-column prop="employeeId" type="index" label="序号" width="80" />
			<el-table-column prop="username" label="账号" min-width="120" />
			<el-table-column prop="realName" label="姓名" min-width="120" />
			<el-table-column label="角色" min-width="200">
				<template #default="{ row }">
					<el-tag v-for="roleName in row.roleNames" :key="roleName" type="info" style="margin-right: 4px">
						{{ roleName }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="isActiveDesc" label="状态" width="100">
				<template #default="{ row }">
					<el-tag :type="row.isActive === 1 ? 'success' : 'danger'">
						{{ row.isActiveDesc }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="lastLoginAt" label="最后登录" min-width="180" />
			<el-table-column prop="createdAt" label="创建时间" min-width="180" />
			<el-table-column fixed="right" label="操作" width="200" align="center">
				<template #default="{ row }">
					<el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
					<el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<!-- 分页 -->
		<el-pagination
			layout="total, prev, pager, next"
			:total="total"
			v-model:page="pageForm.pageNum"
			v-model:limit="pageForm.pageSize"
			@current-change="getEmployeeList"
		/>

		<!-- 新增/编辑对话框 -->
		<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '新增员工'" width="500px" @close="resetForm">
			<el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
				<el-form-item label="账号" prop="username">
					<el-input v-model="form.username" placeholder="请输入登录账号" :disabled="isEdit" />
				</el-form-item>
				<el-form-item :label="isEdit ? '密码' : '密码'" prop="password">
					<el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
					<span v-if="isEdit" class="tip">不填则不修改密码</span>
				</el-form-item>
				<el-form-item label="姓名" prop="realName">
					<el-input v-model="form.realName" placeholder="请输入真实姓名" />
				</el-form-item>
				<el-form-item label="角色" prop="roleIds">
					<el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
						<el-option v-for="role in allRoles" :key="role.roleId" :label="role.roleName" :value="role.roleId" />
					</el-select>
				</el-form-item>
				<el-form-item label="状态" prop="isActive">
					<el-radio-group v-model="form.isActive">
						<el-radio :label="1">启用</el-radio>
						<el-radio :label="0">禁用</el-radio>
					</el-radio-group>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" @click="handleSubmit">确定</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, reactive, onMounted } from 'vue';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { Plus } from '@element-plus/icons-vue';
	import { apiPageEmployee, apiEmployeeDetail, apiCreateEmployee, apiUpdateEmployee, apiDeleteEmployee } from '@/api/admin/employee';
	import { apiAllRoles } from '@/api/admin/role';

	const tableData = ref([]);
	const total = ref(0);
	const pageForm = reactive({
		pageNum: 1,
		pageSize: 10,
	});
	const searchForm = reactive({
		realName: '',
	});

	const dialogVisible = ref(false);
	const isEdit = ref(false);
	const formRef = ref(null);
	const allRoles = ref([]);

	const form = reactive({
		employeeId: null,
		username: '',
		password: '',
		realName: '',
		roleIds: [],
		isActive: 1,
	});

	const rules = {
		username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
		password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
		realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
		roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }],
	};

	const getEmployeeList = () => {
		apiPageEmployee(pageForm.pageNum, pageForm.pageSize, searchForm.realName || null).then((res) => {
			if (res.data.code === 1) {
				tableData.value = res.data.data.records;
				total.value = res.data.data.total;
			}
		});
	};

	const getAllRoles = () => {
		apiAllRoles().then((res) => {
			if (res.data.code === 1) {
				allRoles.value = res.data.data;
			}
		});
	};

	const handleSearch = () => {
		pageForm.pageNum = 1;
		getEmployeeList();
	};

	const openAddDialog = () => {
		isEdit.value = false;
		dialogVisible.value = true;
	};

	const openEditDialog = async (row) => {
		isEdit.value = true;
		const res = await apiEmployeeDetail(row.employeeId);
		if (res.data.code === 1) {
			Object.assign(form, {
				employeeId: res.data.data.employeeId,
				username: res.data.data.username,
				password: '',
				realName: res.data.data.realName,
				roleIds: res.data.data.roleIds || [],
				isActive: res.data.data.isActive,
			});
		}
		dialogVisible.value = true;
	};

	const resetForm = () => {
		formRef.value?.resetFields();
		Object.assign(form, {
			employeeId: null,
			username: '',
			password: '',
			realName: '',
			roleIds: [],
			isActive: 1,
		});
	};

	const handleSubmit = async () => {
		await formRef.value?.validate((valid) => {
			if (!valid) return;
		});

		const data = { ...form };
		if (isEdit.value && !data.password) {
			delete data.password;
		}

		if (isEdit.value) {
			await apiUpdateEmployee(data);
			ElMessage.success('更新成功');
		} else {
			await apiCreateEmployee(data);
			ElMessage.success('新增成功');
		}
		dialogVisible.value = false;
		getEmployeeList();
	};

	const handleDelete = async (row) => {
		await ElMessageBox.confirm('确认删除该员工吗？', '提示', { type: 'warning' });
		await apiDeleteEmployee(row.employeeId);
		ElMessage.success('删除成功');
		getEmployeeList();
	};

	onMounted(() => {
		getEmployeeList();
		getAllRoles();
	});
</script>

<style scoped>
	.employee-management {
		.top-bar {
			display: flex;
			justify-content: space-between;
			margin-bottom: 20px;
		}

		.search-wrapper {
			display: flex;
			gap: 10px;
		}

		.tip {
			font-size: 12px;
			color: #909399;
			margin-left: 8px;
		}
	}
</style>