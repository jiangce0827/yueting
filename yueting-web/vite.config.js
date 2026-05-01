import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
export default defineConfig({
	base: '/',
	plugins: [vue()],
	server: {
		host: '0.0.0.0',
		port: 80,
		proxy: {
			'/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
				rewrite: (path) => path.replace(/^\/api/, ''),
			},
			'/ws': {
				target: 'http://localhost:8080',
				changeOrigin: true,
				ws: true,
			},
		},
	},
	resolve: {
		alias: {
			'@': path.resolve(__dirname, 'src'),
		},
	},
	define: {
		global: 'globalThis',
	},
});
