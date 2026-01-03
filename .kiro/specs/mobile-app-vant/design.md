# 设计文档 - 汽车销售系统移动端改造

## 概述

本设计文档描述了将现有基于 Element UI 的汽车销售管理系统改造为移动端应用的技术方案。改造的核心目标是使用 Vant UI 组件库替换 Element Plus，实现移动端友好的界面，并确保打包成 APK 后能够正常显示和使用。

### 设计目标

1. **移动端适配**：使用 Vant UI 组件库实现移动端友好的界面
2. **API 复用**：完全复用现有后端 API，无需后端改动
3. **MVP 策略**：优先实现核心查询和展示功能
4. **性能优化**：支持下拉刷新、上拉加载等移动端交互
5. **离线友好**：使用 Capacitor 打包为原生 Android 应用

### 技术栈

- **前端框架**：Vue 3 + TypeScript + Composition API
- **UI 组件库**：Vant 4.x (移动端)
- **状态管理**：Pinia
- **路由管理**：Vue Router 4
- **HTTP 客户端**：Axios
- **打包工具**：Vite
- **移动端打包**：Capacitor 8.x
- **目标平台**：Android (APK)

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────┐
│         移动端应用 (Vue 3 + Vant)        │
├─────────────────────────────────────────┤
│  视图层 (Views)                          │
│  ├─ 登录页                               │
│  ├─ 首页 (Dashboard)                     │
│  ├─ 车辆管理 (列表/详情)                 │
│  ├─ 客户管理 (列表/详情)                 │
│  ├─ 订单管理 (列表/详情/创建)           │
│  └─ 个人中心                             │
├─────────────────────────────────────────┤
│  组件层 (Components)                     │
│  ├─ 底部导航栏                           │
│  ├─ 搜索表单                             │
│  ├─ 列表项                               │
│  └─ 详情卡片                             │
├─────────────────────────────────────────┤
│  状态管理 (Pinia Stores)                 │
│  ├─ userStore (用户状态)                 │
│  ├─ carStore (车辆数据)                  │
│  ├─ customerStore (客户数据)             │
│  └─ orderStore (订单数据)                │
├─────────────────────────────────────────┤
│  API 服务层 (Services)                   │
│  ├─ authService                          │
│  ├─ carService                           │
│  ├─ customerService                      │
│  ├─ orderService                         │
│  └─ statisticsService                    │
├─────────────────────────────────────────┤
│  工具层 (Utils)                          │
│  ├─ request.ts (Axios 封装)              │
│  ├─ storage.ts (本地存储)                │
│  └─ format.ts (格式化工具)               │
├─────────────────────────────────────────┤
│  路由守卫 (Router Guards)                │
│  └─ 认证检查、权限控制                   │
└─────────────────────────────────────────┘
              ↓ HTTP/HTTPS
┌─────────────────────────────────────────┐
│      现有后端 API (Spring Boot)          │
│  ├─ /api/auth/*                          │
│  ├─ /api/cars/*                          │
│  ├─ /api/customers/*                     │
│  ├─ /api/orders/*                        │
│  └─ /api/statistics/*                    │
└─────────────────────────────────────────┘
```

### 目录结构

```
frontend/
├── src/
│   ├── api/                    # API 服务层
│   │   ├── auth.ts
│   │   ├── car.ts
│   │   ├── customer.ts
│   │   ├── order.ts
│   │   └── statistics.ts
│   ├── components/             # 公共组件
│   │   ├── TabBar.vue         # 底部导航栏
│   │   ├── SearchBar.vue      # 搜索栏
│   │   └── EmptyState.vue     # 空状态
│   ├── views/                  # 页面视图
│   │   ├── login/
│   │   │   └── index.vue
│   │   ├── home/
│   │   │   └── index.vue
│   │   ├── car/
│   │   │   ├── list.vue
│   │   │   └── detail.vue
│   │   ├── customer/
│   │   │   ├── list.vue
│   │   │   └── detail.vue
│   │   ├── order/
│   │   │   ├── list.vue
│   │   │   ├── detail.vue
│   │   │   └── create.vue
│   │   └── profile/
│   │       └── index.vue
│   ├── stores/                 # Pinia 状态管理
│   │   ├── user.ts
│   │   ├── car.ts
│   │   ├── customer.ts
│   │   └── order.ts
│   ├── router/                 # 路由配置
│   │   └── index.ts
│   ├── utils/                  # 工具函数
│   │   ├── request.ts
│   │   ├── storage.ts
│   │   └── format.ts
│   ├── types/                  # TypeScript 类型定义
│   │   └── index.ts
│   ├── App.vue
│   └── main.ts
├── capacitor.config.ts         # Capacitor 配置
├── vite.config.ts              # Vite 配置
└── package.json
```

## 组件和接口设计

### 核心组件

#### 1. TabBar 组件 (底部导航栏)

**功能**：提供应用主要功能的快速入口

**接口**：
```typescript
interface TabBarProps {
  active: string  // 当前激活的标签
}

interface TabItem {
  name: string
  title: string
  icon: string
  path: string
}
```

**标签配置**：
- 首页 (home) - 图标: home-o
- 车辆 (car) - 图标: logistics
- 客户 (customer) - 图标: friends-o
- 订单 (order) - 图标: orders-o
- 我的 (profile) - 图标: user-o

#### 2. SearchBar 组件 (搜索栏)

**功能**：提供搜索和筛选功能

**接口**：
```typescript
interface SearchBarProps {
  placeholder?: string
  modelValue: string
}

interface SearchBarEmits {
  (e: 'update:modelValue', value: string): void
  (e: 'search'): void
  (e: 'clear'): void
}
```

#### 3. ListItem 组件 (列表项)

**功能**：展示车辆/客户/订单列表项

**接口**：
```typescript
interface ListItemProps {
  type: 'car' | 'customer' | 'order'
  data: Car | Customer | OrderDetail
}
```

### 页面组件

#### 1. 登录页 (LoginView)

**路由**：`/login`

**功能**：
- 用户名密码登录
- 表单验证
- 登录状态保持

**状态**：
```typescript
interface LoginState {
  username: string
  password: string
  loading: boolean
}
```

**API 调用**：
- `POST /api/auth/login`

#### 2. 首页 (HomeView)

**路由**：`/home`

**功能**：
- 显示统计数据卡片
- 下拉刷新

**状态**：
```typescript
interface DashboardData {
  totalCars: number
  availableCars: number
  soldCars: number
  totalRevenue: number
}
```

**API 调用**：
- `GET /api/statistics/dashboard`

#### 3. 车辆列表页 (CarListView)

**路由**：`/car/list`

**功能**：
- 分页列表展示
- 下拉刷新
- 上拉加载更多
- 搜索筛选（品牌、状态、价格区间）

**状态**：
```typescript
interface CarListState {
  list: Car[]
  loading: boolean
  refreshing: boolean
  finished: boolean
  page: number
  size: number
  total: number
  searchParams: {
    brand?: string
    status?: 0 | 1 | 2
    minPrice?: number
    maxPrice?: number
  }
}
```

**API 调用**：
- `GET /api/cars?page={page}&size={size}&brand={brand}&status={status}&minPrice={minPrice}&maxPrice={maxPrice}`

#### 4. 车辆详情页 (CarDetailView)

**路由**：`/car/detail/:id`

**功能**：
- 显示车辆完整信息
- 状态标签展示

**API 调用**：
- `GET /api/cars/{id}`

#### 5. 客户列表页 (CustomerListView)

**路由**：`/customer/list`

**功能**：
- 分页列表展示
- 下拉刷新
- 上拉加载更多
- 搜索（姓名模糊、手机号精确）

**状态**：
```typescript
interface CustomerListState {
  list: Customer[]
  loading: boolean
  refreshing: boolean
  finished: boolean
  page: number
  size: number
  total: number
  searchParams: {
    name?: string
    phone?: string
  }
}
```

**API 调用**：
- `GET /api/customers?page={page}&size={size}&name={name}&phone={phone}`

#### 6. 客户详情页 (CustomerDetailView)

**路由**：`/customer/detail/:id`

**功能**：
- 显示客户完整信息
- 点击手机号拨打电话

**API 调用**：
- `GET /api/customers/{id}`

#### 7. 订单列表页 (OrderListView)

**路由**：`/order/list`

**功能**：
- 分页列表展示
- 下拉刷新
- 上拉加载更多
- 搜索筛选（日期范围、销售员、状态）

**状态**：
```typescript
interface OrderListState {
  list: OrderDetail[]
  loading: boolean
  refreshing: boolean
  finished: boolean
  page: number
  size: number
  total: number
  searchParams: {
    startDate?: string
    endDate?: string
    salesUserId?: string
    status?: 1 | 2
  }
}
```

**API 调用**：
- `GET /api/orders?page={page}&size={size}&startDate={startDate}&endDate={endDate}&salesUserId={salesUserId}&status={status}`

#### 8. 订单详情页 (OrderDetailView)

**路由**：`/order/detail/:id`

**功能**：
- 显示订单完整信息
- 显示客户、车辆、价格信息

**API 调用**：
- `GET /api/orders/{id}`

#### 9. 订单创建页 (OrderCreateView)

**路由**：`/order/create`

**功能**：
- 选择客户（支持搜索）
- 选择车辆（仅在库状态）
- 输入优惠金额
- 自动计算成交价
- 选择订单日期
- 输入备注

**状态**：
```typescript
interface OrderCreateState {
  customerId: string
  customerName: string
  carId: string
  carInfo: string
  originalPrice: number
  discountAmount: number
  actualPrice: number
  orderDate: string
  remark: string
}
```

**API 调用**：
- `GET /api/customers?page=1&size=20&name={keyword}` (客户搜索)
- `GET /api/cars?page=1&size=20&status=0` (在库车辆)
- `POST /api/orders` (创建订单)

#### 10. 个人中心页 (ProfileView)

**路由**：`/profile`

**功能**：
- 显示用户信息
- 退出登录

**状态**：
```typescript
interface ProfileState {
  userInfo: User | null
  loading: boolean
}
```

**API 调用**：
- `GET /api/auth/info`
- `POST /api/auth/logout`

## 数据模型

### 复用现有类型定义

所有数据模型完全复用现有的 TypeScript 类型定义（`frontend/src/types/index.ts`）：

```typescript
// 用户类型
interface User {
  id: string
  username: string
  realName: string
  role: 'ADMIN' | 'SALESPERSON'
  phone?: string
  email?: string
}

// 车辆类型
interface Car {
  id: string
  vin: string
  brand: string
  model: string
  color?: string
  year?: number
  price: number
  status: 0 | 1 | 2  // 0-在库, 1-锁定, 2-已售
  purchaseDate?: string
  createTime: string
  updateTime: string
}

// 客户类型
interface Customer {
  id: string
  name: string
  phone: string
  idCard: string
  gender?: 'M' | 'F'
  address?: string
  createTime: string
  updateTime: string
}

// 订单类型
interface Order {
  id: string
  orderNo: string
  salesUserId: string
  customerId: string
  carId: string
  originalPrice: number
  actualPrice: number
  discountAmount: number
  orderDate: string
  status: 1 | 2  // 1-已完成, 2-已取消
  remark?: string
  createTime: string
  updateTime: string
}

// 订单详情
interface OrderDetail extends Order {
  salesUserName: string
  customerName: string
  customerPhone: string
  carBrand: string
  carModel: string
  carVin: string
}

// 通用响应类型
interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页响应
interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
```

## API 服务层设计

### 认证服务 (authService)

```typescript
// src/api/auth.ts
import request from '@/utils/request'
import type { LoginRequest, LoginResponse, User } from '@/types'

export const authApi = {
  // 登录
  login(data: LoginRequest) {
    return request.post<LoginResponse>('/auth/login', data)
  },
  
  // 登出
  logout(username?: string) {
    return request.post('/auth/logout', null, {
      params: { username }
    })
  },
  
  // 获取用户信息
  getUserInfo() {
    return request.get<LoginResponse>('/auth/info')
  },
  
  // 获取销售员列表
  getSalespersons() {
    return request.get<User[]>('/auth/salespersons')
  }
}
```

### 车辆服务 (carService)

```typescript
// src/api/car.ts
import request from '@/utils/request'
import type { Car, PageResult } from '@/types'

interface CarQueryParams {
  page: number
  size: number
  brand?: string
  status?: 0 | 1 | 2
  minPrice?: number
  maxPrice?: number
}

export const carApi = {
  // 查询车辆列表
  getCarList(params: CarQueryParams) {
    return request.get<PageResult<Car>>('/cars', { params })
  },
  
  // 获取车辆详情
  getCarDetail(id: string) {
    return request.get<Car>(`/cars/${id}`)
  }
}
```

### 客户服务 (customerService)

```typescript
// src/api/customer.ts
import request from '@/utils/request'
import type { Customer, PageResult } from '@/types'

interface CustomerQueryParams {
  page: number
  size: number
  name?: string
  phone?: string
}

export const customerApi = {
  // 查询客户列表
  getCustomerList(params: CustomerQueryParams) {
    return request.get<PageResult<Customer>>('/customers', { params })
  },
  
  // 获取客户详情
  getCustomerDetail(id: string) {
    return request.get<Customer>(`/customers/${id}`)
  }
}
```

### 订单服务 (orderService)

```typescript
// src/api/order.ts
import request from '@/utils/request'
import type { Order, OrderDetail, PageResult } from '@/types'

interface OrderQueryParams {
  page: number
  size: number
  startDate?: string
  endDate?: string
  salesUserId?: string
  status?: 1 | 2
}

interface OrderCreateRequest {
  customerId: string
  carId: string
  originalPrice: number
  discountAmount: number
  actualPrice: number
  orderDate: string
  remark?: string
}

export const orderApi = {
  // 查询订单列表
  getOrderList(params: OrderQueryParams) {
    return request.get<PageResult<OrderDetail>>('/orders', { params })
  },
  
  // 获取订单详情
  getOrderDetail(id: string) {
    return request.get<OrderDetail>(`/orders/${id}`)
  },
  
  // 创建订单
  createOrder(data: OrderCreateRequest) {
    return request.post<Order>('/orders', data)
  }
}
```

### 统计服务 (statisticsService)

```typescript
// src/api/statistics.ts
import request from '@/utils/request'

interface DashboardData {
  totalCars: number
  availableCars: number
  soldCars: number
  totalRevenue: number
}

export const statisticsApi = {
  // 获取仪表盘数据
  getDashboard() {
    return request.get<DashboardData>('/statistics/dashboard')
  }
}
```

## 状态管理设计

### 用户状态 (userStore)

**功能**：管理用户登录状态和信息

```typescript
// src/stores/user.ts
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
```

## 路由设计

### 路由配置

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
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
router.beforeEach((to, from, next) => {
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
```

## UI 设计规范

### Vant 组件使用

#### 布局组件
- `van-nav-bar`: 顶部导航栏
- `van-tabbar`: 底部导航栏
- `van-cell`: 单元格（用于详情页）
- `van-card`: 卡片（用于列表项）

#### 表单组件
- `van-form`: 表单容器
- `van-field`: 输入框
- `van-button`: 按钮
- `van-picker`: 选择器
- `van-datetime-picker`: 日期时间选择器
- `van-search`: 搜索框

#### 反馈组件
- `van-loading`: 加载状态
- `van-empty`: 空状态
- `van-toast`: 轻提示
- `van-dialog`: 对话框
- `van-pull-refresh`: 下拉刷新
- `van-list`: 列表（支持上拉加载）

#### 展示组件
- `van-tag`: 标签
- `van-divider`: 分割线
- `van-grid`: 宫格（用于首页统计）

### 颜色规范

```css
/* 主题色 */
--van-primary-color: #1989fa;
--van-success-color: #07c160;
--van-warning-color: #ff976a;
--van-danger-color: #ee0a24;

/* 状态色 */
--status-available: #07c160;  /* 在库 */
--status-locked: #ff976a;     /* 锁定 */
--status-sold: #ee0a24;       /* 已售 */
--status-completed: #07c160;  /* 已完成 */
--status-cancelled: #969799;  /* 已取消 */
```

### 响应式设计

- 使用 `viewport` 单位和百分比布局
- 最小支持宽度：320px
- 推荐设计稿宽度：375px
- 使用 Vant 的 `rem` 适配方案

```typescript
// vite.config.ts 配置 postcss-pxtorem
import postcssPxtorem from 'postcss-pxtorem'

export default {
  css: {
    postcss: {
      plugins: [
        postcssPxtorem({
          rootValue: 37.5,
          propList: ['*']
        })
      ]
    }
  }
}
```

## 错误处理设计

### HTTP 请求错误处理

在 `request.ts` 中统一处理：

```typescript
// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    if (res.code !== 0) {
      showToast(res.message || '请求失败')
      
      // Token 无效
      if (res.code === 1002) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message))
    }
    
    return res
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      
      switch (status) {
        case 401:
          showToast('未授权，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          showToast('权限不足')
          break
        case 404:
          showToast('请求的资源不存在')
          break
        case 500:
          showToast('服务器错误')
          break
        default:
          showToast('请求失败')
      }
    } else {
      showToast('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)
```

### 页面级错误处理

每个页面组件应包含：

1. **加载状态**：使用 `van-loading` 或骨架屏
2. **空状态**：使用 `van-empty` 展示无数据状态
3. **错误状态**：显示错误信息和重试按钮

```typescript
interface PageState {
  loading: boolean
  error: string | null
  data: any
}

// 错误处理示例
async function loadData() {
  state.loading = true
  state.error = null
  
  try {
    const res = await api.getData()
    state.data = res.data
  } catch (error) {
    state.error = error.message
  } finally {
    state.loading = false
  }
}
```

## 性能优化设计

### 列表优化

1. **虚拟滚动**：对于超长列表，考虑使用虚拟滚动
2. **分页加载**：使用 `van-list` 组件实现上拉加载
3. **下拉刷新**：使用 `van-pull-refresh` 组件

### 图片优化

1. **懒加载**：使用 `van-lazyload` 指令
2. **占位图**：设置默认占位图
3. **压缩**：后端返回压缩后的图片

### 路由优化

1. **懒加载**：所有路由组件使用动态导入
2. **预加载**：关键路由可以预加载
3. **缓存**：使用 `keep-alive` 缓存列表页状态

```vue
<template>
  <router-view v-slot="{ Component }">
    <keep-alive :include="['CarList', 'CustomerList', 'OrderList']">
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>
```

## 测试策略

### 单元测试

**测试范围**：
- API 服务层函数
- 工具函数（格式化、验证等）
- Pinia Store 的状态管理逻辑

**测试工具**：
- Vitest
- @vue/test-utils

**示例**：
```typescript
// authService.test.ts
import { describe, it, expect, vi } from 'vitest'
import { authApi } from '@/api/auth'

describe('authApi', () => {
  it('should login successfully', async () => {
    const mockResponse = {
      code: 0,
      data: {
        token: 'mock-token',
        userInfo: { id: '1', username: 'test' }
      }
    }
    
    vi.spyOn(request, 'post').mockResolvedValue(mockResponse)
    
    const result = await authApi.login({
      username: 'test',
      password: '123456'
    })
    
    expect(result.data.token).toBe('mock-token')
  })
})
```

### 集成测试

**测试范围**：
- 页面组件的完整交互流程
- 路由跳转
- 状态管理集成

**示例场景**：
- 登录流程：输入用户名密码 → 点击登录 → 跳转首页
- 列表加载：进入列表页 → 加载数据 → 下拉刷新 → 上拉加载
- 订单创建：选择客户 → 选择车辆 → 填写信息 → 提交订单

### 端到端测试

**测试范围**：
- 关键业务流程的完整测试
- 在真实设备或模拟器上测试

**测试工具**：
- Playwright 或 Cypress

**示例场景**：
- 完整的订单创建流程
- 从登录到查看订单详情的完整流程

## 部署和打包

### 开发环境配置

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 访问地址
http://localhost:5173
```

### 生产环境打包

```bash
# 构建生产版本
pnpm build

# 同步到 Capacitor
pnpm cap:sync

# 打开 Android Studio
pnpm cap:open
```

### Capacitor 配置

```typescript
// capacitor.config.ts
import { CapacitorConfig } from '@capacitor/cli'

const config: CapacitorConfig = {
  appId: 'com.carsales.app',
  appName: '汽车销售系统',
  webDir: 'dist',
  server: {
    // 生产环境配置
    url: 'https://your-api-domain.com',
    cleartext: false
  },
  android: {
    allowMixedContent: false
  }
}

export default config
```

### 环境变量配置

```bash
# .env.production
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## 安全考虑

### 认证和授权

1. **Token 存储**：使用 localStorage 存储 JWT token
2. **Token 刷新**：Token 过期后自动跳转登录页
3. **请求拦截**：所有请求自动携带 Authorization header

### 数据验证

1. **前端验证**：使用 Vant 的表单验证
2. **后端验证**：依赖后端的数据验证
3. **XSS 防护**：Vue 3 自动转义输出

### HTTPS

1. **生产环境**：必须使用 HTTPS
2. **证书验证**：确保 SSL 证书有效

## 兼容性

### Android 版本支持

- 最低支持：Android 5.0 (API 21)
- 推荐版本：Android 8.0+ (API 26+)

### 浏览器内核

- Capacitor 使用系统 WebView
- Android 5.0+ 使用 Chrome WebView

### 屏幕适配

- 支持屏幕尺寸：4.0 英寸 - 7.0 英寸
- 支持分辨率：320x480 - 1440x2960
- 支持方向：竖屏为主，横屏自适应

## 总结

本设计文档详细描述了汽车销售系统移动端改造的技术方案。通过使用 Vant UI 组件库和 Capacitor 打包工具，我们可以快速实现一个移动端友好的应用，并打包成 APK 在 Android 设备上运行。

设计的核心原则是：
1. 完全复用现有后端 API
2. 使用成熟的移动端 UI 组件库
3. 采用 MVP 策略，优先实现核心功能
4. 注重移动端用户体验（下拉刷新、上拉加载等）
5. 确保代码质量和可维护性
