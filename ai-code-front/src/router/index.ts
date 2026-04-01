import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/features',
      name: 'features',
      component: () => import('../pages/FeaturesPage.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../pages/AboutPage.vue'),
    },
  ],
})

// 存储当前路由的key
router.afterEach((to) => {
  const routeKeyMap: Record<string, string> = {
    '/': '1',
    '/features': '2',
    '/about': '3',
  }

  // 将当前路由的key存储在window对象上，供GlobalHeader组件访问
  (window as any).currentMenuKey = routeKeyMap[to.path] || '1'
})

export default router
