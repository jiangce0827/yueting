import request from './request';
import { toRaw } from 'vue';


// 分页查询用户信息
export function apiPageUser(form) {
    const params = toRaw(form)
    const searchParams = new URLSearchParams(params);

    return request.get(`/user/pageUser?${searchParams.toString()}`)
}

//更新用户信息
export function apiUpdateUser(form) {
    return request.post('/user/updateUser', form);
}