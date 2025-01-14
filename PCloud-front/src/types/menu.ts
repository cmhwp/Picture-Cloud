import type { VNode } from 'vue'

export interface MenuItem {
  key: string
  icon?: () => any
  label: string
  title: string
  href?: string
  target?: string
  role?: string
}
