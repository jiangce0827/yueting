import request from './request';

export const apiGetArtistNotificationList = () => {
	return request.get('/artist-notification/list');
};

export const apiGetArtistNotificationUnreadCount = () => {
	return request.get('/artist-notification/unread-count');
};

export const apiMarkArtistNotificationRead = (notificationId) => {
	return request.post(`/artist-notification/read/${notificationId}`);
};

export const apiMarkAllArtistNotificationsRead = () => {
	return request.post('/artist-notification/read-all');
};
