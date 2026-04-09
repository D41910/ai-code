<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goLogin = () => {
  router.push('/user/login')
}
</script>

<template>
  <div class="noauth-container">
    <div class="noauth-content">
      <div class="noauth-icon">
        <svg
          width="120"
          height="120"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z"
            stroke="#ff4d4f"
            stroke-width="2"
          />
          <path
            d="M12 8V12"
            stroke="#ff4d4f"
            stroke-width="2"
            stroke-linecap="round"
          />
          <circle cx="12" cy="16" r="1" fill="#ff4d4f" />
        </svg>
      </div>
      <h1 class="noauth-title">403</h1>
      <h2 class="noauth-subtitle">暂无权限访问该页面</h2>
      <p class="noauth-desc">抱歉，您没有权限访问此页面，请联系管理员授权</p>
      <div class="noauth-actions">
        <a-button type="primary" size="large" @click="goHome">
          返回首页
        </a-button>
        <a-button size="large" @click="goLogin">
          前往登录
        </a-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.noauth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.noauth-content {
  text-align: center;
  padding: 48px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  max-width: 400px;
}

.noauth-icon {
  margin-bottom: 24px;
}

.noauth-title {
  font-size: 72px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1;
}

.noauth-subtitle {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.noauth-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.noauth-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}
</style>
