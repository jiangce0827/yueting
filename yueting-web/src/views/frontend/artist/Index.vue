<template>
	<div class="artist-container">
		<MusicianNavBar />

		<div class="app-container">
			<div class="main-content">
				<div class="sidebar">
					<div class="sidebar-scroll">
						<div class="user-profile">
							<div class="avatar-container">
								<img :src="authStore.artist.artistAvatarUrl" alt="头像" class="user-avatar" />
							</div>
							<div class="user-info">
								<h3 class="nickname">{{ authStore.artist.artistName }}</h3>
								<button
									class="publish-btn"
									:disabled="!authStore.isArtistActive"
									@click="publishWork"
								>
									发布作品
								</button>
							</div>
						</div>

						<nav class="side-nav">
							<router-link
								v-for="item in navItems"
								:key="item.path"
								:to="item.path"
								class="nav-link"
								:class="{ active: isNavItemActive(item) }"
							>
								{{ item.title }}
							</router-link>
						</nav>
					</div>
				</div>

				<div class="main-view">
					<router-view />
					<div style="display: flex; height: 30px; width: 100%"></div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import MusicianNavBar from '@/components/frontend/MusicianNavBar.vue';
import { useAuthStore } from '@/stores/frontend/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const navItems = ref([
	{ title: '首页', path: `/musician/artist/hot?id=${authStore.artist.artistId}` },
	{ title: '作品管理', path: '/musician/artist/workManage' },
	{ title: '通知', path: '/musician/artist/notifications' },
	{ title: '账号设置', path: '/musician/artist/account-settings' },
]);

const publishWork = () => {
	router.push('/musician/artist/songUpload');
};

navItems.value[0].matchPaths = [
	'/musician/artist/hot',
	'/musician/artist/album',
	'/musician/artist/desc',
];

const isNavItemActive = (item) => {
	if (item.matchPaths) {
		return item.matchPaths.includes(route.path);
	}

	return route.path === item.path;
};
</script>

<style lang="scss" scoped>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

.artist-container {
	background: #f5f6fa;
	min-height: 100vh;
}

.app-container {
	width: 1200px;
	margin: 0 auto;
	background: #f5f5f5;
	padding: 10px;
	height: calc(100vh - 90px);
	box-sizing: border-box;
	display: flex;
	min-height: calc(100vh - 90px);
	overflow: hidden;
}

.main-content {
	width: 100%;
	display: flex;
	min-height: calc(100vh - 90px);
	overflow: hidden;

	.sidebar {
		width: 220px;
		background: #fff;
		margin-right: 12px;
		border-radius: 10px;
		display: flex;
		flex-direction: column;
		height: 100%;
		overflow: hidden;

		.sidebar-scroll {
			overflow-y: overlay;
			padding-right: 0;
			scrollbar-gutter: stable;

			&::-webkit-scrollbar {
				width: 5px;
				background: transparent;
			}

			&::-webkit-scrollbar-thumb {
				background: #c1c1c1;
				border-radius: 4px;
			}
		}

		.user-profile {
			padding: 20px;
			text-align: center;
			margin-bottom: 15px;
			flex-shrink: 0;

			.avatar-container {
				margin: 0 auto 15px;
				width: 80px;
				height: 80px;
				overflow: hidden;
				box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

				.user-avatar {
					width: 100%;
					height: 100%;
					object-fit: cover;
				}
			}

			.user-info {
				.nickname {
					margin-bottom: 12px;
					font-size: 16px;
					color: #333;
				}

				.publish-btn {
					width: 75%;
					padding: 8px 15px;
					background: #66b1ff;
					color: #fff;
					border: none;
					border-radius: 4px;
					cursor: pointer;
					transition: background 0.3s;
				}
			}
		}
	}

	.main-view {
		flex: 1;
		min-width: 0;
		background: #fff;
		overflow-y: auto;
		height: 100%;
		min-height: calc(100vh - 90px);
	}
}

.content-wrapper {
	flex: 1;
	padding: 30px;
}

.side-nav {
	padding: 20px 0;
}

.nav-link {
	display: block;
	padding: 14px 30px;
	color: #000;
	text-decoration: none;
	font-size: 15px;
	position: relative;
	transition: all 0.3s;

	&:hover {
		color: #000;
		background: rgba(180, 180, 180, 0.05);
	}

	&.active {
		color: #409eff;
		background: rgba(64, 158, 255, 0.1);

		&::after {
			content: '';
			position: absolute;
			right: 0;
			top: 50%;
			transform: translateY(-50%);
			width: 3px;
			height: 50%;
			background: #409eff;
			border-radius: 2px;
		}
	}
}

.admin-container * {
	all: initial;
	box-sizing: border-box;
	font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
