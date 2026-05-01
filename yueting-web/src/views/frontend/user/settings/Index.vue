<!-- @/views/frontend/user/settings/Index.vue -->
<template>
	<div class="settings-container">
		<h1>个人设置</h1>
		<nav class="settings-nav">
			<router-link to="/user/basic">基本设置</router-link>
			<router-link to="/user/binding">绑定设置</router-link>
			<router-link to="/user/privacy">隐私设置</router-link>
		</nav>
		<div class="settings-content">
			<router-view />
		</div>
	</div>
</template>

<style scoped>
	.settings-container {
		max-width: 1000px;
		margin: 0 auto;
		padding: 20px;
		background: white;
	}

	.settings-nav {
		display: flex;
		gap: 20px;
		margin: 20px 0;
		border-bottom: 1px solid #eee;
		background: #f7f7f7;
	}

	.settings-nav a {
		padding: 8px 16px;
		border-radius: 4px;
		color: #666;
		text-decoration: none;
		transition: all 0.3s;
	}

	.settings-nav a.router-link-exact-active {
		background: #ec4141;
		color: white;
	}

	.settings-content {
		background: white;
		border-radius: 8px;
	}
</style>
