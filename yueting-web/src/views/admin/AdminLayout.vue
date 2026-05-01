<!-- @/views/admin/AdminLayout.vue -->
<template>
	<div class="admin-container">
		<!-- 顶部导航栏 -->
		<header class="admin-header">
			<div class="header-left">
				<img src="@/assets/logo.png" alt="公司Logo" class="header-logo" />
				<h1 class="header-title">悦听后台管理系统</h1>
			</div>
			<div class="header-right">
				<div
					class="user-info-wrapper"
					@mouseenter="showDropdown = true"
					@mouseleave="showDropdown = false"
				>
					<span class="staff-name">{{ adminStore.emp.realName }}</span>
					<transition name="fade">
						<div v-show="showDropdown" class="dropdown-menu">
							<div class="menu-item" @click="handleLogout">退出登录</div>
						</div>
					</transition>
				</div>
			</div>
		</header>

		<div class="admin-content-wrapper">
			<aside class="admin-sidebar">
				<el-menu
					:default-active="activeMenu"
					class="admin-menu"
					background-color="#001529"
					text-color="rgba(255, 255, 255, 0.65)"
					active-text-color="#fff"
					router
				>
					<template v-for="menu in menuTree" :key="menu.id">
						<!-- 目录类型：可展开的子菜单 -->
						<el-sub-menu v-if="menu.type === 1 && menu.status === 0" :index="String(menu.id)">
							<template #title>
								<el-icon v-if="menu.icon">
									<component :is="menu.icon" />
								</el-icon>
								<span>{{ menu.name }}</span>
							</template>
							<template v-for="child in menu.children" :key="child.id">
								<el-menu-item
									v-if="child.type === 2 && child.status === 0"
									:index="child.path || '/'"
								>
									<el-icon v-if="child.icon">
										<component :is="child.icon" />
									</el-icon>
									<span>{{ child.name }}</span>
								</el-menu-item>
							</template>
						</el-sub-menu>

						<!-- 菜单类型：直接可跳转的菜单项 -->
						<el-menu-item
							v-else-if="menu.type === 2 && menu.status === 0"
							:index="menu.path || '/'"
						>
							<el-icon v-if="menu.icon">
								<component :is="menu.icon" />
							</el-icon>
							<span>{{ menu.name }}</span>
						</el-menu-item>
					</template>
				</el-menu>
			</aside>
			<main class="admin-main">
				<router-view />
			</main>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, computed } from 'vue';
	import { useRouter, useRoute } from 'vue-router';
	import { useAdminStore } from '../../stores/admin/admin';

	const router = useRouter();
	const route = useRoute();
	const adminStore = useAdminStore();
	const showDropdown = ref(false);

	const activeMenu = computed(() => route.path);
	const menuTree = computed(() => adminStore.menus || []);

	const handleLogout = () => {
		adminStore.logout();
		router.push('/admin/login');
	};
</script>

<style lang="scss" scoped>
	.admin-container {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
		background: #f0f2f5;
	}

	.admin-header {
		height: 50px;
		background: #001529;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 24px;
		z-index: 1;

		.header-left {
			display: flex;
			align-items: center;
			gap: 16px;

			.header-logo {
				height: 40px;
				width: auto;
			}

			.header-title {
				margin: 0;
				font-size: 20px;
				color: rgba(255, 255, 255);
				font-weight: 600;
			}
		}

		.header-right {
			position: relative;

			.user-info-wrapper {
				cursor: pointer;
				position: relative;
				padding: 8px 12px;
				border-radius: 4px;
				transition: background-color 0.3s;

				&:hover {
					background-color: rgba(255, 255, 255, 0.1);
				}

				.dropdown-menu {
					position: absolute;
					top: 90%;
					right: 0;
					min-width: 120px;
					background: #fff;
					box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
					border-radius: 4px;
					margin-top: 8px;
					z-index: 1000;

					.menu-item {
						padding: 8px 16px;
						color: rgba(0, 0, 0, 0.85);
						transition: all 0.3s;

						&:hover {
							background-color: #f5f5f5;
							color: #1890ff;
						}
					}
				}
			}

			.staff-name {
				color: #fff;
			}
		}
	}

	.admin-content-wrapper {
		flex: 1;
		display: flex;
		overflow: hidden;
	}

	.admin-sidebar {
		width: 240px;
		background: #001529;
		height: calc(100vh - 50px);
		overflow-y: auto;

		.admin-menu {
			border-right: none;

			:deep(.el-sub-menu__title) {
				&:hover {
					background-color: #1890ff;
				}
			}

			:deep(.el-menu-item) {
				&:hover {
					background-color: #1890ff;
				}

				&.is-active {
					background-color: #1890ff;
					border-right: 3px solid #fff;
				}
			}
		}
	}

	.admin-main {
		flex: 1;
		padding: 24px;
		overflow-y: auto;
		height: calc(100vh - 50px);
	}

	.fade-enter-active,
	.fade-leave-active {
		transition: opacity 0.2s;
	}
	.fade-enter-from,
	.fade-leave-to {
		opacity: 0;
	}
</style>
