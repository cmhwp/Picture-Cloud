import { ref } from 'vue'
import { defineStore } from 'pinia'

import type{ API } from '@/api/typings'
import { getLoginUserUsingGet, userLogoutUsingPost } from '@/api/userController'

export const useLoginStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVo>({
    userName: '未登录',
  })

  const setLoginUser = async () => {
    const res = await getLoginUserUsingGet()
    if (res.data?.code === 0 && res.data?.data) {
      loginUser.value = {
        ...res.data.data,
      }
    }
  }

  const clearLoginUser = async () => {
    const res = await userLogoutUsingPost()
    if (res.data?.code === 0) {
      loginUser.value = {
        userName: '未登录',
      }
    }
  }

  const updateUserInfo = (userInfo: Partial<API.LoginUserVo>) => {
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
