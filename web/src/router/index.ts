import { route } from 'quasar/wrappers'
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory
} from 'vue-router'

import routes from './routes'
import { useAuthStore } from 'stores/auth-store'
import { toRoute } from '@/hooks/toRoute'

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

export default route(() => {
  const isHistory = process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : isHistory

  const router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE)
  })
  router.beforeEach((to, _, next) => {
    const authStore = useAuthStore()
    const toLink = toRoute()
    if (!Object.prototype.hasOwnProperty.call(to.meta, 'auth')) {
      next()
      return
    }
    const auth = to.meta.auth as string[]
    if (auth.length === 0) {
      next()
      return
    }
    if (authStore.canAccess(auth)) {
      next()
      return
    }
    toLink.singleToHome()
  })
  return router
})
