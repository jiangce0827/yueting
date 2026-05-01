<template>
	<div class="banner-management">
		<div class="top-bar">
			<el-button type="primary" icon="Plus" @click="openAddDialog">新增轮播图</el-button>
		</div>

		<el-table :data="tableData" stripe border style="width: 100%; font-size: 14px">
			<el-table-column prop="bannerId" type="index" label="序号" width="60" />
			<el-table-column prop="title" label="标题" min-width="120" />
			<el-table-column label="图片" width="200">
				<template #default="{ row }">
					<el-image
						:src="row.imageUrl"
						fit="cover"
						style="width: 150px; height: 80px"
						:preview-src-list="[row.imageUrl]"
					/>
				</template>
			</el-table-column>
			<el-table-column prop="targetType" label="跳转类型" width="100">
				<template #default="{ row }">
					{{ getTargetTypeName(row.targetType) }}
				</template>
			</el-table-column>
			<el-table-column label="跳转目标" min-width="180">
				<template #default="{ row }">
					{{ getTargetDisplay(row) }}
				</template>
			</el-table-column>
			<el-table-column prop="sortOrder" label="排序" width="80" sortable />
			<el-table-column prop="status" label="状态" width="80">
				<template #default="{ row }">
					<el-tag :type="row.status === 1 ? 'success' : 'danger'">
						{{ row.status === 1 ? '启用' : '禁用' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createdAt" label="创建时间" width="180" />
			<el-table-column fixed="right" label="操作" width="200" align="center">
				<template #default="{ row }">
					<el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
					<el-button
						:type="row.status === 1 ? 'warning' : 'success'"
						size="small"
						@click="toggleStatus(row)"
					>
						{{ row.status === 1 ? '禁用' : '启用' }}
					</el-button>
					<el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<el-pagination
			layout="total, prev, pager, next"
			:total="total"
			v-model:page="pageForm.pageNum"
			v-model:limit="pageForm.pageSize"
			@current-change="getBannerList"
		/>

		<el-dialog
			v-model="dialogVisible"
			:title="isEdit ? '编辑轮播图' : '新增轮播图'"
			width="600px"
			@close="resetForm"
		>
			<el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
				<el-form-item label="标题" prop="title">
					<el-input v-model="form.title" placeholder="请输入轮播图标题" />
				</el-form-item>

				<el-form-item label="图片" prop="imageUrl">
					<div class="image-upload">
						<el-upload
							:show-file-list="false"
							:before-upload="beforeImageUpload"
							accept="image/*"
							:http-request="handleImageUpload"
						>
							<el-image v-if="form.imageUrl" :src="form.imageUrl" fit="cover" class="preview-image" />
							<el-icon v-else class="upload-icon"><Plus /></el-icon>
						</el-upload>
						<span class="upload-tip">点击上传图片</span>
					</div>
				</el-form-item>

				<el-form-item label="跳转类型" prop="targetType">
					<el-select
						v-model="form.targetType"
						placeholder="请选择跳转类型"
						style="width: 100%"
						@change="handleTargetTypeChange"
					>
						<el-option label="无跳转" :value="0" />
						<el-option label="歌曲" :value="1" />
						<el-option label="专辑" :value="2" />
						<el-option label="歌单" :value="3" />
						<el-option label="外部链接" :value="4" />
					</el-select>
				</el-form-item>

				<el-form-item
					v-if="form.targetType >= 1 && form.targetType <= 3"
					label="跳转目标"
					prop="targetId"
				>
					<el-select
						v-model="form.targetId"
						filterable
						remote
						clearable
						reserve-keyword
						default-first-option
						popper-class="banner-target-select-dropdown"
						placeholder="请输入关键词搜索并选择目标"
						style="width: 100%"
						:remote-method="searchTargetOptions"
						:loading="targetOptionsLoading"
						@change="handleTargetSelect"
						@clear="clearTargetSelection"
					>
						<el-option
							v-for="option in targetOptions"
							:key="`${form.targetType}-${option.targetId}`"
							:label="formatTargetOptionLabel(option)"
							:value="option.targetId"
						>
							<div class="target-option">
								<el-image
									v-if="option.coverUrl"
									:src="option.coverUrl"
									fit="cover"
									class="target-option-cover"
								/>
								<div class="target-option-text">
									<div class="target-option-title">{{ option.title }}</div>
									<div v-if="option.subtitle" class="target-option-subtitle">
										{{ option.subtitle }}
									</div>
								</div>
							</div>
						</el-option>
					</el-select>
					<div class="target-search-tip">按名称搜索，选择后会自动填入目标 ID。</div>
					<div v-if="selectedTargetOption" class="selected-target-card">
						<el-image
							v-if="selectedTargetOption.coverUrl"
							:src="selectedTargetOption.coverUrl"
							fit="cover"
							class="selected-target-cover"
						/>
						<div class="selected-target-info">
							<div class="selected-target-title">{{ selectedTargetOption.title }}</div>
							<div v-if="selectedTargetOption.subtitle" class="selected-target-subtitle">
								{{ selectedTargetOption.subtitle }}
							</div>
							<div class="selected-target-id">ID: {{ selectedTargetOption.targetId }}</div>
						</div>
					</div>
				</el-form-item>

				<el-form-item v-if="form.targetType === 4" label="外部链接" prop="targetUrl">
					<el-input v-model="form.targetUrl" placeholder="请输入完整 URL 地址" />
				</el-form-item>

				<el-form-item label="排序" prop="sortOrder">
					<el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
					<span class="sort-tip">数字越小越靠前</span>
				</el-form-item>

				<el-form-item label="状态" prop="status">
					<el-radio-group v-model="form.status">
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
import { reactive, ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import {
	apiPageBanners,
	apiAddBanner,
	apiUpdateBanner,
	apiDeleteBanner,
	apiUpdateBannerStatus,
	apiSearchBannerTargets,
	apiGetBannerTargetOption,
} from '@/api/admin/banner';
import { apiUploadImage } from '@/api/frontend/common';

const tableData = ref([]);
const total = ref(0);
const pageForm = reactive({
	pageNum: 1,
	pageSize: 10,
});

const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const targetOptions = ref([]);
const targetOptionsLoading = ref(false);
const selectedTargetOption = ref(null);

const form = reactive({
	bannerId: null,
	title: '',
	imageUrl: '',
	targetType: 0,
	targetId: '',
	targetUrl: '',
	sortOrder: 0,
	status: 1,
});

const validateTargetId = (_, value, callback) => {
	if (form.targetType >= 1 && form.targetType <= 3 && !value) {
		callback(new Error('请选择跳转目标'));
		return;
	}
	callback();
};

const validateTargetUrl = (_, value, callback) => {
	if (form.targetType === 4 && !value) {
		callback(new Error('请输入外部链接'));
		return;
	}
	callback();
};

const rules = {
	title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
	imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }],
	targetId: [{ validator: validateTargetId, trigger: 'change' }],
	targetUrl: [{ validator: validateTargetUrl, trigger: 'blur' }],
};

const normalizeTargetId = (value) => (value === null || value === undefined ? '' : String(value));

const getBannerList = () => {
	apiPageBanners(pageForm).then((res) => {
		tableData.value = res.data.data.records;
		total.value = res.data.data.total;
	});
};

const getTargetTypeName = (type) => {
	const map = { 0: '无跳转', 1: '歌曲', 2: '专辑', 3: '歌单', 4: '外部链接' };
	return map[type] || '未知';
};

const getTargetDisplay = (row) => {
	if (row.targetType === 4 && row.targetUrl) {
		return row.targetUrl;
	}
	if (row.targetType >= 1 && row.targetType <= 3 && row.targetId) {
		return `${getTargetTypeName(row.targetType)} ID: ${row.targetId}`;
	}
	return '-';
};

const formatTargetOptionLabel = (option) =>
	option.subtitle ? `${option.title} - ${option.subtitle}` : option.title;

const upsertTargetOption = (option) => {
	const normalizedOption = {
		...option,
		targetId: normalizeTargetId(option.targetId),
	};
	const index = targetOptions.value.findIndex((item) => item.targetId === normalizedOption.targetId);
	if (index === -1) {
		targetOptions.value = [normalizedOption, ...targetOptions.value];
	} else {
		targetOptions.value[index] = normalizedOption;
	}
	return normalizedOption;
};

const clearTargetSelection = () => {
	form.targetId = '';
	selectedTargetOption.value = null;
	targetOptions.value = [];
};

const handleTargetTypeChange = (targetType) => {
	if (targetType === 4) {
		clearTargetSelection();
		return;
	}
	if (targetType >= 1 && targetType <= 3) {
		form.targetUrl = '';
		clearTargetSelection();
		return;
	}
	form.targetUrl = '';
	clearTargetSelection();
};

const searchTargetOptions = async (keyword) => {
	if (!(form.targetType >= 1 && form.targetType <= 3) || !keyword?.trim()) {
		targetOptions.value = selectedTargetOption.value ? [selectedTargetOption.value] : [];
		return;
	}

	targetOptionsLoading.value = true;
	try {
		const res = await apiSearchBannerTargets(form.targetType, keyword.trim());
		const options = (res.data.data || []).map((option) => ({
			...option,
			targetId: normalizeTargetId(option.targetId),
		}));
		if (
			selectedTargetOption.value &&
			!options.some((option) => option.targetId === selectedTargetOption.value.targetId)
		) {
			options.unshift(selectedTargetOption.value);
		}
		targetOptions.value = options;
	} finally {
		targetOptionsLoading.value = false;
	}
};

const loadSelectedTargetOption = async () => {
	if (!(form.targetType >= 1 && form.targetType <= 3) || !form.targetId) {
		selectedTargetOption.value = null;
		targetOptions.value = [];
		return;
	}

	const res = await apiGetBannerTargetOption(form.targetType, form.targetId);
	selectedTargetOption.value = upsertTargetOption(res.data.data);
};

const handleTargetSelect = (value) => {
	const targetId = normalizeTargetId(value);
	form.targetId = targetId;
	selectedTargetOption.value =
		targetOptions.value.find((option) => option.targetId === targetId) || selectedTargetOption.value;
};

const openAddDialog = () => {
	resetForm();
	isEdit.value = false;
	dialogVisible.value = true;
};

const openEditDialog = async (row) => {
	resetForm();
	isEdit.value = true;
	Object.assign(form, {
		bannerId: row.bannerId,
		title: row.title,
		imageUrl: row.imageUrl,
		targetType: row.targetType,
		targetId: normalizeTargetId(row.targetId),
		targetUrl: row.targetUrl || '',
		sortOrder: row.sortOrder,
		status: row.status,
	});
	dialogVisible.value = true;
	await loadSelectedTargetOption();
};

const resetForm = () => {
	formRef.value?.clearValidate();
	Object.assign(form, {
		bannerId: null,
		title: '',
		imageUrl: '',
		targetType: 0,
		targetId: '',
		targetUrl: '',
		sortOrder: 0,
		status: 1,
	});
	targetOptions.value = [];
	selectedTargetOption.value = null;
};

const beforeImageUpload = (file) => {
	const isImage = file.type.startsWith('image/');
	const isLt5M = file.size / 1024 / 1024 < 5;
	if (!isImage) {
		ElMessage.error('只能上传图片文件');
	}
	if (!isLt5M) {
		ElMessage.error('图片大小不能超过 5MB');
	}
	return isImage && isLt5M;
};

const handleImageUpload = async (options) => {
	const { file } = options;
	try {
		const formData = new FormData();
		formData.append('image', file);
		const res = await apiUploadImage(formData);
		if (res.data.code === 1) {
			form.imageUrl = res.data.data;
			ElMessage.success('图片上传成功');
		} else {
			ElMessage.error(res.data.msg || '图片上传失败');
		}
	} catch (error) {
		console.error('上传失败', error);
		ElMessage.error('图片上传失败');
	}
};

const handleSubmit = async () => {
	const valid = await formRef.value?.validate().catch(() => false);
	if (!valid) {
		return;
	}

	const data = {
		...form,
		targetId: form.targetType >= 1 && form.targetType <= 3 ? form.targetId : null,
		targetUrl: form.targetType === 4 ? form.targetUrl : null,
	};

	if (isEdit.value) {
		await apiUpdateBanner(data);
		ElMessage.success('更新成功');
	} else {
		await apiAddBanner(data);
		ElMessage.success('新增成功');
	}
	dialogVisible.value = false;
	getBannerList();
};

const toggleStatus = async (row) => {
	const newStatus = row.status === 1 ? 0 : 1;
	await apiUpdateBannerStatus(row.bannerId, newStatus);
	ElMessage.success(newStatus === 1 ? '已启用' : '已禁用');
	getBannerList();
};

const handleDelete = async (row) => {
	await ElMessageBox.confirm('确认删除该轮播图吗？', '提示', { type: 'warning' });
	await apiDeleteBanner(row.bannerId);
	ElMessage.success('删除成功');
	getBannerList();
};

onMounted(() => {
	getBannerList();
});
</script>

<style scoped>
.banner-management {
	.top-bar {
		margin-bottom: 20px;
	}
}

.image-upload {
	display: flex;
	align-items: center;
	gap: 15px;
}

.preview-image {
	width: 150px;
	height: 80px;
	border-radius: 4px;
}

.upload-icon {
	font-size: 28px;
	color: #8c939d;
	width: 150px;
	height: 80px;
	line-height: 80px;
	text-align: center;
	background: #f5f7fa;
	border-radius: 4px;
	border: 1px dashed #d9d9d9;
}

.upload-tip,
.sort-tip,
.target-search-tip {
	color: #909399;
	font-size: 12px;
}

.sort-tip {
	margin-left: 10px;
}

.target-search-tip {
	margin-top: 8px;
	line-height: 1.4;
}

.target-option {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 4px 0;
	min-height: 52px;
}

.target-option-cover,
.selected-target-cover {
	width: 42px;
	height: 42px;
	border-radius: 8px;
	flex: 0 0 42px;
	background: #f5f7fa;
}

.target-option-text,
.selected-target-info {
	min-width: 0;
	flex: 1;
}

.target-option-title,
.selected-target-title {
	color: #303133;
	font-size: 14px;
	font-weight: 600;
	line-height: 1.4;
	white-space: normal;
	word-break: break-all;
}

.target-option-subtitle,
.selected-target-subtitle,
.selected-target-id {
	color: #909399;
	font-size: 12px;
	line-height: 1.4;
	margin-top: 2px;
}

.selected-target-card {
	display: flex;
	align-items: center;
	gap: 12px;
	margin-top: 12px;
	padding: 12px;
	border: 1px solid #ebeef5;
	border-radius: 10px;
	background: #fafafa;
}

:global(.banner-target-select-dropdown .el-select-dropdown__item) {
	height: auto;
	min-height: 68px;
	padding-top: 8px;
	padding-bottom: 8px;
	line-height: 1.4;
	display: flex;
	align-items: center;
}

:global(.banner-target-select-dropdown .el-select-dropdown__item.is-hovering),
:global(.banner-target-select-dropdown .el-select-dropdown__item:hover) {
	height: auto;
}
</style>
