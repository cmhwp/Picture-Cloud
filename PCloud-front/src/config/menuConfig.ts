import { h } from 'vue'
import { HomeOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import type { MenuItem } from '@/types/menu'

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
    key: 'external',
    label: '外部链接',
    title: '外部链接',
    href: 'https://antdv.com',
    target: '_blank',
  },
]
