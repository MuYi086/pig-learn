import { useUserStore } from '@/store/modules/user'

/**
 * 检查是否有权限
 * @param permissions 权限列表
 * @returns boolean
 */
export function hasPermission(permissions: string[]): boolean {
  const userStore = useUserStore()
  const userPermissions = userStore.permissions || []

  if (!permissions || permissions.length === 0) {
    return true
  }

  return permissions.some((permission) => userPermissions.includes(permission))
}

/**
 * 检查是否有角色
 * @param roles 角色列表
 * @returns boolean
 */
export function hasRole(roles: string[]): boolean {
  const userStore = useUserStore()
  const userRoles = userStore.roles || []

  if (!roles || roles.length === 0) {
    return true
  }

  return roles.some((role) => userRoles.includes(role))
}
