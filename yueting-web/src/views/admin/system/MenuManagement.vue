<template>
    <div class="menu-management">
        <!-- 查询表单 -->
        <el-form :model="pageForm" @submit.prevent>
            <el-row :gutter="20">
                <el-col :span="6">
                    <el-form-item label="菜单名称">
                        <el-input v-model="pageForm.name" clearable placeholder="请输入菜单名称" @keyup.enter="getList" />
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="状态">
                        <el-select v-model="pageForm.status" clearable placeholder="全部">
                            <el-option label="全部" value="-1" />
                            <el-option label="正常" value="0" />
                            <el-option label="禁用" value="1" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="菜单类型">
                        <el-select v-model="pageForm.type" clearable placeholder="全部">
                            <el-option label="全部" value="-1" />
                            <el-option label="目录" value="1" />
                            <el-option label="菜单" value="2" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item>
                        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                        <el-button type="primary" icon="Search" @click="filterTreeData">搜索</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>

        <!-- 操作按钮 -->
        <div class="button-group">
            <el-button type="primary" icon="Plus" @click="openAddDialog">新增菜单</el-button>
            <el-button icon="Refresh" @click="getList">刷新</el-button>
        </div>

        <!-- 树形表格 -->
        <el-table :data="treeData" row-key="id" stripe border :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <el-table-column prop="name" label="菜单名称" min-width="200">
                <template #default="{ row }">
                    <span v-html="getIndent(row)"></span>
                    {{ row.name }}
                </template>
            </el-table-column>
            <el-table-column prop="icon" label="图标" width="100" align="center">
                <template #default="{ row }">
                    <el-icon v-if="row.icon">
                        <component :is="row.icon" />
                    </el-icon>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column prop="typeDesc" label="类型" width="80" align="center">
                <template #default="{ row }">
                    <el-tag :type="getTypeTagType(row.type)">{{ row.typeDesc }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="path" label="路由路径" min-width="150" show-overflow-tooltip />
            <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip />
            <el-table-column prop="sort" label="排序" width="80" align="center" />
            <el-table-column prop="statusDesc" label="状态" width="80" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.statusDesc }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="180" align="center">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 编辑对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
            <el-form ref="formRef" :model="editForm" :rules="formRules" label-width="100px">
                <el-form-item label="父级菜单" prop="parentId">
                    <el-tree-select
                        v-model="editForm.parentId"
                        :data="menuTreeSelectData"
                        :props="{ label: 'name', value: 'id', children: 'children' }"
                        check-strictly
                        clearable
                        placeholder="选择父级菜单（默认为顶级）"
                        :render-after-expand="false"
                    />
                </el-form-item>
                <el-form-item label="菜单名称" prop="name">
                    <el-input v-model="editForm.name" placeholder="请输入菜单名称" />
                </el-form-item>
                <el-form-item label="菜单类型" prop="type">
                    <el-radio-group v-model="editForm.type">
                        <el-radio :value="1">目录</el-radio>
                        <el-radio :value="2">菜单</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="路由路径" prop="path">
                    <el-input v-model="editForm.path" placeholder="请输入路由路径" />
                </el-form-item>
                <el-form-item label="组件路径" prop="component">
                    <el-input v-model="editForm.component" placeholder="请输入组件路径" />
                </el-form-item>
                <el-form-item label="菜单图标" prop="icon">
                    <el-input v-model="editForm.icon" placeholder="请输入图标名称（如 el-icon-menu）" />
                </el-form-item>
                <el-form-item label="显示顺序" prop="sort">
                    <el-input-number v-model="editForm.sort" :min="0" :max="9999" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="editForm.status">
                        <el-radio :value="0">正常</el-radio>
                        <el-radio :value="1">禁用</el-radio>
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
import { reactive, ref, onMounted } from 'vue';
import { apiMenuTree, apiMenuDetail, apiCreateMenu, apiUpdateMenu, apiDeleteMenu } from '@/api/admin/menu';
import { ElMessage, ElMessageBox } from 'element-plus';

const pageForm = reactive({
    pageNum: 1,
    pageSize: 10,
    name: '',
    status: '-1',
    type: '-1'
});

const treeData = reactive([]);
const allTreeData = reactive([]);
const menuTreeSelectData = reactive([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const isEdit = ref(false);
const editForm = reactive({
    id: null,
    parentId: 0,
    name: '',
    type: 2,
    path: '',
    component: '',
    icon: '',
    sort: 0,
    status: 0
});

const formRef = ref(null);

const formRules = {
    name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
    type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
};

const getList = () => {
    apiMenuTree().then((res) => {
        if (res.data.code === 1) {
            allTreeData.splice(0, allTreeData.length, ...res.data.data);
            filterTreeData();
        }
    });
};

const filterTreeData = () => {
    const filter = (list, name, status, type, level = 0) => {
        return list.filter(item => {
            const nameMatch = !name || item.name.toLowerCase().includes(name.toLowerCase());
            const statusMatch = status === '-1' || status === '' || item.status === Number(status);
            const typeMatch = type === '-1' || type === '' || item.type === Number(type);

            const matches = nameMatch && statusMatch && typeMatch;

            if (item.children && item.children.length > 0) {
                item.children = filter(item.children, name, status, type, level + 1);
            }

            return matches || (item.children && item.children.length > 0);
        });
    };
    treeData.splice(0, treeData.length, ...filter(JSON.parse(JSON.stringify(allTreeData)), pageForm.name, pageForm.status, pageForm.type));
};

const getMenuTree = () => {
    apiMenuTree().then((res) => {
        if (res.data.code === 1) {
            const convertToSelect = (list) => {
                return list.map((item) => ({
                    id: item.id,
                    name: item.name,
                    children: item.children && item.children.length > 0 ? convertToSelect(item.children) : []
                }));
            };
            const topMenu = { id: 0, name: '顶级菜单', children: convertToSelect(res.data.data) };
            menuTreeSelectData.splice(0, menuTreeSelectData.length, topMenu);
        }
    });
};

const getIndent = (row) => {
    return '';
};

const resetQuery = () => {
    pageForm.name = '';
    pageForm.status = '-1';
    pageForm.type = '-1';
    filterTreeData();
};

const handlePageNumChange = (newNum) => {
    pageForm.pageNum = newNum;
};

const openAddDialog = () => {
    dialogTitle.value = '新增菜单';
    isEdit.value = false;
    resetEditForm();
    dialogVisible.value = true;
};

const openEditDialog = (row) => {
    dialogTitle.value = '编辑菜单';
    isEdit.value = true;
    // 获取完整详情
    apiMenuDetail(row.id).then((res) => {
        if (res.data.code === 1) {
            Object.assign(editForm, res.data.data);
            // 父菜单ID为0时设为null以便显示"顶级菜单"
            if (editForm.parentId === 0) {
                editForm.parentId = null;
            }
        }
    });
    dialogVisible.value = true;
};

const resetEditForm = () => {
    editForm.id = null;
    editForm.parentId = null;
    editForm.name = '';
    editForm.type = 2;
    editForm.path = '';
    editForm.component = '';
    editForm.icon = '';
    editForm.sort = 0;
    editForm.status = 0;
};

const handleDialogClose = () => {
    formRef.value?.resetFields();
    resetEditForm();
};

const handleSubmit = () => {
    formRef.value?.validate((valid) => {
        if (valid) {
            // 父ID为null时设为0
            const submitData = { ...editForm };
            if (submitData.parentId === null) {
                submitData.parentId = 0;
            }
            // 转换类型为数字
            submitData.type = Number(submitData.type);
            submitData.status = Number(submitData.status);

            if (isEdit.value) {
                apiUpdateMenu(submitData).then((res) => {
                    if (res.data.code === 1) {
                        ElMessage.success('更新成功');
                        dialogVisible.value = false;
                        getList();
                        getMenuTree();
                    } else {
                        ElMessage.error(res.data.msg || '更新失败');
                    }
                });
            } else {
                apiCreateMenu(submitData).then((res) => {
                    if (res.data.code === 1) {
                        ElMessage.success('创建成功');
                        dialogVisible.value = false;
                        getList();
                        getMenuTree();
                    } else {
                        ElMessage.error(res.data.msg || '创建失败');
                    }
                });
            }
        }
    });
};

const handleDelete = (row) => {
    ElMessageBox.confirm(`确定要删除菜单"${row.name}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        apiDeleteMenu(row.id).then((res) => {
            if (res.data.code === 1) {
                ElMessage.success('删除成功');
                getList();
                getMenuTree();
            } else {
                ElMessage.error(res.data.msg || '删除失败');
            }
        });
    }).catch(() => {});
};

const getTypeTagType = (type) => {
    switch (type) {
        case 1: return 'warning';
        case 2: return '';
        case 3: return 'info';
        default: return '';
    }
};

onMounted(() => {
    getList();
    getMenuTree();
});
</script>

<style scoped>
.menu-management {
    padding: 20px;
}

.button-group {
    margin-bottom: 15px;
}

.el-form {
    margin-bottom: 15px;
}

.el-table {
    margin-top: 10px;
}

.el-icon {
    font-size: 18px;
}
</style>
