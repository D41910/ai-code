<script setup lang="ts">
import { reactive } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogin } from '@/api/userController'

const router = useRouter()
const route = useRoute()
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginDTO>({
  userAccount: '',
  userPassword: '',
})

const handleSubmit = async () => {
  try {
    const res = await userLogin(formState)
    if (res.data.code === 20000 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      // 登录后跳转到原访问页面或首页
      const redirect = (route.query.redirect as string) || '/'
      router.push({ path: redirect, replace: true })
    } else {
      message.error('登录失败，' + res.data.message)
    }
  } catch {
    message.error('系统异常')
  }
}
</script>

<template>
  <div id="userLoginPage">
    <div class="login-card">
      <h2 class="title">用户登录</h2>
      <div class="desc">不写一行代码，生成完整应用</div>
      <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
          <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
        </a-form-item>

        <a-form-item
          name="userPassword"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 8, message: '密码不能小于 8 位' },
          ]"
        >
          <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
        </a-form-item>

        <div class="tips">
          没有账号？
          <RouterLink to="/user/register">去注册</RouterLink>
        </div>

        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<style scoped>
#userLoginPage {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100vh;
  padding-right: 15%;
  background: url('/login.png') no-repeat center center;
  background-size: cover;
  position: relative;
}

#userLoginPage::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(2px);
  z-index: 0;
}

.login-card {
  position: relative;
  z-index: 10;
  width: 360px;
  padding: 40px 32px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 8px;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.desc {
  text-align: center;
  color: #666;
  margin-bottom: 24px;
  font-size: 13px;
}

.tips {
  margin-bottom: 8px;
  color: #666;
  font-size: 13px;
  text-align: right;
}

:deep(.ant-form-item) {
  margin-bottom: 16px;
}

/* 关键：去掉密码框内部嵌套的 input 层级错位 */
:deep(.ant-input-affix-wrapper),
:deep(.ant-input) {
  height: 40px;
  padding: 0 11px;
  font-size: 14px;
  border-radius: 6px;
}

:deep(.ant-input-password input) {
  height: 100%;
  padding: 0;
  border: none;
  outline: none;
}

/* 去掉聚焦高亮框 */
:deep(.ant-input:focus),
:deep(.ant-input-affix-wrapper:focus-within) {
  box-shadow: none;
  border-color: #d9d9d9;
  outline: none;
}

:deep(.ant-btn-primary) {
  height: 40px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(168, 85, 247, 0.3);
  transition: all 0.3s;
}

:deep(.ant-btn-primary:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(168, 85, 247, 0.4);
}
</style>
