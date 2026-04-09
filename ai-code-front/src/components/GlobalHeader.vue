<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout } from '@/api/userController'
import { LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { ACCESS_ENUM } from '@/router'

type AccessEnumType = (typeof ACCESS_ENUM)[keyof typeof ACCESS_ENUM]

// 检查权限
const checkAccess = (userRole: number | undefined, needAccess: AccessEnumType): boolean => {
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) return true
  if (!userRole) return false
  const loginUserAccess: AccessEnumType = userRole === 1 ? ACCESS_ENUM.ADMIN : ACCESS_ENUM.USER
  if (needAccess === ACCESS_ENUM.USER) {
    return loginUserAccess !== ACCESS_ENUM.NOT_LOGIN
  }
  if (needAccess === ACCESS_ENUM.ADMIN) {
    return loginUserAccess === ACCESS_ENUM.ADMIN
  }
  return true
}

const router = useRouter()
const loginUserStore = useLoginUserStore()
const selectedKeys = ref<string[]>(['/'])

router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

// 原始菜单配置
const originMenuItems = [
  { key: '/', label: '首页', access: ACCESS_ENUM.NOT_LOGIN as AccessEnumType },
  { key: '/admin/userManage', label: '用户管理', access: ACCESS_ENUM.ADMIN as AccessEnumType },
]

// 过滤菜单项
const menuItems = computed<MenuProps['items']>(() => {
  return originMenuItems
    .filter((item) => checkAccess(loginUserStore.loginUser.userRole, item.access))
    .map((item) => ({ key: item.key, label: item.label }))
})

// 菜单点击处理
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  try {
    const res = await userLogout()
    if (res.data.code === 20000) {
      loginUserStore.setLoginUser({ userName: '未登录', userRole: undefined })
      await router.push('/user/login')
    }
  } catch {
    // 退出失败
  }
}
</script>

<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="/ling.png" alt="Logo" />
            <h1 class="site-title">灵创智能应用</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status" v-if="loginUserStore.isLogin()">
          <a-dropdown>
            <a-space>
              <a-avatar :src="loginUserStore.loginUser.userAvatar">
                {{ loginUserStore.loginUser.userName?.[0] || '无' }}
              </a-avatar>
              <span class="username">{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item key="userInfo" @click="router.push('/user/info')">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<style scoped>
.header {
  background: #fff;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 40px;
  width: 40px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #1890ff;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}

.username {
  color: #333;
  font-size: 14px;
}
</style>
