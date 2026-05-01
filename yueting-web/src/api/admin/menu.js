import request from './request';
import { toRaw } from 'vue';

/**
 * 分页查询菜单列表
 */
export function apiPageMenu(form) {
    const params = toRaw(form);
    const searchParams = new URLSearchParams(params);
    return request.get(`/menu?${searchParams.toString()}`);
}

/**
 * 获取菜单树形结构
 */
export function apiMenuTree() {
    return request.get('/menu/tree');
}

/**
 * 获取菜单详情
 */
export function apiMenuDetail(id) {
    return request.get(`/menu/${id}`);
}

/**
 * 创建菜单
 */
export function apiCreateMenu(form) {
    return request.post('/menu', toRaw(form));
}

/**
 * 更新菜单
 */
export function apiUpdateMenu(form) {
    return request.put('/menu', toRaw(form));
}

/**
 * 删除菜单
 */
export function apiDeleteMenu(id) {
    return request.delete(`/menu/${id}`);
}

/**
 * 获取父级菜单列表
 */
export function apiMenuParents() {
    return request.get('/menu/parents');
}
