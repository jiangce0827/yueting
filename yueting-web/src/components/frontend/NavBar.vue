<!-- @/components/NavBar.vue -->
<template>
	<div class="nav-bar">
		<div class="nav-container">
			<!-- 左侧区域 -->
			<div class="log-section" @click="navigate('/')">
				<img src="@/assets/logo.png" class="logo" />
				<span class="brand-name">悦听音乐</span>
			</div>
			<div class="search-section">
				<input
					id="search-input"
					v-model="searchQuery"
					type="text"
					placeholder="搜索音乐/歌手"
					class="search-input"
					@keyup.enter="handleSearch"
				/>
			</div>
			<!-- 导航项 -->
			<div class="nav-items">
				<template v-for="item in navItems" :key="item.path">
					<!-- 内部路由 -->
					<router-link
						v-if="!item.external"
						:to="item.path"
						class="nav-item"
						:class="{ active: isNavItemActive(item) }"
					>
						{{ item.title }}
					</router-link>

					<!-- 外部链接 -->
					<a v-else :href="item.url" class="nav-item" target="_blank" rel="noopener noreferrer">
						{{ item.title }}
					</a>
				</template>
			</div>

			<!-- 右侧区域 -->
			<div class="right-section">
				<template v-if="authStore.isLoggedIn">
					<div
						class="avatar-container"
						@mouseenter="showDropdown = true"
						@mouseleave="showDropdown = false"
					>
						<img :src="authStore.user.avatarUrl" alt="用户头像" class="avatar" />
						<div v-show="showDropdown" class="dropdown-menu">
							<div class="menu-item" @click="navigate(`/user/home?id=${authStore.user.userId}`)">我的主页</div>
							<div class="menu-item" @click="navigate('/user/message')">我的消息</div>
							<div class="menu-item" @click="navigate('/user/setting')">个人设置</div>
							<div class="menu-item" @click="logout">退出</div>
						</div>
					</div>
				</template>
				<template v-else>
					<router-link to="/login" class="login-link">登录</router-link>
				</template>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref } from 'vue';
	import { useRouter, useRoute } from 'vue-router';
	import { useAuthStore } from '@/stores/frontend/auth';
	const router = useRouter();
	const route = useRoute();
	const authStore = useAuthStore();
	const searchQuery = ref('');
	const logPath = ref('');

	const showDropdown = ref(false);

	const navItems = ref([
		{ title: '发现音乐', path: '/discover' },
		{ title: '我的音乐', path: '/my' },
		{ title: '关注', path: '/follow' },
		{ title: '悦听 AI', path: '/ai-chat' },
		{ title: '音乐人', external: true, url: '/musician/artist' },
	]);
	const navigate = (path) => {
		router.push(path);
	};
	const logout = async () => {
		await router.push('/');
		await authStore.logout();
	};

	const handleSearch = () => {
		if (searchQuery.value.trim() === '') {
			return;
		}
		router.push(`/search?keyword=${encodeURIComponent(searchQuery.value)}`);
	};

	const isNavItemActive = (item) => {
		if (item.path === '/my') {
			return route.path === '/my' || route.path.startsWith('/my/');
		}

		if (item.path === '/discover') {
			return route.path === '/discover' || route.path.startsWith('/discover/');
		}

		return route.path === item.path;
	};
</script>

<style lang="scss" scoped>
	/* 外层容器 */
	.nav-bar {
		height: 70px;
		background: #242424;
		display: flex;
		justify-content: center;

		//▂▂▂▂▂▂ 内容容器 ▂▂▂▂▂▂
		// 主要作用：包裹导航栏内容，保持内容居中
		// 关键特性：最小宽度防止内容挤压，flex布局实现元素排列
		.nav-container {
			min-width: 1000px; // 最小宽度保证基础布局
			height: 100%;
			margin: 0 auto;
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 0 50px;
		}

		//▂▂▂▂▂▂ 搜索区域 ▂▂▂▂▂▂
		// 包含搜索输入框，左侧间距保持与logo的距离
		.search-section {
			margin-left: 30px;

			// 搜索框样式
			// 特征：圆角设计，暗色背景，适合深色主题
			.search-input {
				width: 250px;
				padding: 8px 15px;
				border-radius: 20px;
				border: none;
				background: #ffffff;
				color: black;
				margin-right: 30px;
			}
		}

		// 调整右侧区域布局
		.right-section {
			margin-left: auto;
			display: flex;
			align-items: center;
			gap: 20px;

			// 新增登录链接样式
			.login-link {
				font-size: 14px;
				color: #fff;
				padding: 8px 16px;
				border-radius: 4px;
				text-decoration: none;
				transition: background-color 0.3s;

				&:hover {
					color: #787878;
				}
			}
		}

		//▂▂▂▂▂▂ 品牌标识区域 ▂▂▂▂▂▂
		// 包含logo和品牌名称，高度撑满容器
		// 交互：光标指针提示可点击
		.log-section {
			height: 100%;
			display: flex;
			align-items: center;
			cursor: pointer;

			// 圆形logo样式
			.logo {
				width: 45px;
				height: 45px;
				border-radius: 50%;
			}

			// 品牌文字样式
			// 字号较大保持视觉重点，左侧间距与logo保持距离
			.brand-name {
				font-size: 24px;
				margin-left: 15px;
				color: white;
			}
		}

		//▂▂▂▂▂▂ 用户头像及下拉菜单 ▂▂▂▂▂▂
		// 定位容器：相对定位为下拉菜单提供定位基准
		.avatar-container {
			position: relative;
			margin-left: 30px;

			// 圆形头像样式
			.avatar {
				width: 45px;
				height: 45px;
				border-radius: 50%;
				cursor: pointer;
			}

			//▂▂▂▂▂▂ 下拉菜单 ▂▂▂▂▂▂
			// 绝对定位：显示在头像下方
			// 居中技巧：left+transform实现水平居中
			// 视觉设计：阴影增加层次感，圆角柔化边缘
			.dropdown-menu {
				position: absolute;
				top: 100%;
				left: 50%;
				transform: translateX(-50%);
				background: #2a2a2a;
				border-radius: 4px;
				box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
				width: 120px;
				z-index: 1000;

				// 菜单项通用样式
				// 内边距保证点击区域，过渡动画优化交互体验
				.menu-item {
					padding: 12px 16px;
					color: white;
					font-size: 14px;
					cursor: pointer;
					transition: background 0.3s;

					// 悬停状态：深色背景突出当前项
					&:hover {
						background: #000;
					}
				}
			}
		}

		//▂▂▂▂▂▂ 导航菜单项 ▂▂▂▂▂▂
		// 高度撑满容器实现通高效果
		.nav-items {
			display: flex;
			height: 100%;

			// 单个导航项样式
			// 水平内边距控制项间距，过渡动画优化状态切换
			.nav-item {
				position: relative;
				height: 100%;
				padding: 0 30px;
				display: flex;
				align-items: center;
				color: white;
				text-decoration: none;
				font-size: 16px;
				transition: background-color 0.3s;

				// 激活状态指示条
				&::after {
					content: '';
					position: absolute;
					bottom: 3px;
					left: 50%;
					width: 0;
					height: 3px;
					background: #409eff;
					transform: translateX(-50%);
					transition: width 0.3s;
					border-radius: 2px; // 新增圆角
				}

				// 激活状态和悬停状态
				// 使用深色背景突出当前选中项
				&:hover,
				&.router-link-exact-active,
				&.active {
					background-color: #000;

					&::after {
						width: 60%; // 控制下划线长度（原宽度50%）
						border-radius: 3px; // 悬停时稍大圆角（可选）
					}
				}

				// 精确匹配时的更明显指示
				&.router-link-exact-active,
				&.active {
					&::after {
						background: #409eff;
						width: 60%; // 激活状态更长
						height: 3px; // 激活状态更粗
						border-radius: 4px; // 激活状态最大圆角
					}
				}
			}
		}
	}
</style>
