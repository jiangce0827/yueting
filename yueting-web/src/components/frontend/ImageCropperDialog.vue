<!-- @/components/imageCropper.vue -->
<template>
	<el-dialog :model-value="visible" :title="title" :width="dialogWidth" :before-close="handleClose">
		<div class="cropper-cut">
			<div class="avatar-header">
				<input
					style="display: none"
					@change="handleChange"
					ref="fileRef"
					type="file"
					name=""
					id=""
				/>
				<el-button type="success" @click="fileRef?.click()">选择图片</el-button>
				<span class="tips">上传图片 支持jpg、png、bmp格式的图片，且文件小于5M</span>
			</div>
			<div class="picUpload-wrapper">
				<div class="pic-left-wrapper">
					<VueCropper
						ref="cropperRef"
						:fixedNumber="option.fixedNumber"
						:img="option.img"
						:outputType="option.outputType"
						:info="true"
						:full="option.full"
						:canMove="option.canMove"
						:canScale="option.canScale"
						:canMoveBox="option.canMoveBox"
						:fixed="option.fixed"
						:fixedBox="option.fixedBox"
						:original="option.original"
						:autoCrop="option.autoCrop"
						:autoCropWidth="cropWith || option.autoCropWidth"
						:autoCropHeight="cropHeight || option.autoCropHeight"
						:centerBox="option.centerBox"
						:high="option.high"
						:infoTrue="option.infoTrue"
						:enlarge="option.enlarge"
						@realTime="realTime"
					></VueCropper>
				</div>
				<!-- 垂直分割线 -->
				<div class="divider"></div>
				<!-- 新增右侧预览容器 -->
				<div class="preview-container">
					<div class="title">预览</div>

					<div class="pic-right-wrapper">
						<div :style="previewStyle" class="previewImg">
							<!-- 实时预览 -->
							<div v-if="option.img">
								<div :style="previews.div" style="border: 1px solid red">
									<img :src="previews.url" :style="previews.img" />
								</div>
							</div>
							<!-- 显示当前头像 -->
							<div v-else>
								<img :src="props.currentAvatar" class="current-avatar" />
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="pic-operate">
				<el-button @click="changeScale(1)" :icon="Plus"> </el-button>
				<el-button @click="changeScale(-1)" :icon="Minus"> </el-button>
				<el-button @click="rotateRight()" :icon="RefreshRight"> </el-button>
				<el-button @click="saveHeadPic" :disabled="isUploading">保存</el-button>
				<el-button @click="handleClose">返回</el-button>
			</div>
		</div>
	</el-dialog>
</template>

<script setup>
	import { ref, reactive } from 'vue';
	import { VueCropper } from 'vue-cropper';
	import { ElMessage } from 'element-plus';
	import { Plus, Minus, RefreshRight } from '@element-plus/icons-vue';
	import { apiUploadImage } from '@/api/frontend/common';
	const props = defineProps({
		limitSize: {
			type: Number,
			default: 100,
		},
		cropWith: {
			type: String,
			default: '300px',
		},
		cropHeight: {
			type: String,
			default: '320px',
		},
		accept: {
			type: Array,
			default: () => [],
		},
		regExp: {
			type: RegExp,
			default: () => /\.(jpg|jpeg|png)$/i,
		},
		currentAvatar: {
			type: String,
			required: true,
		},
		visible: {
			type: Boolean,
			required: true,
		},
		title: {
			type: String,
			default: '编辑',
		},
		dialogWidth: {
			type: String,
			default: '800px',
		},
		fixedNumber: {
			type: Array,
			default: () => [1, 1], // 默认比例
		},
	});
	const emit = defineEmits(['update:visible', 'success', 'close']);

	// 响应式状态
	const option = reactive({
		img: '',
		fixedNumber: props.fixedNumber,
		outputSize: 1,
		outputType: 'png',
		info: true,
		canScale: true,
		autoCrop: true,
		autoCropWidth: 200,
		autoCropHeight: 200,
		fixed: true,
		full: true,
		fixedBox: false,
		canMove: false,
		canMoveBox: true,
		original: true,
		centerBox: false,
		high: true,
		infoTrue: false,
		maxImgSize: 5000,
		enlarge: 1,
		mode: 'contain',
	});
	const previewStyle = ref();
	const previews = ref({});
	const fileRef = ref();
	const cropperRef = ref();
	const isUploading = ref(false);
	const uploadStatus = ref(null);
	const statusText = ref('');
	let _file = null;

	// 实时预览处理
	const realTime = (data) => {
		previews.value = data;
		previewStyle.value = {
			width: data.w + 'px',
			height: data.h + 'px',
			overflow: 'hidden',
			margin: '0',
			zoom: 200 / data.w,
		};
		if (!option.img)
			previewStyle.value = {
				width: '300px',
				height: '300px',
				overflow: 'hidden',
				margin: '0',
				zoom: 1,
			};
	};

	// 文件选择处理
	const handleChange = (e) => {
		const _target = e.target;
		if (_target.files?.length > 0) {
			_file = _target.files[0];

			if (_file.size / 1024 / 1024 > props.limitSize) {
				ElMessage.warning(`图片大小不能超过${props.limitSize}MB`);
				return;
			}

			if (!props.regExp.test(_file.name)) {
				ElMessage.warning('请上传jpg、jpeg、png格式图片');
				return;
			}

			const reader = new FileReader();
			reader.readAsDataURL(_file);
			reader.onload = (e) => {
				option.img = e.target.result;
			};
		}
	};

	// 缩放控制
	const changeScale = (num) => {
		num = num || 1;
		cropperRef.value?.changeScale(num);
	};

	// 旋转控制
	const rotateRight = () => {
		cropperRef.value?.rotateRight();
	};

	// 保存并上传处理
	const saveHeadPic = async () => {
		if (!option.img) {
			ElMessage.warning('请先上传图片');
			return;
		}

		try {
			isUploading.value = true;
			uploadStatus.value = 'loading';
			statusText.value = '正在上传...';

			const blob = await new Promise((resolve, reject) => {
				cropperRef.value.getCropBlob((blob) => {
					blob ? resolve(blob) : reject(new Error('获取裁剪数据失败'));
				});
			});

			const formData = new FormData();
			formData.append('image', blob, 'avatar.png');

			const response = await apiUploadImage(formData);

			uploadStatus.value = 'success';
			emit('success', response.data.data); // 传递接口响应数据
			isUploading.value = false;
		} catch (error) {
			uploadStatus.value = 'error';
			ElMessage.error(`上传失败: ${error.message}`);
		}
	};
	// 返回按钮逻辑
	const handleClose = () => {
		emit('update:visible', false);
		emit('close'); // 调用父组件的关闭事件
	};
</script>

<style lang="scss" scoped>
	.avatar-header {
		display: flex;
		align-items: center;
		padding-bottom: 10px;
		.tips {
			font-size: 14px;
			color: #999;
			padding: 0 5px;
		}
	}
	.picUpload-wrapper {
		height: fit-content;
		display: flex;
		.pic-left-wrapper {
			width: 360px;
			height: 360px;
		}

		.divider {
			width: 1px;
			background: #ebeef5;
			margin: 0 50px; // 左右各50px间距
			height: 360px; // 与左侧高度一致
			align-self: center;
		}

		.preview-container {
			display: flex;
			flex-direction: column;
			gap: 20px; // 设置垂直间距
			width: 240px; // 固定预览容器宽度

			.title {
				text-align: left; // 标题左对齐
				padding-left: 0 8px;
				color: #333;
				font-size: 13px;
			}

			// 统一预览区块样式
			.pic-right-wrapper {
				padding: 0 20px;
				width: 400px;
				max-height: 360px;
				.tips {
					text-align: left; // 标题左对齐
					padding-left: 0 8px;
					color: #999;
					font-size: 13px;
				} // 统一预览区块样式
				.pic-right-wrapper {
					padding: 0 20px;
					width: 200px;
					max-height: 360px;
					.tips {
						text-align: left; // 标题左对齐
						padding-left: 0 8px;
						color: #999;
						font-size: 13px;
					}

					.previewImg {
						max-height: 360px !important;
						overflow: hidden;
					}
					.current-avatar {
						height: 800px;
						width: 8%;
					}
				}

				.previewImg {
					max-height: 360px !important;
					overflow: hidden;
				}
				.current-avatar {
					height: 200px;
					width: 200px;
				}
			}
		}
	}
	.pic-operate {
		margin-top: 10px;
	}
</style>
