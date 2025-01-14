import { h } from 'vue'
import { HomeOutlined, AppstoreOutlined, UserOutlined } from '@ant-design/icons-vue'
import type { MenuItem } from '@/types/menu'
import { UserRole } from '@/config/userConfig'

export const menuItems: MenuItem[] = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/about',
    icon: () => h(AppstoreOutlined),
    label: '关于',
    title: '关于',
  },
  {
    key: '/admin/userManage',
    icon: () => h(UserOutlined),
    label: '用户管理',
    title: '用户管理',
    role: UserRole.ADMIN,
  },
  {
    key: 'external',
    label: '外部链接',
    title: '外部链接',
    href: 'https://antdv.com',
    target: '_blank',
  },
]

// 根据用户角色过滤菜单
export const filterMenuByRole = (role?: string) => {
  // 未登录用户只能看到不需要角色的菜单项
  return menuItems.filter((item) => {
    // 如果菜单项没有设置 role，说明是公共页面，所有人都可以访问
    if (!item.role) {
      return true
    }
    // 如果用户有角色，且角色匹配，则可以访问
    if (role && item.role === role) {
      return true
    }
    return false
  })
}
