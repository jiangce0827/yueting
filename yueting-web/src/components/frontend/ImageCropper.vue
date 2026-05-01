<!-- @/components/imageCropper.vue -->
<template>
	<div class="cropper-cut">
		<div class="avatar-header">
			<input style="display: none" @change="handleChange" ref="fileRef" type="file" name="" id="" />
			<el-button type="success" @click="fileRef?.click()">选择图片</el-button>
			<span class="tips">上传图片 支持jpg、png、bmp格式的图片，且文件小于5M</span>
		</div>
		<div class="picUpload-wrapper">
			<div class="pic-left-wrapper">
				<VueCropper
					ref="cropperRef"
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
				<div class="title">头像预览</div>

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
					<div class="tips">大尺寸头像</div>
				</div>
				<div class="pic-right-wrapper_small">
					<div :style="previewStyle" class="previewImg">
						<!-- 实时预览 -->
						<div v-if="option.img">
							<div :style="previews.div">
								<img :src="previews.url" :style="previews.img" />
							</div>
						</div>
						<!-- 显示当前头像 -->
						<div v-else>
							<img :src="props.currentAvatar" class="current-avatar" />
						</div>
					</div>
					<div class="tips" style="margin-top: -100px">小尺寸头像</div>
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
			default: '320px',
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
		onClose: {
			type: Function,
			required: true,
		},
	});
	const emit = defineEmits(['onSuccess', 'close']);

	// 响应式状态
	const option = reactive({
		img: '',
		outputSize: 1,
		outputType: 'png',
		info: true,
		canScale: true,
		autoCrop: true,
		autoCropWidth: 240,
		autoCropHeight: 300,
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
				width: '200px',
				height: '200px',
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
			ElMessage.success('上传成功');
			emit('onSuccess', response.data.data); // 传递接口响应数据
			isUploading.value = false;
			
		} catch (error) {
			uploadStatus.value = 'error';
			ElMessage.error(`上传失败: ${error.message}`);
			isUploading.value = false;
		}
	};
	// 返回按钮逻辑
	const handleClose = () => {
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
			.pic-right-wrapper,
			.pic-right-wrapper_small {
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
					height: 200px;
					width: 200px;
				}
			}

			// 小预览特殊处理
			.pic-right-wrapper_small {
				height: 100px;
				.previewImg {
					transform: scale(0.5);
					transform-origin: left top; // 确保缩放基于左上角
					border-radius: 0px;
				}
			}
		}
	}
	.pic-operate {
		margin-top: 10px;
	}
</style>
