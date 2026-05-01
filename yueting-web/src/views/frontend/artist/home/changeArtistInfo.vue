<template>
	<div class="certification-container">
		<!-- 艺人信息部分 -->
		<div class="section">
			<h2>艺人信息</h2>
			<el-form label-width="100px">
				<el-form-item label="艺人名" class="artist-name-wrapper" ref="searchContainer">
					<el-input
						v-model="searchQuery"
						@input="handleSearch"
						placeholder="请输入需要申请或认领的音乐人名称"
						@click="showBelow()"
						style="width: 500px"
						class="artist-name-input"
						ref="searchInput"
					/>
					<div
						v-if="searchResults.length"
						class="search-results"
						:class="{ 'position-right': positionRight }"
					>
						<!-- 添加顶部提示 -->
						<div class="result-hints">
							<p class="hint">以下艺人名已被使用</p>
							<p class="hint-secondary">如果需要更改的艺人名已被使用，请联系客服</p>
						</div>
						<!-- 搜索结果列表 -->
						<div class="result-list">
							<div v-for="item in searchResults" :key="item.name" class="result-item">
								<img :src="item.artistAvatarUrl" alt="Avatar" class="avatar" />
								<span class="name">{{ item.artistName }}</span>
							</div>
						</div>
					</div>
				</el-form-item>

				<el-form-item label="艺人头像">
					<img
						:src="form.artistAvatarUrl"
						@click="avatarShowCropper = true"
						class="avatar-preview"
					/>
					<ImageCropperDialog
						v-model:visible="avatarShowCropper"
						:current-avatar="form.artistAvatarUrl"
						:fixed-number="[1, 1]"
						@success="handleAvatarUploadSuccess"
						@close="handleAvatarClose"
					/>
				</el-form-item>
				<el-form-item label="艺人页头图">
					<img :src="form.artistCoverUrl" @click="coverShowCropper = true" class="cover-preview" />
					<ImageCropperDialog
						v-model:visible="coverShowCropper"
						:current-avatar="form.artistCoverUrl"
						:fixed-number="[2, 1]"
						@success="handleCoverUploadSuccess"
						@close="handleCoverClose"
					/>
				</el-form-item>
				<el-form-item label="性别">
					<el-radio-group v-model="form.gender">
						<el-radio :value="1">男</el-radio>
						<el-radio :value="2">女</el-radio>
						<el-radio :value="0">团体</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="语种">
					<el-select v-model="form.language" placeholder="请选择" style="width: 500px">
						<el-option label="华语" :value="1" />
						<el-option label="欧美" :value="2" />
						<el-option label="日本" :value="3" />
						<el-option label="韩国" :value="4" />
						<el-option label="其他" :value="0" />
					</el-select>
				</el-form-item>
				<el-form-item label="介绍">
					<el-input
						v-model="form.artistBio"
						type="textarea"
						style="width: 500px"
						:rows="4"
						:placeholder="artistBioTips"
					/>
				</el-form-item>
				<el-form-item label="详细介绍">
					<el-input
						v-model="form.artistDescription"
						type="textarea"
						style="width: 500px"
						:rows="8"
						:placeholder="artistDescriptionTips"
					/>
				</el-form-item>
			</el-form>
		</div>
		<div style="display: flex; margin-left: 200px">
			<el-button type="primary" :loading="isSubmitting" @click="handleChangeArtistInfo"
				>保存</el-button
			>
		</div>
	</div>
</template>

<script setup>
	import { computed, reactive, ref, onMounted, onBeforeUnmount } from 'vue';
	import { searchArtistBasic } from '@/api/frontend/artist';
	import { ElMessage } from 'element-plus';
	import ImageCropperDialog from '@/components/frontend/ImageCropperDialog.vue';
	import { apiUpdateArtistInfo } from '@/api/frontend/artist';
	import { useAuthStore } from '@/stores/frontend/auth.js';
	const authStore = useAuthStore();
	const form = reactive({
		artistName: '',
		artistAvatarUrl: authStore.artist.artistAvatarUrl,
		artistCoverUrl: authStore.artist.artistCoverUrl,
		gender: authStore.artist.gender,
		language: authStore.artist.language,
		artistBio: authStore.artist.artistBio,
		artistDescription: authStore.artist.artistDescription,
	});

	//####################艺人名相关方法###########################
	const searchQuery = ref(authStore.artist.artistName); // 搜索关键词
	const searchResults = ref([]); // 搜索结果
	const debouncedSearch = ref(null);
	const positionRight = ref(false); // 控制结果列表的位置
	const searchInput = ref(null);
	const searchContainer = ref(null);
	// 点击外部区域处理
	const handleClickOutside = (event) => {
		if (!searchContainer.value || !searchInput.value) return;

		const containerEl = searchContainer.value.$el;
		const inputEl = searchInput.value.$el;
		const resultsEl = containerEl.querySelector('.search-results');

		// 判断点击目标位置
		const isClickOnInput = inputEl.contains(event.target);
		const isClickOnResults = resultsEl && resultsEl.contains(event.target);

		if (isClickOnInput) {
			// 点击输入框时始终显示在下方
			positionRight.value = false;
		} else if (!isClickOnResults && !isClickOnInput) {
			// 点击外部区域时切换位置
			positionRight.value = true;
		}
	};
	// 显示在下方的逻辑
	const showBelow = () => {
		positionRight.value = false;
	};
	// 生命周期钩子
	onMounted(() => {
		document.addEventListener('click', handleClickOutside);
	});

	onBeforeUnmount(() => {
		document.removeEventListener('click', handleClickOutside);
	});
	// 搜索艺人方法
	const handleSearch = () => {
		if (searchQuery.value.trim() === '') {
			searchResults.value = [];
			return;
		}

		// 防抖处理（可选）
		clearTimeout(debouncedSearch.value);
		debouncedSearch.value = setTimeout(async () => {
			try {
				const response = await searchArtistBasic(searchQuery.value);
				searchResults.value = response.data.data.records;
			} catch (error) {
				console.error('搜索失败:', error);
			}
		}, 300); // 300ms防抖时间
	};

	// ##################### 上传头像和封面 ##########################
	const avatarShowCropper = ref(false); // 控制头像裁剪弹窗的显示
	const coverShowCropper = ref(false); // 控制封面裁剪弹窗的显示
	const handleAvatarUploadSuccess = (newAvatar) => {
		form.artistAvatarUrl = newAvatar;
		avatarShowCropper.value = false;
		ElMessage.success('成功');
	};
	const handleCoverUploadSuccess = (newAvatar) => {
		form.artistCoverUrl = newAvatar;
		coverShowCropper.value = false;
		ElMessage.success('成功');
	};

	const handleAvatarClose = () => {
		avatarShowCropper.value = false;
	};

	const handleCoverClose = () => {
		coverShowCropper.value = false;
	};
	//############## 个人简介 ##################
	const artistBioTips = ref(
		'请控制在10-1000个字，可以填写个人信息、代表作品、创作经历、有自己特色的信息等等，好的介绍可以更吸引更多粉丝关注哦'
	);
	const artistDescriptionTips = ref('标签请写在<h2></h2>中');

	//############## 提交 #####################
	const isSubmitting = ref(false); // 添加加载状态

	// 发送申请
	const handleChangeArtistInfo = async () => {
		if (isSubmitting.value) return;

		// 表单验证
		if (!validateForm()) {
			return;
		}

		isSubmitting.value = true;

		try {
			// 构建提交数据
			const submitData = {
				...form,
				artistName: searchQuery.value,
			};

			// 提交申请
			const res = await apiUpdateArtistInfo(submitData);

			if (res.data.code === 1) {
				ElMessage.success('成功');
			} else {
				ElMessage.error(res.data.msg || '请稍后再试');
			}
		} catch (error) {
			ElMessage.error(error.message || '提交失败');
		} finally {
			isSubmitting.value = false;
		}
	};

	// 表单验证方法
	const validateForm = () => {
		// 验证艺人信息
		if (!searchQuery.value.trim()) {
			ElMessage.error('请输入艺人名称');
			return false;
		}

		if (form.artistAvatarUrl === '/src/assets/upload.png') {
			ElMessage.error('请上传艺人头像');
			return false;
		}

		if (form.artistCoverUrl === '/src/assets/uploadCover.png') {
			ElMessage.error('请上传艺人页头图');
			return false;
		}

		if (!form.artistBio || form.artistBio.length < 10 || form.artistBio.length > 1000) {
			ElMessage.error('艺人介绍需在10-1000字之间');
			return false;
		}
		return true;
	};
</script>
<style lang="scss" scoped>
	.certification-container {
		margin: 0 auto;
		padding: 0 20px;
		width: 100%;
		background: white;

		.section {
			padding: 20px 0px;
			border-radius: 12px;

			.claimed-box {
				border: 1px solid #e5e5e5;
				border-radius: 8px;
				padding: 20px;
				margin-bottom: 20px;

				.artist-info {
					display: flex;
					align-items: center;
					gap: 15px;

					.claimed-avatar {
						width: 60px;
						height: 60px;
						border-radius: 50%;
						object-fit: cover;
					}

					.artist-details {
						.artist-name {
							margin: 0;
							font-size: 18px;
							color: #333;
						}

						.artist-id {
							margin: 4px 0 0;
							font-size: 14px;
							color: #666;
						}
					}
				}

				.cancel-claim {
					margin-top: 15px;
					font-size: 14px;
					color: #666;
					border-top: 1px solid #eee;
					padding-top: 15px;

					.cancel-link {
						color: #2c9cff;
						cursor: pointer;
						&:hover {
							text-decoration: underline;
						}
					}
				}
			}

			h2 {
				margin-bottom: 25px;
				color: #333;
				font-size: 20px;
				padding-bottom: 10px;
				border-bottom: 1px solid #eee;
			}
			.artist-name-wrapper {
				:deep(.el-form-item__content) {
					// 穿透 Element UI 的样式
					position: relative;
				}
				width: 600px;
				.search-results {
					position: absolute;
					top: 100%;
					left: 0;
					width: 450px;
					max-height: 400px; // 限制最大高度
					overflow-y: auto; // 添加滚动条
					z-index: 1000; // 确保悬浮层在最前
					margin-top: 5px; // 添加一点间距
					background: #f7f7f7;
					box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); //添加阴影效果
					transition: all 0.3s ease; // 添加过渡动画
					&.position-right {
						left: calc(100% + 50px); // 增加间距
						top: 0%;
					}
					.result-hints {
						padding: 15px;
						border-bottom: 1px solid #eee;

						.hint {
							font-size: 14px;
							color: #333;
							margin-bottom: 5px;
						}

						.hint-secondary {
							font-size: 12px;
							color: #666;
							margin-top: 3px;
						}
					}
					.result-list {
						display: grid;
						grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
						gap: 20px;
						padding: 15px 0;

						.result-item {
							padding: 5px;
							border-radius: 8px;
							cursor: pointer;
							transition: transform 0.2s;

							&:hover {
								transform: translateY(-2px);
								box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
							}

							.avatar {
								width: 50px;
								height: 50px;
								border-radius: 50%;
								margin: 0 auto 3px;
								display: block;
							}

							.name {
								font-size: 13px;
								display: block;
								text-align: center;
								font-weight: 600;
								color: #333;
							}

							.claim-btn {
								display: block;
								margin: 0 auto 3px;

								padding: 2px 4px;
								background: #2c9cff;
								color: white;
								border: none;
								border-radius: 4px;
								font-size: 14px;
								cursor: pointer;
								transition: background 0.2s;

								&:hover {
									background: #2a8ae6;
								}
							}
						}
					}
				}
			}
			.code-input-group {
				width: 500px;
				display: flex;
				gap: 10px;

				:deep(.code-input) {
					flex: 1;
					.el-input__wrapper {
						border-top-right-radius: 0;
						border-bottom-right-radius: 0;
					}
				}

				:deep(.send-btn) {
					width: 120px;
					border-top-left-radius: 0;
					border-bottom-left-radius: 0;
				}
			}
		}
	}
	.avatar-preview {
		width: 70px;
		height: 70px;
		cursor: pointer;
		border: 2px solid #eee;
	}
	.cover-preview {
		width: 140px;
		height: 70px;
		cursor: pointer;
		border: 2px solid #eee;
	}
	.tips {
		color: #999;
		font-size: 13px;
	}
	.id-upload {
		:deep(.el-form-item__content) {
			line-height: normal;
		}

		.upload-group {
			display: flex;
			gap: 30px;
			margin-top: 10px;

			.upload-item {
				flex: 1;

				.title-group {
					margin-bottom: 10px;
					.item-title {
						display: block;
						font-size: 14px;
						color: #606266;
					}
					.item-tip {
						display: block;
						font-size: 12px;
						color: #909399;
						margin-left: 5px;
					}
				}

				.uploader-box {
					width: 200px;
					height: 140px;
					background-size: cover;
					background-position: center;
					border: 1px solid #dcdfe6;
					border-radius: 4px;
					position: relative;
					overflow: hidden;

					.upload-btn {
						position: absolute;
						bottom: 0;
						left: 0;
						right: 0;
						width: 100%;
						height: 32px;
						line-height: 32px;
						background: rgba(0, 0, 0, 0.5);
						color: white;
						border: none;
						cursor: pointer;
						transition: background 0.2s;

						&:hover {
							background: rgba(0, 0, 0, 0.7);
						}
					}
					.upload-tip {
						position: absolute;
						top: 50%;
						left: 50%;
						transform: translate(-50%, -50%);
						text-align: center;
						color: #909399;
						font-size: 14px;

						.tip-text {
							font-size: 12px;
							margin-top: 4px;
							color: #c0c4cc;
						}
					}
				}
			}
		}
	}
</style>
