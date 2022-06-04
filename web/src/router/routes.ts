import { RouteRecordRaw } from 'vue-router'
import { Admin, Auth, Home, Page, Student, Teacher } from '@/constant/Routes'
import { Role } from '@/constant/Role'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', name: Home.HOME, component: () => import('pages/IndexPage.vue') }]
  },
  {
    path: '/auth',
    component: () => import('layouts/AuthLayout.vue'),
    children: [
      { path: 'role', name: Auth.ROLE, component: () => import('pages/auth/AuthRole.vue'), meta: { tip: '请选择你的角色', auth: [Role.NEED_AUTH] } },
      { path: 'login', name: Auth.LOGIN, component: () => import('pages/auth/AuthLogin.vue'), meta: { tip: '请输入账号密码' } },
      { path: 'register', name: Auth.REGISTER, component: () => import('pages/auth/AuthRegister.vue'), meta: { tip: '请填写注册信息' } }
    ]
  },
  {
    path: '/admin/home',
    name: Admin.HOME,
    component: () => import('pages/admin/AdminHome.vue'),
    meta: { auth: [Role.ROLE_ADMIN] }
  },
  {
    path: '/student/home',
    name: Student.HOME,
    component: () => import('pages/student/StudentHome.vue'),
    meta: { auth: [Role.ROLE_STUDENT] }
  },
  {
    path: '/teacher/home',
    name: Teacher.HOME,
    component: () => import('pages/teacher/TeacherHome.vue'),
    meta: { auth: [Role.ROLE_TEACHER] }
  },
  {
    path: '/:catchAll(.*)*',
    name: Page.NOT_FOUND,
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
