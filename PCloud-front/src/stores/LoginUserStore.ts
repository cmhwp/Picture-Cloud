import { ref } from 'vue'
import { defineStore } from 'pinia'

import type { API } from '@/api/typings'
import { getLoginUserUsingGet, userLogoutUsingPost } from '@/api/userController'

// 从 localStorage 获取默认用户信息
const getDefaultUser = () => {
  const savedUser = localStorage.getItem('loginUser')
  return savedUser ? JSON.parse(savedUser) : {
    userName: '未登录',
  }
}

export const useLoginStore = defineStore('login', () => {
  const loginUser = ref<API.LoginUserVO>(getDefaultUser())

  const setLoginUser = async () => {
    try {
      const res = await getLoginUserUsingGet()
      if (res.data?.code === 0 && res.data?.data) {
        loginUser.value = res.data.data
        // 保存到 localStorage
        localStorage.setItem('loginUser', JSON.stringify(res.data.data))
      } else {
        clearLoginUser()
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      clearLoginUser()
    }
  }

  const clearLoginUser = async () => {
    try {
      await userLogoutUsingPost()
      loginUser.value = { userName: '未登录' }
      // 清除 localStorage
      localStorage.removeItem('loginUser')
    } catch (error) {
      console.error('退出登录失败:', error)
      loginUser.value = { userName: '未登录' }
      localStorage.removeItem('loginUser')
    }
  }

  const updateUserInfo = (userInfo: Partial<API.LoginUserVO>) => {
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
