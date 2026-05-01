<!-- @/views/admin/RoleManagement.vue -->
<template>
	<div class="role-management">
		<!-- 顶部操作栏 -->
		<div class="top-bar">
			<el-button type="primary" icon="Plus" @click="openAddDialog">新增角色</el-button>
		</div>

		<!-- 角色列表 -->
		<el-table :data="tableData" stripe border style="width: 100%; font-size: 14px">
			<el-table-column prop="roleId" type="index" label="序号" width="80" />
			<el-table-column prop="roleName" label="角色名称" min-width="150" />
			<el-table-column prop="roleKey" label="角色标识" min-width="150" />
			<el-table-column prop="description" label="描述" min-width="200" />
			<el-table-column prop="status" label="状态" width="100">
				<template #default="{ row }">
					<el-tag :type="row.status === 0 ? 'success' : 'danger'">
						{{ row.status === 0 ? '正常' : '禁用' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createdAt" label="创建时间" width="180" />
			<el-table-column fixed="right" label="操作" width="280" align="center">
				<template #default="{ row }">
					<el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
					<el-button type="warning" size="small" @click="openMenuDialog(row)">分配菜单</el-button>
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
			@current-change="getRoleList"
		/>

		<!-- 新增/编辑对话框 -->
		<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" @close="resetForm">
			<el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
				<el-form-item label="角色名称" prop="roleName">
					<el-input v-model="form.roleName" placeholder="请输入角色名称" />
				</el-form-item>
				<el-form-item label="角色标识" prop="roleKey">
					<el-input v-model="form.roleKey" placeholder="请输入角色标识（如：SONG_APPROVER）" />
				</el-form-item>
				<el-form-item label="描述" prop="description">
					<el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
				</el-form-item>
				<el-form-item label="状态" prop="status">
					<el-radio-group v-model="form.status">
						<el-radio :label="0">正常</el-radio>
						<el-radio :label="1">禁用</el-radio>
					</el-radio-group>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" @click="handleSubmit">确定</el-button>
			</template>
		</el-dialog>

		<!-- 分配菜单对话框 -->
		<el-dialog v-model="menuDialogVisible" title="分配菜单" width="500px" @close="resetMenuForm">
			<el-form :model="menuForm" ref="menuFormRef" label-width="100px">
				<el-form-item label="角色名称">
					<span>{{ currentRole?.roleName }}</span>
				</el-form-item>
				<el-form-item label="分配菜单">
					<el-tree
						ref="menuTreeRef"
						:data="menuTreeData"
						:props="{ label: 'name', children: 'children' }"
						node-key="id"
						show-checkbox
						default-expand-all
						check-strictly
						style="max-height: 400px; overflow-y: auto"
					/>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="menuDialogVisible = false">取消</el-button>
				<el-button type="primary" @click="handleMenuSubmit">确定</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, reactive, onMounted } from 'vue';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { Plus } from '@element-plus/icons-vue';
	import { apiPageRole, apiRoleDetail, apiCreateRole, apiUpdateRole, apiDeleteRole, apiAllRoles, apiAssignRoleMenus } from '@/api/admin/role';
	import { apiMenuTree } from '@/api/admin/menu';

	const tableData = ref([]);
	const total = ref(0);
	const pageForm = reactive({
		pageNum: 1,
		pageSize: 10,
	});

	const dialogVisible = ref(false);
	const isEdit = ref(false);
	const formRef = ref(null);

	const form = reactive({
		roleId: null,
		roleName: '',
		roleKey: '',
		description: '',
		status: 0,
		menuIds: [],
	});

	const rules = {
		roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
		roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
	};

	// 菜单分配相关
	const menuDialogVisible = ref(false);
	const menuFormRef = ref(null);
	const menuTreeRef = ref(null);
	const currentRole = ref(null);
	const menuTreeData = ref([]);

	const menuForm = reactive({
		roleId: null,
		roleName: '',
	});

	const getRoleList = () => {
		apiPageRole(pageForm.pageNum, pageForm.pageSize).then((res) => {
			if (res.data.code === 1) {
				tableData.value = res.data.data.records;
				total.value = res.data.data.total;
			}
		});
	};

	const openAddDialog = () => {
		isEdit.value = false;
		dialogVisible.value = true;
	};

	const openEditDialog = async (row) => {
		isEdit.value = true;
		const res = await apiRoleDetail(row.roleId);
		if (res.data.code === 1) {
			Object.assign(form, {
				roleId: res.data.data.roleId,
				roleName: res.data.data.roleName,
				roleKey: res.data.data.roleKey,
				description: res.data.data.description,
				status: res.data.data.status,
			});
		}
		dialogVisible.value = true;
	};

	const resetForm = () => {
		formRef.value?.resetFields();
		Object.assign(form, {
			roleId: null,
			roleName: '',
			roleKey: '',
			description: '',
			status: 0,
		});
	};

	const handleSubmit = async () => {
		await formRef.value?.validate((valid) => {
			if (!valid) return;
		});

		const data = { ...form };
		delete data.menuIds;

		if (isEdit.value) {
			await apiUpdateRole(data);
			ElMessage.success('更新成功');
		} else {
			await apiCreateRole(data);
			ElMessage.success('新增成功');
		}
		dialogVisible.value = false;
		getRoleList();
	};

	const handleDelete = async (row) => {
		await ElMessageBox.confirm('确认删除该角色吗？', '提示', { type: 'warning' });
		await apiDeleteRole(row.roleId);
		ElMessage.success('删除成功');
		getRoleList();
	};

	// 分配菜单
	const openMenuDialog = async (row) => {
		currentRole.value = row;
		menuForm.roleId = row.roleId;
		menuForm.roleName = row.roleName;

		// 获取菜单树
		const menuRes = await apiMenuTree();
		if (menuRes.data.code === 1) {
			menuTreeData.value = menuRes.data.data || [];
		}

		// 获取该角色已有的菜单
		const roleRes = await apiRoleDetail(row.roleId);
		if (roleRes.data.code === 1) {
			const checkedKeys = roleRes.data.data.menuIds || [];
			// 设置选中状态
			setTimeout(() => {
				if (menuTreeRef.value) {
					menuTreeRef.value.setCheckedKeys(checkedKeys);
				}
			}, 0);
		}

		menuDialogVisible.value = true;
	};

	const resetMenuForm = () => {
		if (menuTreeRef.value) {
			menuTreeRef.value.setCheckedKeys([]);
		}
		currentRole.value = null;
	};

	const handleMenuSubmit = async () => {
		const checkedNodes = menuTreeRef.value?.getCheckedNodes() || [];
		const menuIds = checkedNodes.map((node) => node.id);
		await apiAssignRoleMenus(menuForm.roleId, menuIds);
		ElMessage.success('菜单分配成功');
		menuDialogVisible.value = false;
	};

	onMounted(() => {
		getRoleList();
	});
</script>

<style scoped>
	.role-management {
		.top-bar {
			margin-bottom: 20px;
		}
	}
</style>