import { defineStore } from 'pinia'
import { login } from 'api/auth'

export const useAuthStore = defineStore({
  id: 'auth',
  state: () => ({
    accessToken: null
  }),
  actions: {
    async login (user: string, password: string) {
      const response = await login(user, password)
      this.accessToken = response.accessToken
    }
  },
  persist: true
})
