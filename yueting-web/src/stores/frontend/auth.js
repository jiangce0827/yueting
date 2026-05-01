// @/stores/frontend/auth.js
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
export const useAuthStore = defineStore('auth', () => {
	// 状态
	const token = ref(localStorage.getItem('token') || null);
	const tokenExp = ref(localStorage.getItem('tokenExp') || null);
	const user = ref(JSON.parse(localStorage.getItem('user') || null));
	const artist = ref(JSON.parse(localStorage.getItem('artist') || null));
	const role = ref(localStorage.getItem('role') || null);

	// 操作
	const login = (responseData) => {
		parseJWT(responseData.token);

		user.value = {
			userId: responseData.userId,
			email: responseData.email,
			passwordStatus: responseData.passwordStatus,
			nickname: responseData.nickname,
			avatarUrl: responseData.avatarUrl,
			gender: responseData.gender,
			signature: responseData.signature,
			listenCount: responseData.listenCount,
			experience: responseData.experience,
			followingCount: responseData.followingCount,
			followerCount: responseData.followerCount,
			vip: responseData.vip,
			vipExpireAt: responseData.vipExpireAt,
			birthday: responseData.birthday,
			userType: responseData.userType,
			privacyAllowMessages: responseData.privacyAllowMessages,
			privacyShowListeningHabits: responseData.privacyShowListeningHabits,
		};

		// 持久化存储
		localStorage.setItem('user', JSON.stringify(user.value));
	};
	const logout = () => {
		tokenExp.value = null;
		token.value = null;
		user.value = null;
		localStorage.removeItem('token');
		localStorage.removeItem('tokenExp');
		localStorage.removeItem('user');
		localStorage.removeItem('artist');
		localStorage.removeItem('role');
	};

	const updateUser = (responseData) => {
		user.value = {
			userId: responseData.userId,
			email: responseData.email,
			passwordStatus: responseData.passwordStatus,
			nickname: responseData.nickname,
			avatarUrl: responseData.avatarUrl,
			gender: responseData.gender,
			signature: responseData.signature,
			listenCount: responseData.listenCount,
			experience: responseData.experience,
			followingCount: responseData.followingCount,
			followerCount: responseData.followerCount,
			vip: responseData.vip,
			vipExpireAt: responseData.vipExpireAt,
			birthday: responseData.birthday,
			userType: responseData.userType,
			privacyAllowMessages: responseData.privacyAllowMessages,
			privacyShowListeningHabits: responseData.privacyShowListeningHabits,
		};
		localStorage.setItem('user', JSON.stringify(user.value));
	};

	const updateArtist = (responseData) => {
		artist.value = {
			artistId: responseData.artistId,
			artistName: responseData.artistName,
			artistAvatarUrl: responseData.artistAvatarUrl,
			artistCoverUrl: responseData.artistCoverUrl,
			gender: responseData.gender,
			language: responseData.language,
			artistBio: responseData.artistBio,
			artistDescription: responseData.artistDescription,
			identities: responseData.identities,
		};
		localStorage.setItem('artist', JSON.stringify(artist.value));
	};

	function checkToken() {
		if (token.value && Date.now() > tokenExp.value) {
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
		token.value = jwt;
		const decoded = jwtDecode(jwt);
		tokenExp.value = decoded.exp * 1000;
		role.value = decoded.authorities;
		localStorage.setItem('token', jwt);
		localStorage.setItem('tokenExp', tokenExp.value); // 存储过期时间
		localStorage.setItem('role', role.value);
	};

	// 在 Store 初始化时启动定时器检测令牌是否过期
	checkTokenExpirationInterval();

	// 计算属性
	const isLoggedIn = ref(computed(() => !!token.value));
	const isArtist = ref(computed(() => role.value.includes('ROLE_ARTIST')));
	//identities是否有值
	const isArtistActive = ref(computed(() => artist?.value?.identities?.length > 0))
	return { token, user, login, logout, updateUser, updateArtist, isLoggedIn, artist,isArtist,isArtistActive,parseJWT };
});
