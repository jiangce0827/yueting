// @/stores/admin/auth.js
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
export const useAdminStore = defineStore('admin', () => {
	// 状态
	const empToken = ref(localStorage.getItem('empToken') || null);
	const empTokenExp = ref(localStorage.getItem('empTokenExp') || null);
	const emp = ref(JSON.parse(localStorage.getItem('emp') || null));
	const menus = ref(JSON.parse(localStorage.getItem('menus') || '[]'));

	// 操作
	const login = (responseData) => {
		parseJWT(responseData.empToken);

		emp.value = {
			employeeId: responseData.employeeId,
			realName: responseData.realName,
		};
		menus.value = responseData.menus || [];

		// 持久化存储
		localStorage.setItem('emp', JSON.stringify(emp.value));
		localStorage.setItem('menus', JSON.stringify(menus.value));
	};
	const logout = () => {
		empTokenExp.value = null;
		empToken.value = null;
		emp.value = null;
		menus.value = [];
		localStorage.removeItem('empToken');
		localStorage.removeItem('empTokenExp');
		localStorage.removeItem('emp');
		localStorage.removeItem('menus');
	};

	const updateEmp = (responseData) => {
		emp.value = {
			empId: responseData.empId,
		};
		localStorage.setItem('emp', JSON.stringify(emp.value));
	};

	function checkToken() {
		if (empToken.value && Date.now() > empTokenExp.value) {
			logout();
		}
	}

	// 定时检测令牌过期
	const checkTokenExpirationInterval = () => {
		checkToken();
		setInterval(() => {
			checkToken();
		}, 60 * 1000); // 每分钟检查一次
	};

	const parseJWT = (jwt) => {
		empToken.value = jwt;
		const decoded = jwtDecode(jwt);
		empTokenExp.value = decoded.exp * 1000;
		localStorage.setItem('empToken', jwt);
		localStorage.setItem('empTokenExp', empTokenExp.value); // 存储过期时间
	};

	// 在 Store 初始化时启动定时器检测令牌是否过期
	checkTokenExpirationInterval();

	// 计算属性
	const isLoggedIn = ref(computed(() => !!empToken.value));
	return { empToken, emp, menus, login, logout, updateEmp, isLoggedIn, parseJWT };
});
