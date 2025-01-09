export interface BaseResponse<T = unknown> {
  code: number
  data: T
  message: string
}

export interface RequestConfig {
  showMessage?: boolean // 是否显示消息提示
  loading?: boolean // 是否显示加载中
}
