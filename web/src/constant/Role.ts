export enum Role {
  ROLE_ADMIN = 'ROLE_ADMIN',
  ROLE_STUDENT = 'ROLE_STUDENT',
  ROLE_TEACHER = 'ROLE_TEACHER',
  NEED_AUTH = 'NEED_AUTH'
}

/**
 * 是否有期待的角色
 * @param roles 角色
 * @param expect 期待的角色
 */
export const hasRole = (roles: string[], expect: Role): boolean =>
  roles && roles.findIndex(role => role as Role === expect) !== -1

/**
 * 是否是管理员
 * @param roles 角色
 */
export const hasAdmin = (roles: string[]): boolean =>
  roles && roles.findIndex(role => role as Role === Role.ROLE_ADMIN) !== -1

/**
 * 是否是学生
 * @param roles 角色
 */
export const hasStudent = (roles: string[]): boolean =>
  roles && roles.findIndex(role => role as Role === Role.ROLE_STUDENT) !== -1

/**
 * 是否是教师
 * @param roles 角色
 */
export const hasTeacher = (roles: string[]): boolean =>
  roles && roles.findIndex(role => role as Role === Role.ROLE_TEACHER) !== -1

/**
 * 拥有的橘色数量
 * @param roles 角色
 */
export const hasRoleCount = (roles: string[]): number => {
  let count = 0
  for (const roleKey in Role) {
    if (hasRole(roles, roleKey as Role)) {
      count++
    }
  }
  return count
}
