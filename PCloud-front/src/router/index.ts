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
          path: 'login',
          name: 'userLogin',
          component: () => import('@/views/user/UserLoginView.vue'),
        },
        {
          path: 'register',
          name: 'userRegister',
          component: () => import('@/views/user/UserRegisterView.vue'),
        },
        {
          path: 'space',
          name: 'userSpace',
          component: () => import('@/views/user/UserSpaceView.vue'),
          meta: {
            roles: [UserRole.USER, UserRole.ADMIN],
          },
        },
        {
          path: 'info',
          name: 'userInfo',
          component: () => import('@/views/user/UserInfoView.vue'),
          meta: {
            roles: [UserRole.USER, UserRole.ADMIN],
          },
        },
      ],
    },
    {
      path: '/admin',
      children: [
        {
          path: 'userManage',
          name: 'userManage',
          component: () => import('@/views/admin/userManageView.vue'),
          meta: {
            roles: [UserRole.ADMIN],
          },
        },
      ],
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const loginStore = useLoginStore()
  const { loginUser } = loginStore

  // 白名单路由，无需登录即可访问
  const whiteList = ['/user/login', '/user/register', '/', '/about']

  // 如果已登录且要访问登录/注册页，重定向到首页
  if (loginUser.userName!=='未登录' && ['/user/login', '/user/register'].includes(to.path)) {
    next('/')
    return
  }

  // 如果访问的是白名单页面，直接放行
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 未登录，跳转到登录页
  if (!loginUser) {
    message.warning('请先登录')
    next({
      path: '/user/login',
      query: { redirect: to.fullPath },
    })
    return
  }

  // 检查路由是否需要特定角色
  const requiredRoles = to.meta.roles as string[]
  if (requiredRoles && loginUser?.userRole) {
    // 如果用户角色不在允许的角色列表中，拒绝访问
    if (!requiredRoles.includes(loginUser.userRole)) {
      message.error('无权访问该页面')
      next('/')
      return
    }
  }

  // 其他情况放行
  next()
})

export default router
