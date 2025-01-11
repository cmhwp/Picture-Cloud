import axios from 'axios'
import type { AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'
import type { BaseResponse, RequestConfig } from '@/types/request'

declare module 'axios' {
  export interface AxiosRequestConfig {
    requestConfig?: RequestConfig
  }
}

const DEFAULT_CONFIG: RequestConfig = {
  showMessage: true,
  loading: true,
}

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 60000,
  withCredentials: true,
})

let loadingCount = 0
const showLoading = () => {
  loadingCount++
  if (loadingCount === 1) {
    message.loading('加载中...', 0)
  }
}

const hideLoading = () => {
  loadingCount--
  if (loadingCount === 0) {
    message.destroy()
  }
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const requestConfig = (config.requestConfig || DEFAULT_CONFIG) as RequestConfig
    if (requestConfig.loading) {
      showLoading()
    }
    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<BaseResponse>) => {
    const requestConfig = (response.config.requestConfig || DEFAULT_CONFIG) as RequestConfig
    hideLoading()

    const { data } = response
    const { code, message: msg } = data

    // 处理成功响应
    if (code === 0) {
      // if (requestConfig.showMessage) {
      //   message.success(msg)
      // }
      return response
    }

    // 处理未登录状态
    if (code === 40100) {
      const isLoginRequest = response.config.url?.includes('user/get/login')
      const isLoginPage = window.location.pathname.includes('/user/login')

      if (!isLoginRequest && !isLoginPage) {
        message.warning('请先登录')
        const currentUrl = encodeURIComponent(window.location.href)
        window.location.href = `/login?redirect=${currentUrl}`
      }
      return Promise.reject(new Error(msg))
    }

    // 处理其他错误
    if (requestConfig.showMessage) {
      message.error(msg)
    }
    return Promise.reject(new Error(msg))
  },
  (error) => {
    hideLoading()
    message.error(error.message || '请求失败')
    return Promise.reject(error)
  },
)

export default request
