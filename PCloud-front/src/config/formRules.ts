/**
 * 表单校验规则配置
 */
import type { Rule } from 'ant-design-vue/es/form'

// 用户名校验规则
export const userAccountRules: Rule[] = [
  { required: true, message: '请输入用户名' },
  { min: 4, message: '用户名至少4位' },
  { max: 20, message: '用户名最多20位' },
  {
    pattern: /^[a-zA-Z0-9_]+$/,
    message: '用户名只能包含字母、数字和下划线',
  },
]

// 密码校验规则
export const passwordRules: Rule[] = [
  { required: true, message: '请输入密码' },
  { min: 8, message: '密码至少8位' },
]

// 确认密码校验规则
export const checkPasswordRules = (formState: { userPassword?: string }): Rule[] => [
  { required: true, message: '请确认密码' },
  {
    validator: async (_: Rule, value: string) => {
      if (!value || formState.userPassword === value) {
        return Promise.resolve()
      }
      return Promise.reject(new Error('两次输入的密码不一致'))
    },
  },
]
