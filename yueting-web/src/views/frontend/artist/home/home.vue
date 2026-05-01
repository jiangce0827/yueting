<template>
	<div class="artist-container">
		<div class="left-panel">
			<div class="left-content">
				<h1 class="artist-name">
					{{ authStore.artist.artistName }}
					<roleShow :identities="authStore.artist.identities" />
				</h1>
				<div class="banner-wrapper">
					<div
						class="artist-banner"
						:style="{ backgroundImage: `url(${authStore.artist.artistCoverUrl})` }"
					>
						<router-link to="/musician/artist/info/change" class="edit-btn">编辑</router-link>
					</div>
				</div>

				<div class="tab-nav">
					<router-link
						v-for="tab in tabs"
						:key="tab.path"
						:to="tab.path"
						class="tab-item"
						exact-active-class="active"
					>
						{{ tab.label }}
					</router-link>
				</div>
				<div class="sub-view">
					<router-view />
				</div>
			</div>
		</div>

		<div class="right-panel">
			<div class="button-group">
				<button
					v-for="action in quickActions"
					:key="action.label"
					class="action-btn"
					@click="navigate(action.path)"
				>
					{{ action.label }}
				</button>
			</div>
		</div>
	</div>
</template>

<script setup>
import { apiGetMyArtistBasic } from '@/api/frontend/artist';
import roleShow from '@/components/frontend/roleShow.vue';
import { useAuthStore } from '@/stores/frontend/auth';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const tabs = [
	{ path: `/musician/artist/hot?id=${authStore.artist.artistId}`, label: '热门作品' },
	{ path: `/musician/artist/album?id=${authStore.artist.artistId}`, label: '全部专辑' },
	{ path: `/musician/artist/desc?id=${authStore.artist.artistId}`, label: '艺人介绍' },
];

const quickActions = [
	{ label: '作品管理', path: '/musician/artist/workManage' },
	{ label: '账号设置', path: '/musician/artist/account-settings' },
	{ label: '开通身份', path: '/musician/artist/apply/role' },
];

if (!route.query.id) {
	router.push(`/musician/artist/hot?id=${authStore.artist.artistId}`);
}

apiGetMyArtistBasic().then((res) => {
	if (res.data.code === 1) {
		authStore.updateArtist(res.data.data);
	}
});

const navigate = (path) => {
	router.push(path);
};
</script>

<style scoped>
	.artist-container {
		display: flex;
		gap: 24px;
		align-items: flex-start;
		min-height: 100%;
		padding: 24px;
		background: #f6f7fb;
	}

	.left-panel {
		padding: 0;
		flex: 1;
		min-width: 0;
		background: white;
		border: 1px solid #eef0f4;
		border-radius: 12px;
		box-shadow: 0 12px 30px rgba(31, 41, 55, 0.06);
		overflow: hidden;
	}

	.left-content {
		margin: 0 auto;
		padding: 0 32px 32px;
	}

	.artist-name {
		display: flex;
		align-items: center;
		gap: 12px;
		padding: 28px 0 20px;
		margin: 0;
		font-size: 32px;
		font-weight: 700;
		color: #1f2937;
		letter-spacing: 0;
	}

	.banner-wrapper {
		position: relative;
		height: 320px;
		border-radius: 12px;
		overflow: hidden;
		background: #f0f2f5;
	}

	.artist-banner {
		height: 100%;
		width: 100%;
		background-size: cover;
		background-position: center;
		position: relative;
	}

	.artist-banner::after {
		content: '';
		position: absolute;
		inset: 0;
		background: linear-gradient(180deg, rgba(0, 0, 0, 0.02), rgba(0, 0, 0, 0.28));
		pointer-events: none;
	}

	.edit-btn {
		position: absolute;
		bottom: 20px;
		right: 20px;
		z-index: 1;
		padding: 9px 18px;
		background: rgba(255, 255, 255, 0.92);
		border: 1px solid rgba(255, 255, 255, 0.72);
		border-radius: 8px;
		color: #1f2937;
		text-decoration: none;
		font-weight: 600;
		box-shadow: 0 10px 24px rgba(0, 0, 0, 0.16);
		backdrop-filter: blur(8px);
		transition: transform 0.2s ease, background 0.2s ease;
	}

	.edit-btn:hover {
		transform: translateY(-1px);
		background: #fff;
	}

	.tab-nav {
		display: flex;
		gap: 8px;
		margin: 24px 0 18px;
		padding: 6px;
		background: #f3f4f6;
		border: 1px solid #eceff3;
		border-radius: 10px;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 12px 16px;
		cursor: pointer;
		border-radius: 8px;
		transition: color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
		color: #667085;
		text-decoration: none;
		font-size: 15px;
		font-weight: 600;
		line-height: 1.4;
	}

	.tab-item:hover {
		color: #ec4141;
		background: rgba(255, 255, 255, 0.7);
	}

	.tab-item.active {
		background: #fff;
		color: #ec4141;
		box-shadow: 0 8px 20px rgba(31, 41, 55, 0.08);
	}

	.sub-view {
		padding-top: 4px;
	}

	.bio-content {
		text-indent: 2em;
		line-height: 1.8;
		color: #666;
		margin-bottom: 30px;
	}

	.detail-content {
		line-height: 1.8;
		color: #444;
	}

	.right-panel {
		width: 220px;
		flex: 0 0 220px;
		position: sticky;
		top: 24px;
	}

	.button-group {
		display: flex;
		flex-direction: column;
		gap: 12px;
		padding: 20px;
		background: white;
		border: 1px solid #eef0f4;
		border-radius: 12px;
		box-shadow: 0 12px 30px rgba(31, 41, 55, 0.06);
	}

	.action-btn {
		width: 100%;
		padding: 13px 16px;
		background: #fff;
		border: 1px solid #edf0f5;
		border-radius: 8px;
		color: #344054;
		text-align: left;
		cursor: pointer;
		font-weight: 600;
		transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, color 0.2s ease;
	}

	.action-btn:hover {
		color: #ec4141;
		border-color: #ffd4d4;
		background: #fffafa;
		transform: translateY(-2px);
		box-shadow: 0 10px 20px rgba(236, 65, 65, 0.08);
	}

	@media (max-width: 1100px) {
		.artist-container {
			flex-direction: column;
		}

		.right-panel {
			width: 100%;
			flex-basis: auto;
			position: static;
		}

		.button-group {
			flex-direction: row;
		}
	}

	@media (max-width: 720px) {
		.artist-container {
			padding: 12px;
		}

		.left-content {
			padding: 0 16px 20px;
		}

		.artist-name {
			font-size: 24px;
		}

		.banner-wrapper {
			height: 220px;
		}

		.tab-nav,
		.button-group {
			flex-direction: column;
		}
	}
</style>
