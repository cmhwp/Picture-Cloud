import { createRouter, createWebHistory } from 'vue-router'

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
          path: '/info',
          name: 'userInfo',
          component: () => import('@/views/user/UserInfoView.vue'),
        },
      ]
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
    },
  ],
})

export default router
