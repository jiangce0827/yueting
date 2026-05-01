<template>
	<MusicianNavBar />
	<div class="certification-container">
		<!-- 艺人信息部分 -->
		<div class="section">
			<h2>艺人信息</h2>
			<!-- 认领状态显示 -->
			<div v-if="isClaimed" class="claimed-box">
				<div class="artist-info">
					<img :src="selectedArtist.artistAvatarUrl" alt="Avatar" class="claimed-avatar" />
					<div class="artist-details">
						<h3 class="artist-name">{{ selectedArtist.artistName }}</h3>
					</div>
				</div>
				<p class="cancel-claim" @click="cancelClaim">
					发现此艺人不是你要认领的艺人？<span class="cancel-link">取消认领此艺人</span>
				</p>
			</div>
			<el-form v-else label-width="100px">
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
							<p class="hint">为您匹配到以下许的账号，可点击直接认领</p>
							<p class="hint-secondary">若无匹配结果，可以点击其他区域创建新的艺人账号</p>
						</div>
						<!-- 搜索结果列表 -->
						<div class="result-list">
							<div v-for="item in searchResults" :key="item.name" class="result-item">
								<img :src="item.artistAvatarUrl" alt="Avatar" class="avatar" />
								<span class="name">{{ item.artistName }}</span>
								<button @click="claimMusician(item)" class="claim-btn">认领</button>
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
			</el-form>
		</div>
		<div class="section">
			<h2>实名认证</h2>
			<el-form label-width="100">
				<el-form-item label="真实姓名">
					<el-input
						v-model="form.realName"
						style="width: 500px"
						placeholder="请输入真实姓名，身份认证后不可更改"
					/>
				</el-form-item>
				<el-form-item label="邮箱">
					{{ authStore.user.email }}
					<span class="tips">(您可以在云音乐帐号的绑定设置中更换手机)</span>
				</el-form-item>
				<el-form-item label="验证码">
					<div class="code-input-group">
						<el-input v-model="form.code" placeholder="请输入验证码" class="code-input" />
						<el-button type="primary" class="send-btn" @click="sendVerificationCode">
							{{ countdown > 0 ? `重新发送(${countdown}s)` : '发送验证码' }}
						</el-button>
					</div>
				</el-form-item>
				<el-form-item label="证件号">
					<el-input v-model="form.idCard" placeholder="请输入证件号" style="width: 500px" />
				</el-form-item>
				<!-- 在证件号输入框下方添加 -->
				<el-form-item label="身份认证" class="id-upload">
					<div class="upload-group">
						<!-- 个人半身照 -->
						<div class="upload-item">
							<div class="title-group">
								<span class="item-title">个人半身照</span>
								<span class="item-tip">(需手持身份证露上半身)</span>
							</div>
							<el-upload
								action=""
								:show-file-list="false"
								:http-request="handleUpload"
								:before-upload="(file) => beforeAvatarUpload(file, 'halfBody')"
							>
								<div
									class="uploader-box"
									:style="{
										backgroundImage: form.halfBody
											? `url(${form.halfBody})`
											: 'url(/src/assets/idCard_halfBody.png)',
										backgroundPosition: '0px 0px',
									}"
								>
									<div class="upload-tip" v-if="!form.halfBody">
										<div>点击上传照片</div>
									</div>
									<div class="upload-tip" v-else>
										<div>点击更换照片</div>
									</div>
								</div>
							</el-upload>
						</div>

						<!-- 证件正面 -->
						<div class="upload-item">
							<div class="title-group">
								<span class="item-title">证件正面图</span>
								<span class="item-tip">(国徽面)</span>
							</div>
							<el-upload
								action=""
								:show-file-list="false"
								:http-request="handleUpload"
								:before-upload="(file) => beforeAvatarUpload(file, 'front')"
							>
								<div
									class="uploader-box"
									:style="{
										backgroundImage: form.front
											? `url(${form.front})`
											: 'url(/src/assets/idCard_front.png)',
											backgroundPosition: '0px -10px',

									}"
								>
									<div class="upload-tip" v-if="!form.front">
										<div>点击上传照片</div>
									</div>
									<div class="upload-tip" v-else>
										<div>点击更换照片</div>
									</div>
								</div>
							</el-upload>
						</div>

						<!-- 证件反面 -->
						<div class="upload-item">
							<div class="title-group">
								<span class="item-title">证件正反面</span>
								<span class="item-tip">(头像面)</span>
							</div>
							<el-upload
								action=""
								:show-file-list="false"
								:http-request="handleUpload"
								:before-upload="(file) => beforeAvatarUpload(file, 'back')"
							>
								<div
									class="uploader-box"
									:style="{
										backgroundImage: form.back
											? `url(${form.back})`
											: 'url(/src/assets/idCard_back.png)',
											backgroundPosition: '0px -10px',

									}"
								>
									<!-- {{ form.back ? '重新选择' : '选择文件' }} -->
									<div class="upload-tip" v-if="!form.back">
										<div>点击上传照片</div>
									</div>
									<div class="upload-tip" v-else>
										<div>点击更换照片</div>
									</div>
								</div>
							</el-upload>
						</div>
					</div>
				</el-form-item>
			</el-form>
		</div>
		<div style="display: flex; justify-content: center">
			<el-button type="primary" :loading="isSubmitting" @click="handleArtistApply"
				>提交审核</el-button
			>
		</div>
	</div>
</template>

<script setup>
	import MusicianNavBar from '@/components/frontend/MusicianNavBar.vue';
	import { computed, reactive, ref, onMounted, onBeforeUnmount } from 'vue';
	import { searchArtistBasic } from '@/api/frontend/artist';
	import { ElMessage } from 'element-plus';
	import ImageCropperDialog from '@/components/frontend/ImageCropperDialog.vue';
	import { applyArtist } from '@/api/frontend/artist';
	import { useAuthStore } from '@/stores/frontend/auth.js';
	import { apiSendCodeEmailToMe } from '@/api/frontend/auth';
	import { apiUploadImage } from '@/api/frontend/common';
	const authStore = useAuthStore();
	const form = reactive({
		artistName: '',
		artistAvatarUrl: '/src/assets/upload.png',
		artistCoverUrl: '/src/assets/uploadCover.png',
		gender: 1,
		language: 1,
		artistBio: '',
		realName: '',
		idCard: '',
		code: '',
		halfBodyFile: null, // 存储File对象
		frontFile: null,
		backFile: null,
		halfBody: '', // 预览URL
		front: '',
		back: '',
	});

	//####################艺人名相关方法###########################
	const searchQuery = ref(''); // 搜索关键词
	const searchResults = ref([]); // 搜索结果
	const debouncedSearch = ref(null);
	const positionRight = ref(false); // 控制结果列表的位置
	const searchInput = ref(null);
	const searchContainer = ref(null);
	const isClaimed = ref(false); // 是否已认领
	const selectedArtist = ref(null); // 已认领的艺人数据
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

	// 认领方法
	const claimMusician = (item, event) => {
		selectedArtist.value = item;
		isClaimed.value = true;
		searchResults.value = []; // 清空搜索结果
	};
	// 取消认领方法
	const cancelClaim = () => {
		isClaimed.value = false;
		selectedArtist.value = null;
		searchQuery.value = ''; // 可选：清空搜索词
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
	// ############### 邮箱验证码 #####################
	const countdown = ref(0);
	const timer = ref(null);

	const sendVerificationCode = () => {
		if (countdown.value > 0) return;

		// 发送验证码请求
		apiSendCodeEmailToMe();

		// 开始倒计时
		countdown.value = 60;
		timer.value = setInterval(() => {
			countdown.value--;
			if (countdown.value <= 0) {
				clearInterval(timer.value);
				timer.value = null;
			}
		}, 1000);
	};

	// 组件卸载时清除定时器
	onBeforeUnmount(() => {
		if (timer.value) {
			clearInterval(timer.value);
		}
	});

	// ############# 证件照 ###################

	// 图片上传处理
	const beforeAvatarUpload = (file, type) => {

		const isImage = file.type.startsWith('image/');
		if (!isImage) {
			ElMessage.error('只能上传图片文件');
			return false;
		}

		// 存储原始File对象并生成预览
		form[`${type}File`] = file;
		form[type] = URL.createObjectURL(file); // 使用更高效的预览方式

		return false;
	};
	const handleUpload = () => {
		// 这是一个空方法，用于阻止默认上传行为
		// 我们已经在 beforeAvatarUpload 中处理了文件
	};
	//############## 提交 #####################
	const isSubmitting = ref(false); // 添加加载状态

	// 发送申请
	const handleArtistApply = async () => {
		if (isSubmitting.value) return;

		// 表单验证
		if (!validateForm()) {
			return;
		}

		isSubmitting.value = true;

		try {
			// 验证必填项
			const requiredFields = [
				{ field: 'halfBody', name: '个人半身照', file: form.halfBodyFile },
				{ field: 'front', name: '证件正面', file: form.frontFile },
				{ field: 'back', name: '证件背面', file: form.backFile },
			];

			// 检查文件是否上传
			const missing = requiredFields.find((item) => !item.file);
			if (missing) throw new Error(`请上传${missing.name}`);

			// 并行上传所有图片
			const uploadTasks = requiredFields.map(({ field, file }) => {
				const formData = new FormData();
				formData.append('image', file);
				return apiUploadImage(formData)
					.then((res) => ({ field, url: res.data.data }))
					.catch((error) => {
						throw new Error(`${field}上传失败: ${error.message}`);
					});
			});

			// 等待所有上传完成
			const uploadResults = await Promise.all(uploadTasks);

			// 构建提交数据
			const submitData = {
				...form,
				artistName: isClaimed.value ? selectedArtist.value.artistName : searchQuery.value,
				// 新增字段
				applyType: isClaimed.value ? 1 : 0, // 0=申请新艺人，1=认领艺人
				claimedArtistId: isClaimed.value ? selectedArtist.value.artistId : null, // 认领的艺人ID
				// 用上传后的URL替换预览URL
				...uploadResults.reduce((acc, cur) => {
					acc[cur.field] = cur.url;
					return acc;
				}, {}),
			};

			// 移除不需要提交的字段
			delete submitData.halfBodyFile;
			delete submitData.frontFile;
			delete submitData.backFile;

			// 提交申请
			const res = await applyArtist(submitData);

			if (res.data.code === 1) {
				ElMessage.success('提交成功');
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
		if (!isClaimed.value && !searchQuery.value.trim()) {
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

		// 验证实名认证信息
		if (!form.realName) {
			ElMessage.error('请输入真实姓名');
			return false;
		}

		if (!form.code) {
			ElMessage.error('请输入验证码');
			return false;
		}

		if (!form.idCard) {
			ElMessage.error('请输入证件号');
			return false;
		}

		// 验证身份证号格式（简单示例）
		if (!/^\d{17}[\dXx]$/.test(form.idCard)) {
			ElMessage.error('请输入正确的证件号');
			return false;
		}

		return true;
	};
	// 添加组件卸载时清理
	onBeforeUnmount(() => {
		['halfBody', 'front', 'back'].forEach((type) => {
			if (form[type]) URL.revokeObjectURL(form[type]);
		});
	});
</script>
<style lang="scss" scoped>
	.certification-container {
		margin: 0 auto;
		padding: 0 20px;
		width: 1200px;
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
