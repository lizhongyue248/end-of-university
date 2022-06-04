import { useAuthStore } from 'stores/auth-store'
import { useRouter } from 'vue-router'
import { Admin, Auth, Home, Student, Teacher } from '@/constant/Routes'

export const toRoute = () => {
  const authStore = useAuthStore()
  const router = useRouter()
  const logout = async () => {
    authStore.logout()
    await router.push({ name: Auth.LOGIN })
  }
  const singleToHome = () => {
    if (authStore.hasAdmin) {
      void router.push({ name: Admin.HOME })
    } else if (authStore.hasTeacher) {
      void router.push({ name: Teacher.HOME })
    } else if (authStore.hasStudent) {
      void router.push({ name: Student.HOME })
    } else {
      void router.push({ name: Home.HOME })
    }
  }
  return {
    logout,
    singleToHome
  }
}
