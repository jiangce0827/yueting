import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/frontend/auth';
import { useAdminStore } from '../stores/admin/admin';
import { apiGetMyArtistBasic } from '@/api/frontend/artist';
import { apiGetToken } from '@/api/frontend/auth';
//前台路由
const frontendRoutes = [
	{
		path: '/musician/artist',
		meta: {
			requiresAuth: true,
		},
		redirect: `/musician/artist/hot`,
		component: () => import('../views/frontend/artist/Index.vue'),
		children: [
			{
				path: '/musician/artist/hot',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
					requiresArtistActive: true,
				},
				component: () => import('../views/frontend/artist/home/home.vue'),
				children: [
					{
						path: '',
						component: () => import('../views/frontend/artist/home/hot.vue'),
					},
					{
						path: '/musician/artist/album',
						component: () => import('../views/frontend/artist/home/ArtistAlbums.vue'),
					},
					{
						path: '/musician/artist/desc',
						component: () => import('../views/frontend/user/artist/desc.vue'),
					},
				],
			},
			{
				path: '/musician/artist/album/:albumId',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
					requiresArtistActive: true,
				},
				component: () => import('../views/frontend/artist/home/ArtistAlbum.vue'),
			},
			{
				path: '/musician/artist/album/edit/:albumId',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
					requiresArtistActive: true,
				},
				component: () => import('../views/frontend/artist/home/ArtistEditAlbum.vue'),
			},
			{
				path: '/musician/artist/workManage',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
					requiresArtistActive: true,
				},
				component: () => import('../views/frontend/artist/workManage.vue'),
			},
			{
				path: '/musician/artist/songUpload',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
				},
				component: () => import('../views/frontend/artist/songUpload.vue'),
			},
			{
				path: '/musician/artist/apply/role',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/artist/role/Index.vue'),
			},
			{
				path: '/musician/artist/apply/role/singer',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/artist/role/singer.vue'),
			},
			{
				path: '/musician/artist/apply/role/lyricist',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/artist/role/lyricist.vue'),
			},
			{
				path: '/musician/artist/apply/role/composer',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/artist/role/composer.vue'),
			},
			{
				path: '/musician/artist/info/change',
				meta: {
					requiresArtist: true,
					requiresArtistActive: true,
				},
				component: () => import('../views/frontend/artist/home/changeArtistInfo.vue'),
			},
			{
				path: '/musician/artist/account-settings',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
				},
				component: () => import('../views/frontend/artist/accountSettings.vue'),
			},
			{
				path: '/musician/artist/notifications',
				meta: {
					requiresAuth: true,
					requiresArtist: true,
				},
				component: () => import('../views/frontend/artist/Notifications.vue'),
			},
		],
	},
	{
		path: '/musician/artist/apply/info',
		meta: {
			requiresAuth: true,
		},
		component: () => import('../views/frontend/artist/apply/info.vue'),
	},

	// 用户路由
	{
		path: '/',
		component: () => import('../views/frontend/user/Index.vue'),
		redirect: '/discover',
		children: [
			{
				path: '/discover',
				component: () => import('../views/frontend/user/Discover.vue'),
				children: [
					{
						path: '/discover',
						component: () => import('../views/frontend/user/discover/home.vue'),
					},
					{
						path: '/discover/topList',
						component: () => import('../views/frontend/user/discover/topList.vue'),
					},
					{
						path: '/discover/playlist',
						component: () => import('../views/frontend/user/discover/playList.vue'),
					},
					{
						path: '/discover/artist',
						component: () => import('../views/frontend/user/discover/artist.vue'),
					},
					{
						path: '/artist',
						component: () => import('../views/frontend/user/artist/index.vue'),
						children: [
							{
								path: '/artist',
								component: () => import('../views/frontend/user/artist/hot.vue'),
							},
							{
								path: '/artist/album',
								component: () => import('../views/frontend/user/artist/album.vue'),
							},
							{
								path: '/artist/desc',
								component: () => import('../views/frontend/user/artist/desc.vue'),
							},
						],
					},
					{
						path: '/album',
						component: () => import('../views/frontend/user/album/index.vue'),
					},
					{
						path: '/song',
						component: () => import('../views/frontend/user/song/index.vue'),
					},
					{
						path: '/playlist',
						component: () => import('../views/frontend/user/playlist/index.vue'),
					},
				],
			},
			{
				path: '/my',
				meta: {
					requiresAuth: true,
				},
				redirect: '/my/playlist',
				component: () => import('../views/frontend/user/MyMusic.vue'),
				children: [
					{
						path: '/my/playlist',
						component: () => import('../views/frontend/user/myMusic/playlist.vue'),
					},
					{
						path: '/my/edit',
						component: () => import('../views/frontend/user/myMusic/edit.vue'),
					},
				],
			},
			{
				path: '/my/play-history',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/myMusic/PlayHistory.vue'),
			},
			{
				path: '/follow',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/Follow.vue'),
			},
			{
				path: '/ai-chat',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/AIChat.vue'),
			},
			{
				path: '/my-notes',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/MyNotes.vue'),
			},
			{
				path: '/my-following',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/MyFollowing.vue'),
			},
			{
				path: '/my-followers',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/MyFollowers.vue'),
			},
			{
				path: '/search',
				component: () => import('../views/frontend/user/search/index.vue'),
			},
			{
				path: '/user/home',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/home/Home.vue'),
			},
			{
				path: '/user/message',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/Message.vue'),
			},
			{
				path: '/user/setting',
				meta: {
					requiresAuth: true,
				},
				component: () => import('../views/frontend/user/settings/Index.vue'),
				children: [
					{
						path: '',
						meta: {
							requiresAuth: true,
						},
						redirect: '/user/basic',
					},
					{
						path: '/user/basic',
						meta: {
							requiresAuth: true,
						},
						component: () => import('../views/frontend/user/settings/Basic.vue'),
					},
					{
						path: '/user/binding',
						meta: {
							requiresAuth: true,
						},
						component: () => import('../views/frontend/user/settings/Binding.vue'),
					},
					{
						path: '/user/privacy',
						meta: {
							requiresAuth: true,
						},
						component: () => import('../views/frontend/user/settings/Privacy.vue'),
					},
				],
			},
		],
	},
	{
		path: '/register',
		component: () => import('@/views/frontend/user/Register.vue'),
	},
	{
		path: '/login',
		component: () => import('@/views/frontend/user/Login.vue'),
	},
];

// 后台路由
const adminRoutes = [
	{
		path: '/admin',
		redirect: '/admin/dashboard',
		component: () => import('../views/admin/AdminLayout.vue'),
		meta: { requiresEmp: true },
		children: [
			{
				path: 'dashboard',
				component: () => import('../views/admin/system/Dashboard.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'user-management',
				component: () => import('../views/admin/UserManagement.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'artist-management',
				component: () => import('../views/admin/ArtistManagement.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'song-management',
				component: () => import('../views/admin/SongManagement.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'song-review',
				component: () => import('../views/admin/SongReview.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'artist-review',
				component: () => import('../views/admin/ArtistReview.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'artistIdentity-review',
				component: () => import('../views/admin/ArtistIdentityReview.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'banner-management',
				component: () => import('../views/admin/BannerManagement.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'menu-management',
				component: () => import('../views/admin/system/MenuManagement.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'role-management',
				component: () => import('../views/admin/RoleManagement.vue'),
				meta: { requiresEmp: true },
			},
			{
				path: 'employee-management',
				component: () => import('../views/admin/system/EmployeeManagement.vue'),
				meta: { requiresEmp: true },
			},
		],
	},
	{
		path: '/admin/login',
		component: () => import('../views/admin/AdminLogin.vue'),
	},
];

const router = createRouter({
	history: createWebHistory(),
	routes: [...frontendRoutes, ...adminRoutes],
});

router.beforeEach(async (to) => {
	const authStore = useAuthStore();
	// 用户必须登录
	if (to.meta.requiresAuth && !authStore.isLoggedIn) {
		return { path: '/login', query: { redirect: to.fullPath } };
	}
	// 必须实名艺人身份
	if (to.meta.requiresArtist && !authStore.isArtist) {
		const tokenRes = await apiGetToken();
		if (tokenRes.data.code == 1) {
			await authStore.parseJWT(tokenRes.data.data);
			if (!authStore.isArtist) {
				return { path: '/musician/artist/apply/info', query: { redirect: to.fullPath } };
			}
		} else {
			return { path: '/musician/artist/apply/info', query: { redirect: to.fullPath } };
		}
	}
	// 必须激活艺人身份
	if (to.meta.requiresArtistActive && !authStore.isArtistActive) {
		// 先同步检查本地缓存
		if (!authStore.isArtistActive) {
			// 调用 API 获取最新状态
			const res = await apiGetMyArtistBasic();
			if (res.data.code === 1) {
				await authStore.updateArtist(res.data.data);
				// 更新后再次检查
				if (!authStore.isArtistActive) {
					return { path: '/musician/artist/apply/role' };
				}
			} else {
				return { path: '/musician/artist/apply/role' };
			}
		}
	}
	// 必须是管理人员
	const adminStore = useAdminStore();
	if (to.meta.requiresEmp && !adminStore.isLoggedIn) {
		return { path: '/admin/login', query: { redirect: to.fullPath } };
	}
});

export default router;
