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
  withCredentials: false,
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

    // 获取token并添加到请求头
    const token = localStorage.getItem('satoken')
    if (token) {
      config.headers['satoken'] = `${token}`  // 直接使用token值
    }

    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<BaseResponse>) => {
    const requestConfig = (response.config.requestConfig || DEFAULT_CONFIG) as RequestConfig
    hideLoading()

    // 保存token
    const token = response.headers['satoken']
    if (token) {
      localStorage.setItem('satoken', token)
      // 立即更新当前请求的header
      axios.defaults.headers.common['satoken'] = token
    }

    const { data } = response
    const { code, message: msg } = data

    if (code === 0) {
      return response
    }

    // token过期或未登录
    if (code === 40100) {
      localStorage.removeItem('satoken') // 清除token
      const isLoginRequest = response.config.url?.includes('user/get/login')
      const isLoginPage = window.location.pathname.includes('/user/login')

      if (!isLoginRequest && !isLoginPage) {
        message.warning('请先登录')
        const currentUrl = encodeURIComponent(window.location.href)
        window.location.href = `/login?redirect=${currentUrl}`
      }
      return Promise.reject(new Error(msg))
    }

    if (requestConfig.showMessage) {
      message.error(msg)
    }
    return Promise.reject(new Error(msg))
  },
  (error) => {
    hideLoading()
    message.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default request
