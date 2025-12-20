// 通用响应类型
export interface Result<T = any> {
    code: number
    message: string
    data: T
    timestamp: number
}

// 分页响应
export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
    pages: number
}

// 用户类型
export interface User {
    id: number
    username: string
    realName: string
    role: 'ADMIN' | 'SALESPERSON'
    phone?: string
    email?: string
}

// 登录请求
export interface LoginRequest {
    username: string
    password: string
}

// 登录响应
export interface LoginResponse {
    token: string
    userInfo: User
}

// 车辆类型
export interface Car {
    id: number
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
export interface Customer {
    id: number
    name: string
    phone: string
    idCard: string
    gender?: 'M' | 'F'
    address?: string
    createTime: string
    updateTime: string
}

// 订单类型
export interface Order {
    id: number
    orderNo: string
    salesUserId: number
    customerId: number
    carId: number
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
export interface OrderDetail extends Order {
    salesUserName: string
    customerName: string
    customerPhone: string
    carBrand: string
    carModel: string
    carVin: string
}
