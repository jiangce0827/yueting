<script setup>
	import { reactive, onMounted, ref, warn } from 'vue';
	import { apiPageArtistApplication, apiRejectArtist, apiApproveArtist } from '@/api/admin/artist';
	import { ElMessage } from 'element-plus';
	onMounted(() => {
		getUserList();
	});
	/**
	 * 查询用户数据
	 */
	// 分页数据
	const pageForm = reactive({
		nickname: '',
		phone: '',
		email: '',
		applyType: '-1',
		status: '0',
		pageNum: 1,
		pageSize: 10,
	});
	const tableData = reactive([]); // 表格数据
	const total = ref(0); // 数据总数
	// 获取用户数据
	const getUserList = () => {
		apiPageArtistApplication(pageForm).then((res) => {
			tableData.splice(0, tableData.length, ...res.data.data.records);
			total.value = res.data.data.total;
		});
	};
	// 重置查询条件
	const handleResetPageForm = () => {
		Object.assign(pageForm, {
			name: '',
			phone: '',
			email: '',
			vip: '-1',
			applyType: '-1',
			status: '0',
		});
		getUserList();
	};
	//页码变化
	const handlePageNumChange = (newNum) => {
		pageForm.pageNum = newNum;
		getUserList();
	};
	// 页容量变化
	const handlePageSizeChange = () => {
		getUserList();
	};

	/**
	 * 更新用户数据
	 */
	const currentUpdateUserId = ref(null); // 当前修改的用户id
	const updateUserDialog = ref(false); // 修改用户对话框
	// 修改用户数据表单
	const updateForm = reactive({
		id: '',
		name: '',
		avatar: '',
		gender: '',
		phone: '',
		email: '',
		type: '',
		singerReviewStatus: '',
		reason: '',
		statusTime: '',
		vip: '',
		vipTime: '',
	});
	// 打开修改用户对话框
	const OpenUpdateDialog = (row) => {
		updateUserDialog.value = true;
		//拿数据
		currentUpdateUserId.value = row.id;
		Object.assign(updateForm, row);
		//把gender转成字符串
		updateForm.gender = String(updateForm.gender);
		updateForm.type = String(updateForm.type);
		updateForm.vip = String(updateForm.vip);
		updateForm.singerReviewStatus = String(updateForm.singerReviewStatus);
	};
	//修改用户数据
	const handleUpdateUser = (singerReviewStatus) => {
		if (singerReviewStatus == 1) {
			apiApproveArtist(updateForm.applicationId).then((res) => {
				ElMessage.success('审批成功');
				getUserList();
				updateUserDialog.value = false;
			});
		} else if (singerReviewStatus == 0) {
			apiRejectArtist(updateForm.applicationId, updateForm.reason).then((res) => {
				ElMessage.success('审批成功');
				getUserList();
				updateUserDialog.value = false;
			});
		}
	};
</script>

<template>
	<!-- 查询条件 -->
	<el-form :model="pageForm" @submit.prevent>
		<el-row>
			<el-form-item label="昵称" prop="nickname">
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
			<el-form-item label="审批状态">
				<el-select v-model="pageForm.status" style="width: 100px">
					<el-option label="未审批" value="0" @click="getUserList"></el-option>
					<el-option label="已通过" value="1" @click="getUserList"></el-option>
					<el-option label="已拒绝" value="2" @click="getUserList"></el-option>
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="申请类型">
				<el-select v-model="pageForm.applyType" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="新建艺人" value="0" @click="getUserList"></el-option>
					<el-option label="申领艺人" value="1" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button icon="Refresh" @click="handleResetPageForm">重置</el-button>
			</el-form-item>
			<el-form-item style="margin-left: 40px">
				<el-button type="primary" icon="Search" @click="getUserList">搜索</el-button>
			</el-form-item>
		</el-row>
	</el-form>
	<!-- 表格 -->
	<el-table
		:data="tableData"
		stripe
		border
		:row-style="{ height: '47px' }"
		style="width: 100%; text-align: left; font-size: 16px"
	>
		<el-table-column prop="applicationId" type="index" label="序号" width="60"> </el-table-column>
		<el-table-column prop="userName" label="昵称" min-width="120"> </el-table-column>
		<el-table-column prop="applyType" label="申请类型" width="70">
			<template #default="{ row }">
				<el-tag>{{ row.applyType == 0 ? '新建' : '认领' }}</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="artistName" label="艺人名" min-width="80">
			<template #default="{ row }">
				{{ row.artistName }}
			</template>
		</el-table-column>

		<el-table-column prop="artistBio" label="简介" min-width="150" max-width="300">
			<template #default="{ row }">
				<div class="singer-review-message">
					{{ row.artistBio }}
				</div>
			</template>
		</el-table-column>

		<el-table-column prop="singerReviewStatus" label="审批状态" width="80">
			<template #default="{ row }">
				<el-tag :type="row.status == 1 ? 'success' : row.status == 2 ? 'danger' : 'warning'">
					{{ row.status == 0 ? '审批中' : row.status == 1 ? '通过' : '拒绝' }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="appliedAt" label="申请时间" min-width="150"> </el-table-column>
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
	<!-- 分页插件 -->
	<el-pagination
		layout="prev, pager, next"
		:total="total"
		v-model:page="pageForm.pageNum"
		v-model:limit="pageForm.pageSize"
		@current-change="handlePageNumChange"
		@size-change="handlePageSizeChange"
	/>
	<!-- 审批对话框 -->
	<el-dialog title="审批详情" v-model="updateUserDialog" width="30%">
		<el-form :model="updateForm">
			<el-form-item label="昵称" prop="userName">
				{{ updateForm.userName }}
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
			<el-form-item label="状态" prop="singerReviewStatus">
				{{
					updateForm.singerReviewStatus == 0
						? '审批中'
						: updateForm.singerReviewStatus == 1
						? '已通过'
						: '已拒绝'
				}}
			</el-form-item>
			<el-form-item label="简介" prop="artistBio">
				{{ updateForm.artistBio }}
			</el-form-item>
			<el-form-item label="真实姓名" prop="realName">
				{{ updateForm.realName }}
			</el-form-item>
			<el-form-item label="身份证号" prop="idCard">
				{{ updateForm.idCard }}
			</el-form-item>
			<el-form-item label="半身照" prop="halfBody">
				<img :src="updateForm.halfBody" alt="" style="width: 100px; height: 100px" />
			</el-form-item>
			<el-form-item label="证件正面" prop="front">
				<img :src="updateForm.front" alt="" style="width: 100px; height: 100px" />
			</el-form-item>
			<el-form-item label="证件反面" prop="back">
				<img :src="updateForm.back" alt="" style="width: 100px; height: 100px" />
			</el-form-item>
			<el-form-item label="批复">
				<el-input
					type="textarea"
					v-model="updateForm.reason"
					clearable
					:rows="4"
					style="width: 400px"
					placeholder="输入拒绝理由"
				></el-input>
			</el-form-item>
			<el-form-item v-if="updateForm.singerReviewStatus == 0">
				<el-button type="primary" @click="handleUpdateUser(1)">通过</el-button>
				<el-button type="primary" @click="handleUpdateUser(0)">拒绝</el-button>
			</el-form-item>
		</el-form>
	</el-dialog>
</template>

<style scoped>
	/* 允许单元格内容换行 */
	:deep(.el-table .cell) {
		white-space: normal;
	}

	/* 多行文本省略 */
	.singer-review-message {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 1.5em; /* 保持合适的行高 */
		max-height: 3em; /* 两行文本的高度 */
	}
</style>
