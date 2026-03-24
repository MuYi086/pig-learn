import request from '@/utils/request'
import type { LoginForm, LoginResult, UserInfo, ApiResponse } from '@/types/user'

// 登录
export function login(data: LoginForm) {
  return request.post<LoginResult>('/auth/oauth2/token', data, {
    headers: {
      Authorization: 'Basic cGlnOnBpZw=='
    }
  })
}

// 获取用户信息
export function getUserInfo() {
  return request.get<ApiResponse<UserInfo>>('/admin/user/info')
}

// 退出登录
export function logout() {
  return request.post('/auth/token/logout')
}

// 刷新 Token
export function refreshToken(refreshToken: string) {
  return request.post<LoginResult>('/auth/oauth2/token', {
    grant_type: 'refresh_token',
    refresh_token: refreshToken
  }, {
    headers: {
      Authorization: 'Basic cGlnOnBpZw=='
    }
  })
}

// 获取验证码
export function getCode() {
  return request.get('/code')
}
