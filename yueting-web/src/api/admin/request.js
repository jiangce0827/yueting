// @/api/frontend/request.js
import axios from 'axios';
import { useAdminStore } from '@/stores/admin/admin';
const request = axios.create({
	baseURL: '/api/admin', //基础url
	timeout: 5000,
});

request.interceptors.request.use((config) => {
	const adminStore = useAdminStore();	
	if (adminStore.empToken) {		
		config.headers['Authorization'] = `Bearer ${adminStore.empToken}`; // 携带token
	}
	
	return config;
});

request.interceptors.response.use((response) => {
	if (response.data.code === 401) {
		useAdminStore().logout();
		window.location.reload();
	}
	return response;
});

export default request;
