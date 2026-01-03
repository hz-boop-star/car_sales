import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { requiresAuth: true, showTabBar: true }
    },
    {
        path: '/car/list',
        name: 'CarList',
        component: () => import('@/views/car/list.vue'),
        meta: { requiresAuth: true, showTabBar: true }
    },
    {
        path: '/car/detail/:id',
        name: 'CarDetail',
        component: () => import('@/views/car/detail.vue'),
        meta: { requiresAuth: true, showTabBar: false }
    },
    {
        path: '/customer/list',
        name: 'CustomerList',
        component: () => import('@/views/customer/list.vue'),
        meta: { requiresAuth: true, showTabBar: true }
    },
    {
        path: '/customer/detail/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/customer/detail.vue'),
        meta: { requiresAuth: true, showTabBar: false }
    },
    {
        path: '/order/list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { requiresAuth: true, showTabBar: true }
    },
    {
        path: '/order/detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: { requiresAuth: true, showTabBar: false }
    },
    {
        path: '/order/create',
        name: 'OrderCreate',
        component: () => import('@/views/order/create.vue'),
        meta: { requiresAuth: true, showTabBar: false }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { requiresAuth: true, showTabBar: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()

    if (to.meta.requiresAuth && !userStore.token) {
        next('/login')
    } else if (to.path === '/login' && userStore.token) {
        next('/home')
    } else {
        next()
    }
})

export default router
