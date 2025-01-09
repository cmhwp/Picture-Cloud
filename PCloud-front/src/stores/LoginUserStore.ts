import { ref } from 'vue'
import { defineStore } from 'pinia'

interface LoginUser {
  username: string
  userId?: number
  avatar?: string
  email?: string
  token?: string
  isLogin: boolean
}

export const useLoginStore = defineStore('loginUser', () => {
  const loginUser = ref<LoginUser>({
    username: '未登录',
    isLogin: false,
  })

  const setLoginUser = (user: LoginUser) => {
    loginUser.value = {
      ...user,
      isLogin: true,
    }
  }

  const clearLoginUser = () => {
    loginUser.value = {
      username: '未登录',
      isLogin: false,
    }
  }

  const updateUserInfo = (userInfo: Partial<LoginUser>) => {
    loginUser.value = {
      ...loginUser.value,
      ...userInfo,
    }
  }

  return {
    loginUser,
    setLoginUser,
    clearLoginUser,
    updateUserInfo,
  }
})
