import type { VNode } from 'vue'

export interface MenuItem {
  key: string
  icon?: () => VNode
  label: string
  title: string
  href?: string
  target?: string
}
