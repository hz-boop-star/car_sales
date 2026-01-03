import request from '@/utils/request'
import type { LoginRequest, LoginResponse, User } from '@/types'

/**
 * 认证相关 API
 */
export const authApi = {
    /**
     * 用户登录
     */
    login(data: LoginRequest) {
        return request.post<LoginResponse>('/auth/login', data)
    },

    /**
     * 用户登出
     */
    logout(username?: string) {
        return request.post('/auth/logout', null, {
            params: { username }
        })
    },

    /**
     * 获取当前用户信息
     */
    getUserInfo() {
        return request.get<LoginResponse>('/auth/info')
    },

    /**
     * 获取销售员列表
     */
    getSalespersons() {
        return request.get<User[]>('/auth/salespersons')
    }
}
