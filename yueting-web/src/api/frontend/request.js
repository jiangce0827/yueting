// @/api/frontend/request.js
import axios from 'axios';
import { useAuthStore } from '@/stores/frontend/auth';
const request = axios.create({
	baseURL: '/api/web', //基础url
	timeout: 5000,
});

request.interceptors.request.use((config) => {
	const authStore = useAuthStore();	
	if (authStore.token) {		
		config.headers['Authorization'] = `Bearer ${authStore.token}`; // 携带token
	}
	
	return config;
});

request.interceptors.response.use((response) => {
	if (response.data.code === 401) {
		useAuthStore().logout();
		window.location.reload();
	}
	return response;
});

export default request;
