<script setup lang="ts">
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { userRegister } from '@/api/userController'

const router = useRouter()

const formState = reactive<API.UserRegisterDTO>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await userRegister(values)
  if (res.data.code === 20000) {
    message.success('注册成功')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}
</script>

<template>
  <div id="userRegisterPage">
    <div class="register-card">
      <h2 class="title">用户注册</h2>
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
        <a-form-item
          name="checkPassword"
          :rules="[
            { required: true, message: '请确认密码' },
            { min: 8, message: '密码不能小于 8 位' },
          ]"
        >
          <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
        </a-form-item>
        <div class="tips">
          已有账号？
          <RouterLink to="/user/login">去登录</RouterLink>
        </div>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<style scoped>
#userRegisterPage {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100vh;
  padding-right: 15%;
  background: url('/login.png') no-repeat center center;
  background-size: cover;
  position: relative;
}

#userRegisterPage::before {
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

.register-card {
  position: relative;
  z-index: 10;
  width: 360px;
  padding: 40px 32px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 8px;
}

.logo-container img {
  width: 60px;
  height: 60px;
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

:deep(.ant-input),
:deep(.ant-input-password) {
  border-radius: 8px;
  height: 40px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s;
}

:deep(.ant-input:focus),
:deep(.ant-input-password:focus) {
  border-color: #a855f7;
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.1);
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
