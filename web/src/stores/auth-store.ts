import { defineStore } from 'pinia'
import { login } from 'api/auth'
import { hasAdmin, hasRoleCount, hasStudent, hasTeacher, Role } from '@/constant/Role'

export const useAuthStore = defineStore({
  id: 'auth',
  state: () => ({
    accessToken: null,
    roles: [],
    currentRole: null,
    config: {
      rememberRole: null
    }
  }),
  getters: {
    hasAdmin: ({ roles }) => hasAdmin(roles),
    hasStudent: ({ roles }) => hasStudent(roles),
    hasTeacher: ({ roles }) => hasTeacher(roles),
    hasMultipleRole: ({ roles }) => hasRoleCount(roles) > 1,
    isAuth: ({ accessToken, roles }) => accessToken !== null && roles.length > 0,
    hasRole: ({ roles }) => hasRoleCount(roles) > 0
  },
  actions: {
    async login (user: string, password: string, remember: boolean) {
      const response = await login(user, password, remember)
      this.accessToken = response.accessToken
      this.roles = response.roles
      return response.roles
    },
    logout () {
      this.$reset()
    },
    canAccess (checkRoles: string[]) {
      if (checkRoles.includes(Role.NEED_AUTH)) {
        return true
      }
      for (const role of checkRoles) {
        if (this.roles && this.roles.includes(role as never)) {
          return true
        }
      }
      return false
    }
  },
  persist: true
})
