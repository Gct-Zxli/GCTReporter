import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  (error) => {
    // 对于特定的401错误（Session过期），自动跳转到登录页
    if (error.response?.status === 401 && error.response?.data?.code === 'SESSION_EXPIRED') {
      ElMessage.warning('登录已过期，请重新登录')
      localStorage.clear()
      window.location.href = '/login'
    }
    
    // 将错误抛出，让调用方自行处理
    return Promise.reject(error)
  }
)

export default request
