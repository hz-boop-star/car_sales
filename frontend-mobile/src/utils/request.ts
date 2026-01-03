import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { showFailToast } from 'vant'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 生产环境使用环境变量中的 API 地址，开发环境使用代理
const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
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
        const res = response.data

        // 如果返回的状态码不是 0，说明有错误
        if (res.code !== 0) {
            showFailToast(res.message || '请求失败')

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


            switch (status) {
                case 401:
                    showFailToast('未授权，请重新登录')
                    const userStore = useUserStore()
                    userStore.logout()
                    router.push('/login')
                    break
                case 403:
                    showFailToast('权限不足')
                    break
                case 404:
                    showFailToast('请求的资源不存在')
                    break
                case 500:
                    showFailToast('服务器错误')
                    break
                default:
                    showFailToast(data?.message || '请求失败')
            }
        } else {
            showFailToast('网络错误，请检查网络连接')
        }

        return Promise.reject(error)
    }
)

export default service
