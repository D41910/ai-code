import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: () => import('../pages/user/UserLoginPage.vue'),
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: () => import('../pages/user/UserRegisterPage.vue'),
    },
    {
      path: '/user/info',
      name: '个人中心',
      component: () => import('../pages/user/UserInfoPage.vue'),
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: () => import('../pages/admin/UserManagePage.vue'),
    },
  ],
})

// 存储当前路由的key
router.afterEach((to) => {
  const routeKeyMap: Record<string, string> = {
    '/': '1',
    '/user/login': '2',
    '/user/register': '3',
    '/admin/userManage': '4',
  }

  // 将当前路由的key存储在window对象上，供GlobalHeader组件访问
  (window as any).currentMenuKey = routeKeyMap[to.path] || '1'
})

export default router
