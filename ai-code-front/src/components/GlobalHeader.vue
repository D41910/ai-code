<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Menu, Button } from 'ant-design-vue'
import type { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedKeys = ref<string[]>(['1'])

onMounted(() => {
  // 从window对象获取当前路由的key
  const currentKey = (window as any).currentMenuKey || '1'
  selectedKeys.value = [currentKey]
})

const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
  const routes: Record<string, string> = {
    '1': '/',
    '2': '/features',
    '3': '/about'
  }
  router.push(routes[key])
}

const menuItems: MenuProps['items'] = [
  { key: '1', label: '首页' },
  { key: '2', label: '功能' },
  { key: '3', label: '关于' },
]
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
      <a-button type="primary">登录</a-button>
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
