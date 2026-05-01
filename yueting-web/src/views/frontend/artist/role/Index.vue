<template>
	<div class="container">
		<h2>请选择以下任一身份并完成认证</h2>
		<div class="cards-wrapper">
			<!-- 歌手卡片 -->
			<div class="role-card">
				<img
					class="card-img"
					src="https://p5.music.126.net/obj/wonDlsKUwrLClGjCm8Kx/11228807478/b1ca/962f/38aa/4b5d7d998b24bb7d0d94f7e9050987d3.png"
				/>
				<h3 class="card-title">我是歌手/唱作人</h3>
				<p class="card-desc">
					有演唱、演奏、电音制作、Remix等才能的歌手/创作音乐人，可通过上传歌曲作品，完成身份入驻。
				</p>
				<div v-if="authStore.artist.identities.includes('歌手')" class="activated-button">
					已开通
				</div>
				<router-link v-else to="/musician/artist/apply/role/singer" class="apply-button">
					立刻申请
				</router-link>
			</div>

			<!-- 作词/作曲卡片 -->
			<div class="role-card">
				<img
					class="card-img"
					src="https://p5.music.126.net/obj/wonDlsKUwrLClGjCm8Kx/11228805816/0147/a418/5a40/ea577c9297662ccd886e668cd29114b3.png"
				/>
				<h3 class="card-title">我是作词</h3>
				<p class="card-desc">有作词创作才能的作者，<br />可通过发布歌词，<br />完成身份入驻</p>
				<div v-if="authStore.artist.identities.includes('作词')" class="activated-button">
					已开通
				</div>
				<router-link v-else to="/musician/artist/apply/role/lyricist" class="apply-button">
					立刻申请
				</router-link>
			</div>

			<!-- 编曲卡片 -->
			<div class="role-card">
				<img
					class="card-img"
					src="https://p5.music.126.net/obj/wonDlsKUwrLClGjCm8Kx/11228806681/065e/9528/ffda/411385aa489a249eccbf695c2b56ea53.png"
				/>
				<h3 class="card-title">我是作曲</h3>
				<p class="card-desc">有作曲创作才能的作者，<br />可通过发布曲子，<br />完成身份入驻</p>
				<div v-if="authStore.artist.identities.includes('作曲')" class="activated-button">
					已开通
				</div>
				<router-link v-else to="/musician/artist/apply/role/composer" class="apply-button">
					立刻申请
				</router-link>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { useAuthStore } from '@/stores/frontend/auth';
	import { apiGetMyArtistBasic } from '@/api/frontend/artist';
	const authStore = useAuthStore();
	apiGetMyArtistBasic().then((res) => {
		if (res.data.code == 1) {
			authStore.updateArtist(res.data.data);
		}
	});
</script>

<style scoped>
	.container {
		background: white;
		padding: 40px 20px;
	}

	h2 {
		text-align: center;
		margin-bottom: 50px;
		color: #333;
		font-size: 24px;
		font-weight: 500;
	}

	.cards-wrapper {
		display: flex;
		gap: 30px;
		justify-content: center;
	}

	.role-card {
		flex: 1;
		max-width: 320px;
		background: #fff;
		border-radius: 12px;
		padding: 24px;
		border: 1px solid #ebedf0;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
		text-align: center;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.card-img {
		width: 160px;
		height: 160px;
		margin-bottom: 20px;
		object-fit: contain;
	}

	.card-title {
		color: #333;
		font-size: 18px;
		margin: 0 0 15px 0;
		font-weight: 600;
	}

	.card-desc {
		color: #666;
		font-size: 14px;
		line-height: 1.6;
		margin: 0 0 20px 0;
		flex-grow: 1;
	}

	.apply-button {
		display: inline-block;
		width: 140px;
		padding: 10px 20px;
		background: #3385ff;
		color: white;
		border-radius: 20px;
		text-decoration: none;
		font-size: 14px;
		transition: all 0.3s;
	}

	.apply-button:hover {
		background: #2a6dc9;
		transform: translateY(-2px);
		box-shadow: 0 4px 12px rgba(51, 133, 255, 0.3);
	}
</style>
