import request from './request';
import { toRaw } from 'vue';

/**
 * 分页查询角色列表
 */
export function apiPageRole(current, size) {
    return request.get(`/role?current=${current}&size=${size}`);
}

/**
 * 获取角色详情
 */
export function apiRoleDetail(id) {
    return request.get(`/role/${id}`);
}

/**
 * 创建角色
 */
export function apiCreateRole(form) {
    return request.post('/role', toRaw(form));
}

/**
 * 更新角色
 */
export function apiUpdateRole(form) {
    return request.put('/role', toRaw(form));
}

/**
 * 删除角色
 */
export function apiDeleteRole(id) {
    return request.delete(`/role/${id}`);
}

/**
 * 获取所有角色
 */
export function apiAllRoles() {
    return request.get('/role/all');
}

/**
 * 分配角色菜单
 */
export function apiAssignRoleMenus(roleId, menuIds) {
    return request.put(`/role/${roleId}/menus`, menuIds);
}

/**
 * 获取管理员的角色ID列表
 */
export function apiEmployeeRoles(employeeId) {
    return request.get(`/role/employee/${employeeId}`);
}

/**
 * 分配管理员角色
 */
export function apiAssignEmployeeRoles(form) {
    return request.put('/role/employee', toRaw(form));
}