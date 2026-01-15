import request from '@/utils/request'

// 登录请求接口
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应接口
export interface LoginResponse {
  token: string
  userId: number
  username: string
  role: 'ADMIN' | 'DESIGNER' | 'VIEWER'
}

// 认证 API
export const authApi = {
  // 用户登录
  login: (data: LoginRequest) =>
    request.post<LoginResponse>('/api/v1/auth/login', data),

  // 用户登出
  logout: () =>
    request.post('/api/v1/auth/logout'),

  // 获取当前用户信息
  getCurrentUser: () =>
    request.get('/api/v1/auth/current')
}
