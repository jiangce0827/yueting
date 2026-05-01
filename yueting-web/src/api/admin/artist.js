import request from './request';
import { toRaw } from 'vue';


// 分页查询用户信息
export function apiPageArtist(form) {
    const params = toRaw(form)
    const searchParams = new URLSearchParams(params);

    return request.get(`/artist/pageArtist?${searchParams.toString()}`)
}
// 分页查询艺人审核
export function apiPageArtistApplication(pageForm) {
    // 将reactive对象转化为普通对象
    const params = toRaw(pageForm);
    // 创建URLSearchParams实例并将params传递进去
    const searchParams = new URLSearchParams(params);
    return request.get(`/artist/pageArtistApplication?${searchParams.toString()}`);
}


//艺人申请通过
export function apiApproveArtist(id) {
    return request.post(`/artist/apply/artist/${id}/approve`);
}

//艺人申请不通过
export function apiRejectArtist(id,reason) {
    const params = new URLSearchParams();
    params.append('rejectionReason',reason);
    return request.post(`/artist/apply//artist/${id}/reject`,params);
}

// 分页查询艺人具体身份审核
export function apiPageArtistIdentityApplication(pageForm) {
    // 将reactive对象转化为普通对象
    const params = toRaw(pageForm);
    // 创建URLSearchParams实例并将params传递进去
    const searchParams = new URLSearchParams(params);
    return request.get(`/artist/pageArtistIdentityApplication?${searchParams.toString()}`);
}


//艺人具体身份申请通过
export function apiApproveArtistIdentity(id) {
    return request.post(`/artist/apply/artistIdentity/${id}/approve`);
}

//艺人具体身份申请不通过
export function apiRejectArtistIdentity(id,reason) {
    const params = new URLSearchParams();
    params.append('rejectionReason',reason);
    return request.post(`/artist/apply//artistIdentity/${id}/reject`,params);
}
