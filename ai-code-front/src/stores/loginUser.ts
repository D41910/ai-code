import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUserVo } from '@/api/userController'

/**
 * 登录用户数据类型
 */
export interface LoginUserVO {
  id?: number
  userName?: string
  userAccount?: string
  userAvatar?: string
  userProfile?: string
  userRole?: number // 0-普通用户 1-管理员
  createTime?: string
  updateTime?: string
}

export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<LoginUserVO>({
    id: undefined,
    userName: '未登录',
    userAccount: '',
    userAvatar: '',
    userProfile: '',
    userRole: undefined,
  })

  // 是否正在加载
  const loading = ref(false)

  // 获取登录用户信息
  async function fetchLoginUser() {
    loading.value = true
    try {
      const res = await getLoginUserVo()
      if (res.data.code === 20000 && res.data.data) {
        loginUser.value = res.data.data
      }
    } catch {
      // 获取失败，保持默认状态
    } finally {
      loading.value = false
    }
  }

  // 更新登录用户信息
  function setLoginUser(newLoginUser: LoginUserVO) {
    loginUser.value = newLoginUser
  }

  // 判断是否已登录
  function isLogin(): boolean {
    return !!loginUser.value.id
  }

  // 判断是否为管理员
  function isAdmin(): boolean {
    return loginUser.value.userRole === 1
  }

  return {
    loginUser,
    loading,
    fetchLoginUser,
    setLoginUser,
    isLogin,
    isAdmin,
  }
})
