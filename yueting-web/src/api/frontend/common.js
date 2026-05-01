// @/api/common/file.js
import request from './request';


export function apiUploadImage(form) {
    return request.post('/common/upload/image', form, {
		headers: {
			'Content-Type': 'multipart/form-data',
		},
		timeout:30000
	});
}

export function apiUploadMusicFile(form) {
    return request.post('/common/upload/musicFile', form, {
		headers: {
			'Content-Type': 'multipart/form-data',
		},
		timeout:30000
	});
}