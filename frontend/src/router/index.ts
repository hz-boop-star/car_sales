import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('@/components/Layout.vue'),
        redirect: '/dashboard',
        meta: { requiresAuth: true },
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
                meta: { title: '仪表盘' }
            },
            {
                path: 'cars',
                name: 'CarList',
                component: () => import('@/views/Car/CarList.vue'),
                meta: { title: '车辆管理' }
            },
            {
                path: 'customers',
                name: 'CustomerList',
                component: () => import('@/views/Customer/CustomerList.vue'),
                meta: { title: '客户管理' }
            },
            {
                path: 'orders',
                name: 'OrderList',
                component: () => import('@/views/Order/OrderList.vue'),
                meta: { title: '订单管理' }
            },
            {
                path: 'orders/create',
                name: 'CreateOrder',
                component: () => import('@/views/Order/CreateOrder.vue'),
                meta: { title: '创建订单' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    if (to.meta.requiresAuth !== false && !userStore.token) {
        next('/login')
    } else if (to.path === '/login' && userStore.token) {
        next('/dashboard')
    } else {
        next()
    }
})

export default router
