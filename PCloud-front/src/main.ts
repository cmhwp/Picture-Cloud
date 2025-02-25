import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import 'normalize.css'
import { useLoginStore } from '@/stores'

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Antd)

// 初始化用户状态
const initUserState = async () => {
  const loginStore = useLoginStore()
  const token = localStorage.getItem('satoken')

  if (token) {
    // 如果有token，尝试获取用户信息
    try {
      await loginStore.setLoginUser()
    } catch (error) {
      console.error('初始化用户状态失败:', error)
      // 如果获取用户信息失败，清除登录状态
      loginStore.clearLoginUser()
    }
  }
}

// 等待用户状态初始化完成后再挂载应用
initUserState().finally(() => {
  app.mount('#app')
})
