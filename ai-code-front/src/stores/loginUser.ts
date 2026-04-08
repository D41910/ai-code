import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUserVo } from '@/api/userController.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<API.UserVO>({
    userName: '未登录',
  })

  // 获取登录用户信息
  async function fetchLoginUser() {
    const res = await getLoginUserVo()
    if (res.data.code === 20000 && res.data.data) {
      loginUser.value = res.data.data
    }
  }
  // 更新登录用户信息
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
