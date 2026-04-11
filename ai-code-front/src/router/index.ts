import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { message } from 'ant-design-vue'

// 权限枚举
const ACCESS_ENUM = {
  NOT_LOGIN: 'notLogin',
  USER: 'user',
  ADMIN: 'admin',
} as const

type AccessEnumType = (typeof ACCESS_ENUM)[keyof typeof ACCESS_ENUM]

// 页面组件
import HomePage from '../pages/HomePage.vue'
import NoAuth from '../pages/NoAuth.vue'

// 检查权限
const checkAccess = (userRole: number | undefined, needAccess: AccessEnumType): boolean => {
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) return true
  if (userRole === undefined || userRole === null) return false

  const loginUserAccess: AccessEnumType = userRole === 1 ? ACCESS_ENUM.ADMIN : ACCESS_ENUM.USER

  if (needAccess === ACCESS_ENUM.USER) {
    return loginUserAccess !== ACCESS_ENUM.NOT_LOGIN
  }
  if (needAccess === ACCESS_ENUM.ADMIN) {
    return loginUserAccess === ACCESS_ENUM.ADMIN
  }
  return true
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 无权限页面（公开访问）
    {
      path: '/noAuth',
      name: 'noAuth',
      component: NoAuth,
      meta: { access: ACCESS_ENUM.NOT_LOGIN, hideInMenu: true },
    },
    // 首页（公开访问）
    {
      path: '/',
      name: 'home',
      component: HomePage,
      meta: { access: ACCESS_ENUM.NOT_LOGIN, title: '首页' },
    },
    // 登录页（公开访问）
    {
      path: '/user/login',
      name: 'login',
      component: () => import('../pages/user/UserLoginPage.vue'),
      meta: { access: ACCESS_ENUM.NOT_LOGIN, hideInMenu: true },
    },
    // 注册页（公开访问）
    {
      path: '/user/register',
      name: 'register',
      component: () => import('../pages/user/UserRegisterPage.vue'),
      meta: { access: ACCESS_ENUM.NOT_LOGIN, hideInMenu: true },
    },
    // 个人中心（需登录）
    {
      path: '/user/info',
      name: 'userInfo',
      component: () => import('../pages/user/UserInfoPage.vue'),
      meta: { access: ACCESS_ENUM.USER, title: '个人中心' },
    },
    // 用户管理（需管理员权限）
    {
      path: '/admin/userManage',
      name: 'adminUserManage',
      component: () => import('../pages/admin/UserManagePage.vue'),
      meta: { access: ACCESS_ENUM.ADMIN, title: '用户管理' },
    },
  ] as RouteRecordRaw[],
})

// 是否为首次获取登录用户
let firstFetchLoginUser = true

// 全局路由守卫 - 权限校验
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser

  // 自动登录逻辑：首次进入页面时，自动获取登录状态
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }

  // 获取目标页面需要的权限
  const needAccess: AccessEnumType = (to.meta?.access as AccessEnumType) ?? ACCESS_ENUM.NOT_LOGIN

  // 权限拦截逻辑
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    // 未登录：跳转到登录页
    if (!loginUser || loginUser.userRole === undefined || loginUser.userRole === null) {
      message.warning('请先登录')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }

    // 权限不足：跳转到无权限页面
    if (!checkAccess(loginUser.userRole, needAccess)) {
      message.error('没有权限访问该页面')
      next('/noAuth')
      return
    }
  }

  next()
})

// 全局后置守卫 - 更新当前选中菜单
router.afterEach((to) => {
  const routeKeyMap: Record<string, string> = {
    '/': '/',
    '/user/login': '/user/login',
    '/user/register': '/user/register',
    '/user/info': '/user/info',
    '/admin/userManage': '/admin/userManage',
  }
  ;(window as any).currentMenuKey = routeKeyMap[to.path] || '/'
})

export { ACCESS_ENUM }
export type { AccessEnumType }
export default router
