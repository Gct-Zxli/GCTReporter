import request from '@/utils/request'

/**
 * API统一响应格式
 */
export interface ApiResponse<T> {
  code: string
  message: string
  data: T
  timestamp: number
}

/**
 * 用户DTO
 */
export interface UserDTO {
  id: number
  username: string
  role: 'ADMIN' | 'DESIGNER' | 'VIEWER'
  enabled: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 创建用户请求
 */
export interface CreateUserRequest {
  username: string
  password: string
  role: 'ADMIN' | 'DESIGNER' | 'VIEWER'
  enabled?: boolean
}

/**
 * 更新用户请求
 */
export interface UpdateUserRequest {
  password?: string
  role?: 'ADMIN' | 'DESIGNER' | 'VIEWER'
  enabled?: boolean
}

/**
 * 用户管理API
 */
export const userApi = {
  /**
   * 获取所有用户
   */
  getAllUsers() {
    return request.get<ApiResponse<UserDTO[]>>('/api/v1/users')
  },

  /**
   * 根据ID获取用户
   */
  getUserById(id: number) {
    return request.get<ApiResponse<UserDTO>>(`/api/v1/users/${id}`)
  },

  /**
   * 创建用户
   */
  createUser(data: CreateUserRequest) {
    return request.post<ApiResponse<UserDTO>>('/api/v1/users', data)
  },

  /**
   * 更新用户
   */
  updateUser(id: number, data: UpdateUserRequest) {
    return request.put<ApiResponse<UserDTO>>(`/api/v1/users/${id}`, data)
  },

  /**
   * 删除用户
   */
  deleteUser(id: number) {
    return request.delete<ApiResponse<void>>(`/api/v1/users/${id}`)
  }
}
