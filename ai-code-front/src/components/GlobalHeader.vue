<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Menu, Button, Dropdown, Space, Avatar, message } from 'ant-design-vue'
import type { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController'
import { LogoutOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const selectedKeys = ref<string[]>(['1'])
const loginUserStore = useLoginUserStore()

onMounted(() => {
  // 从window对象获取当前路由的key
  const currentKey = (window as any).currentMenuKey || '1'
  selectedKeys.value = [currentKey]
})

const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
  const routes: Record<string, string> = {
    '1': '/',
    '2': '/user/login',
    '3': '/user/register',
    '4': '/admin/userManage'
  }
  router.push(routes[key])
}

const menuItems: MenuProps['items'] = [
  { key: '1', label: '首页' },
]

// 用户注销
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 20000) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<template>
  <a-layout-header class="global-header">
    <div class="header-left">
      <img src="/ling.png" alt="logo" class="logo" />
      <span class="site-title">灵创智能应用</span>
    </div>
    <div class="header-center">
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        :items="menuItems"
        @click="handleMenuClick"
      />
    </div>
    <div class="header-right">
      <div v-if="loginUserStore.loginUser.id">
        <a-dropdown>
          <a-space>
            <a-avatar :src="loginUserStore.loginUser.userAvatar" />
            {{ loginUserStore.loginUser.userName ?? '无名' }}
          </a-space>
          <template #overlay>
            <a-menu>
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
    </div>
  </a-layout-header>
</template>

<style scoped>
.global-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 32px;
  height: 32px;
}

.site-title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-center :deep(.ant-menu) {
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
}
</style>
