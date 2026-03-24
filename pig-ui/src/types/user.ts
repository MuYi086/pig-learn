// 用户相关类型
export interface LoginForm {
  username: string
  password: string
  code: string
  randomStr: string
}

export interface UserInfo {
  userId: string
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  roles: string[]
  permissions: string[]
}

export interface LoginResult {
  access_token: string
  refresh_token: string
  expires_in: number
  license: string
}

// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 分页类型
export interface PageQuery {
  current?: number
  size?: number
}

export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
