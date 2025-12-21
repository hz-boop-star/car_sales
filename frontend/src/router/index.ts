import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { requiresAuth: false, title: '登录' }
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
                meta: { title: '仪表盘', requiresAuth: true }
            },
            {
                path: 'cars',
                name: 'CarList',
                component: () => import('@/views/Car/CarList.vue'),
                meta: { title: '车辆管理', requiresAuth: true }
            },
            {
                path: 'customers',
                name: 'CustomerList',
                component: () => import('@/views/Customer/CustomerList.vue'),
                meta: { title: '客户管理', requiresAuth: true }
            },
            {
                path: 'orders',
                name: 'OrderList',
                component: () => import('@/views/Order/OrderList.vue'),
                meta: { title: '订单管理', requiresAuth: true }
            },
            {
                path: 'orders/create',
                name: 'CreateOrder',
                component: () => import('@/views/Order/CreateOrder.vue'),
                meta: { title: '创建订单', requiresAuth: true }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/dashboard'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()
    const requiresAuth = to.meta.requiresAuth !== false

    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 汽车销售管理系统`
    }

    // 需要认证的路由
    if (requiresAuth) {
        if (!userStore.token) {
            ElMessage.warning('请先登录')
            next('/login')
        } else {
            next()
        }
    } else {
        // 已登录用户访问登录页，重定向到首页
        if (to.path === '/login' && userStore.token) {
            next('/dashboard')
        } else {
            next()
        }
    }
})

export default router
