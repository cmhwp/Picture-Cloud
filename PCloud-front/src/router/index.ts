import { createRouter, createWebHistory } from 'vue-router'
import { UserRole } from '@/config/userConfig'
import { useLoginStore } from '@/stores'
import { message } from 'ant-design-vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
    },
    {
      path: '/user',
      children: [
        {
          path: '/login',
          name: 'userLogin',
          component: () => import('@/views/user/UserLoginView.vue'),
        },
        {
          path: '/register',
          name: 'userRegister',
          component: () => import('@/views/user/UserRegisterView.vue'),
        },
        {
          path: '/space',
          name: 'userSpace',
          component: () => import('@/views/user/UserSpaceView.vue'),
          meta: {
            role: UserRole.USER,
          },
        },
        {
          path: '/info',
          name: 'userInfo',
          component: () => import('@/views/user/UserInfoView.vue'),
          meta: {
            role: UserRole.USER,
          },
        },
      ],
    },
    {
      path: '/admin',
      children: [
        {
          path: '/userManage',
          name: 'userManage',
          component: () => import('@/views/admin/userManageView.vue'),
        },
      ],
      meta: {
        role: UserRole.ADMIN,
      },
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const loginStore = useLoginStore()
  const { loginUser } = loginStore

  // 白名单路由，无需登录即可访问
  const whiteList = ['/login', '/register',"/","/about"]

  // 如果访问的是白名单页面，直接放行
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 未登录，跳转到登录页
  if (!loginUser) {
    message.warning('请先登录')
    next(`/login?redirect=${to.path}`)
    return
  }

  // 检查路由是否需要特定角色
  if (to.meta.role) {
    // 如果用户角色不匹配，跳转到首页
    if (loginUser.userRole !== to.meta.role) {
      message.error('无权访问该页面')
      next('/')
      return
    }
  }

  next()
})

export default router
