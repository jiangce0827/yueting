<!-- @/views/admin/UserManagement.vue -->
<template>
	<!-- 分页表单 -->
	<el-form :model="pageForm" @submit.prevent>
		<el-row>
			<el-form-item label="昵称" prop="artistName">
				<el-input
					v-model="pageForm.singerName"
					placeholder="输入昵称"
					clearable
					@keyup.enter="getUserList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
			<el-form-item label="真实姓名" prop="realName" style="margin-left: 40px">
				<el-input
					v-model="pageForm.realName"
					placeholder="输入艺人绑定真实姓名"
					clearable
					@keyup.enter="getUserList"
					style="width: 200px"
				>
				</el-input>
			</el-form-item>
			<el-form-item label="身份证号" prop="idCard" style="margin-left: 40px">
				<el-input
					v-model="pageForm.idCard"
					placeholder="输入艺人绑定身份证号"
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
					<el-option label="禁用" value="1" @click="getUserList"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="语种" style="margin-left: 30px">
				<el-select v-model="pageForm.language" style="width: 100px">
					<el-option label="全部" value="-1" @click="getUserList"></el-option>
					<el-option label="华语" value="1" @click="getUserList"> </el-option>
					<el-option label="欧美" value="2" @click="getUserList"> </el-option>
					<el-option label="日语" value="3" @click="getUserList"> </el-option>
					<el-option label="韩语" value="4" @click="getUserList"> </el-option>
					<el-option label="其他" value="0" @click="getUserList"></el-option>
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
	<!-- 数据表格 -->
	<el-table
		:data="tableData"
		stripe
		border
		:row-style="{ height: '47px' }"
		style="width: 100%; text-align: left; font-size: 16px"
	>
		<el-table-column prop="artistId" type="index" label="序号" width="60"> </el-table-column>
		<el-table-column prop="artistName" label="昵称" min-width="120"> </el-table-column>
		<el-table-column prop="gender" label="性别" width="60">
			<template #default="{ row }">
				{{ row.gender == 1 ? '男' : row.gender == 0 ? '女' : '私密' }}
			</template>
		</el-table-column>
		<el-table-column prop="language" label="语种" width="80">
			<template #default="{ row }">
				{{
					row.singerLanguage == 1
						? '华语'
						: row.singerLanguage == 2
						? '欧美'
						: row.singerLanguage == 3
						? '日韩'
						: '其他'
				}}
			</template>
		</el-table-column>
		<el-table-column prop="status" label="状态" min-width="80">
			<template #default="{ row }">
				<el-tag :type="row.status === 0 ? 'success' : 'danger'">{{
					row.status == 0 ? '正常' : row.status == 1 ? '禁用' : '其他'
				}}</el-tag>
			</template>
		</el-table-column>
		<el-table-column
			prop="createdAt"
			label="注册时间"
			min-width="150"
			sortable="custom"
			:sort-orders="['ascending', 'descending']"
			:sort-by="(row) => new Date(row.createTime).getTime()"
		>
		</el-table-column>
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
	<!-- 编辑歌手对话框 -->
	<el-dialog title="编辑用户" v-model="updateUserDialog" width="60%">
		<el-form :model="updateForm" label-width="75px" label-position="left">
			<el-row>
				<el-col :md="12" :sm="24">
					<el-form-item label="性别" prop="gender" style="width: 200px">
						{{ updateForm.gender == 1 ? '男' : updateForm.gender == 2 ? '女' : '其他' }}
					</el-form-item>

					<el-form-item label="身份" prop="type" style="width: 200px">
						<span class="tags" v-if="updateForm.identities.includes('歌手')">歌手</span>
						<span class="tags" v-if="updateForm.identities.includes('作词')">作词</span>
						<span class="tags" v-if="updateForm.identities.includes('作曲')">作曲</span>
					</el-form-item>
					<el-form-item label="真实姓名" prop="realName" style="width: 200px">
						{{ updateForm.realName }}
					</el-form-item>
					<el-form-item label="身份证" prop="idCard" style="width: 200px">
						{{ updateForm.idCard }}
					</el-form-item>
				</el-col>
				<el-col :md="12" :sm="24">
					<el-form-item label="用户昵称" prop="name">{{ updateForm.userName }}</el-form-item>
					<el-form-item label="歌手名" prop="singerName">
						{{ updateForm.artistName }}
					</el-form-item>
					<el-form-item label="歌手头像" prop="artistAvatarUrl">
						<img :src="updateForm.artistAvatarUrl" alt="" style="height: 100px" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-col>
				<el-form-item label="歌手简介:" prop="singerBio"> {{ updateForm.artistBio }} </el-form-item>
				<el-form-item label="歌手详情:" prop="artistDescription">
					<div
						class="detail-content"
						v-html="formatDescription(updateForm.artistDescription)"
					></div>
				</el-form-item>

				<el-form-item>
					<el-button type="primary" @click="handleUpdateUser">确定</el-button>
				</el-form-item>
			</el-col>
		</el-form>
	</el-dialog>
</template>

<script setup>
	import { reactive, onMounted, ref } from 'vue';
	import { apiPageArtist } from '@/api/admin/artist';
	import { ElMessage } from 'element-plus';
	onMounted(() => {
		getUserList();
	});
	/**
	 * 查询用户数据
	 */
	// 分页数据
	const pageForm = reactive({
		artistName: '',
		phone: '',
		email: '',
		gender: '-1',
		language: '-1',
		status: '-1',
		pageNum: 1,
		pageSize: 10,
	});
	const tableData = reactive([]); // 表格数据
	const total = ref(0); // 数据总数
	// 获取用户数据
	const getUserList = () => {
		apiPageArtist(pageForm).then((res) => {
			tableData.splice(0, tableData.length, ...res.data.data.records);
			total.value = res.data.data.total;
		});
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
	// 重置查询条件
	const resetQuery = () => {
		Object.assign(pageForm, {
			singerName: '',
			phone: '',
			email: '',
			gender: '-1',
			vip: '-1',
			status: '-1',
		});
		getUserList();
	};
	const formatDescription = (html) => {
		if (!html) return '';
		return html
			.replace(/\n/g, '<br>') // 转换换行符
			.replace(/<h2>/g, '<h3 class="section-title">'); // 可选：给标题添加样式
	};
	/**
	 * 更新用户数据
	 */
	const currentUpdateUserId = ref(null); // 当前修改的用户id
	const updateUserDialog = ref(false); // 修改用户对话框
	// 修改用户数据表单
	const updateForm = reactive({
		artistId: '',
		artistName: '',
		artistAvatarUrl: '',
		artistBio: '',
		artistDescription: '',
		userName: '',
		realName: '',
		idCard: '',
		identities: [],
		gender: '',
		status: '',
	});
	// 打开修改用户对话框
	const OpenUpdateDialog = (row) => {
		updateUserDialog.value = true;
		//拿数据
		currentUpdateUserId.value = row.artistId;
		Object.assign(updateForm, row);
		if (!Array.isArray(updateForm.identities)) {
			updateForm.identities = [];
		}
		//把gender转成字符串
		updateForm.gender = String(updateForm.gender);
		updateForm.status = String(updateForm.status);
	}; //修改用户数据
	const handleUpdateUser = () => {
		updateUserDialog.value = false;
	};
</script>

<style scoped>
	.tags {
		font-size: 14px;
		padding: 2px 2px;
	}
	.detail-content {
		line-height: 1.8;
		color: #444;
	}
</style>
