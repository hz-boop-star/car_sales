import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

const service: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
    (config) => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        return config
    },
    (error) => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        // 如果是文件下载（blob），直接返回
        if (response.config.responseType === 'blob') {
            return response
        }

        const res = response.data

        // 如果返回的状态码不是 0，说明有错误
        if (res.code !== 0) {
            ElMessage.error(res.message || '请求失败')

            // 1002: Token 无效或过期
            if (res.code === 1002) {
                const userStore = useUserStore()
                userStore.logout()
                router.push('/login')
            }

            return Promise.reject(new Error(res.message || '请求失败'))
        }

        return res
    },
    (error) => {
        console.error('响应错误:', error)

        if (error.response) {
            const { status, data } = error.response

            if (status === 401) {
                ElMessage.error('未授权，请重新登录')
                const userStore = useUserStore()
                userStore.logout()
                router.push('/login')
            } else if (status === 403) {
                ElMessage.error('权限不足')
            } else if (status === 404) {
                ElMessage.error('请求的资源不存在')
            } else if (status === 500) {
                ElMessage.error('服务器错误')
            } else {
                ElMessage.error(data?.message || '请求失败')
            }
        } else {
            ElMessage.error('网络错误，请检查网络连接')
        }

        return Promise.reject(error)
    }
)

export default service
