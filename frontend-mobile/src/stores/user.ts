import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types'

export const useUserStore = defineStore('user', () => {
    const token = ref<string>(localStorage.getItem('token') || '')
    const userInfo = ref<User | null>(
        localStorage.getItem('userInfo')
            ? JSON.parse(localStorage.getItem('userInfo')!)
            : null
    )

    function setToken(newToken: string) {
        token.value = newToken
        localStorage.setItem('token', newToken)
    }

    function setUserInfo(info: User) {
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
    }

    function logout() {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
    }

    function isAdmin() {
        return userInfo.value?.role === 'ADMIN'
    }

    return {
        token,
        userInfo,
        setToken,
        setUserInfo,
        logout,
        isAdmin
    }
})
